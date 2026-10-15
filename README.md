![MRP 物料需求计划系统](assets/cover.svg)

# MRP 物料需求计划系统

基于 Java 8、Struts2 和 Hibernate 的物料需求计划系统。维护物料、BOM 与库存，输入顶层物料的分期计划下达量，按 BOM 拓扑顺序展开需求，使用 0～12 共 13 个期段槽保存需求、预计库存与计划订单，并单独保留负期段的下达量和原始逾期需求。

计算逻辑与数据库访问分开，适合阅读多层 BOM 的需求传递，并用不依赖数据库的计算测试核对规则。页面使用 JSP 与 Bootstrap，数据库使用 MySQL。

## 模块与计算过程

| 模块 | 当前作用 | 阅读入口 |
| --- | --- | --- |
| 物料、BOM、库存 | 维护物料属性、父子关系、单位用量及库存输入 | [实体与映射](src/entity/) |
| TPOP | 输入顶层物料各期的 `POR0`～`POR12` 计划下达量 | [TPOPAction.java](src/action/TPOPAction.java) |
| 多层展开 | 校验 BOM 和 LLC，按拓扑顺序累加所有父项需求 | [BomGraph.java](src/service/BomGraph.java) · [MrpCalculationService.java](src/service/MrpCalculationService.java) |
| 期段计算 | 使用库存、已分配量、安全库存、提前期与批量规则计算各期结果 | [MrpPeriodCalculator.java](src/util/MrpPeriodCalculator.java) |
| 应用入口 | 在单个事务中读取输入、计算并替换完整批次 | [MrpRecalculationService.java](src/service/MrpRecalculationService.java) · [MRPAction.java](src/action/MRPAction.java) |

```text
物料 + BOM + 库存 + 顶层计划下达量
  → BOM 校验与拓扑排序 → 汇总物料毛需求
  → 净需求与计划接收量 → 按提前期移位得到计划下达量
  → 传递到子项 → 原子替换整批结果
```

TPOP 当前直接接收计划下达量，不是从独立订单需求自动生成主生产计划的模块。

## 数据库与运行前提

完整应用需要 JDK 8、Tomcat 与 MySQL。在独立的示例数据库中初始化表，不要对已有业务库直接执行初始化脚本：

1. 先自行创建并选中示例库，例如 `mrp_demo`。
2. 导入 [mrp.sql](mrp.sql)。它包含 `DROP TABLE` / `CREATE TABLE`，不会自动创建或选中数据库。
3. 为运行 Tomcat 的进程设置数据库配置；缺少必要配置会明确报错。

```powershell
$env:MRP_DB_URL = "jdbc:mysql://localhost:3306/mrp_demo?useUnicode=true&characterEncoding=UTF-8"
$env:MRP_DB_USERNAME = "mrp_user"
$env:MRP_DB_PASSWORD = "填入本地示例库密码"
```

可选环境变量及对应 JVM 参数见 [hibernate.properties.example](src/hibernate.properties.example)，包括驱动、方言、建表模式与 SQL 日志设置。这里的环境变量需要由实际启动 Tomcat 的进程继承。

[V1 基线](db/migration/V1__legacy_schema.sql) 与 `mrp.sql` 相同，二选一即可。随后执行一次 [V3](db/migration/V3__atomic_batches_and_past_due.sql)，增加批次互斥行和期外需求表。[V2](db/migration/V2__period_line_tables.sql) 是尚未启用的规范化期段表设计，本版本无需执行；仓库没有自动执行 Flyway 迁移的配置。

## 构建与验证

需要 JDK 8 与 Maven 3.9；在仓库根目录执行：

```powershell
powershell -NoProfile -File scripts\verify.ps1
```

[验证脚本](scripts/verify.ps1) 统一执行 `mvn -B clean verify`，任一编译、测试或打包失败都会终止并返回非零。Maven 和 JDK 的 `jar` 工具缺失也会明确失败。

- [MrpPeriodCalculatorTest](test/util/MrpPeriodCalculatorTest.java)：期段计算规则。
- [MrpCalculationServiceTest](test/service/MrpCalculationServiceTest.java)：物料之间的需求展开。

成功产物为 `target/mrp-1.0.0-SNAPSHOT.war`。脚本检查其中的应用类、Hibernate/Struts 配置以及运行依赖；运行依赖来自 Maven，不重复打入旧 `web/WEB-INF/lib` 中的 jar。

有 Python 3 时，可在独立副本中验证编译、计算断言、打包和缺少 Maven 四种失败路径：

```powershell
python scripts\check_verify_failures.py
```

完整集成测试只连接专用测试 MySQL 服务器，随机创建 `mrp_test_` 前缀数据库并在结束时清理；不要填写已有业务服务器地址。服务端用户需要创建、删除测试库的权限：

```powershell
$env:MRP_TEST_MYSQL_URL = "jdbc:mysql://127.0.0.1:33317/"
$env:MRP_TEST_MYSQL_USER = "test_admin"
$env:MRP_TEST_MYSQL_PASSWORD = "测试服务器密码"
powershell -NoProfile -File scripts\verify.ps1 -Integration
```

[集成测试](test/service/MrpWorkflowIT.java) 执行真实 MySQL 回滚、并发重算和映射验证，再以单独 JVM 启动 Tomcat 9、部署实际 WAR 并访问计算页面。测试进程的业务依赖不会为 WAR 补齐缺失类。[CI](.github/workflows/ci.yml) 分别验证 Windows 构建/失败退出码与 Linux MySQL/Tomcat 部署。

## 计算边界

- 当前固定使用 13 个期段槽。第 0 期的预计库存、预计可用库存和净需求用 `-1` 占位；第 0 期的毛需求与预计入库量并入第 1 期计算，常规计算覆盖第 1～12 期。它不是任意长度的滚动计划。
- FOQ 按固定批量的整数倍向上补足。例如净需求 25、批量 10，接收量为 30；LFL 等于净需求。负数量、非法规则、FOQ 零批量、负提前期和算术溢出都明确失败。
- 提前期与安全时间之和决定下达期段。负期段保留在 `pastDueReleases` 并写入数据库；子项累加这些需求到第 0 槽，同时在 `pastDueGrossRequirements` 中保留原始逾期期段。它们表示需要处置的逾期量，并不表示按时满足。
- BOM 必须无环、无重复边，物料必须存在且单位用量为正。LLC 必须等于该物料从任意根节点出发的最长路径层数；实际展开顺序来自图关系。顶层 TPOP、组件库存和重复记录缺失/冲突会报错，不补成零需求或零库存。
- 重算使用 InnoDB、`REPEATABLE_READ` 和数据库互斥行锁。一次计算读取同一个输入快照，提交前任何异常都会回滚全部结果；并发重算按锁顺序提交。重算期间发生的输入编辑由下一次计算的快照读取。

手算例、实际验收结果和环境边界见 [优化验证记录](docs/optimization-validation.md)。
