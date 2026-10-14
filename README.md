# MRP 物料需求计划系统

基于 Struts2 + Hibernate 的物料需求计划（MRP, Material Requirements Planning）管理系统：维护 BOM、库存与物料主数据，按低层码（LLC）逐层计算毛需求、净需求、计划订单与需求时间，输出各期 MRP 结果。

## 模块

| 模块 | 说明 |
| --- | --- |
| 物料主数据（ItemMaster） | 物料编号、提前期、低层码（LLC）等基础属性 |
| BOM | 父项-子项结构与单位用量 |
| 库存（Inventory） | 现有库存与可用量 |
| 主生产计划（TPOP） | 各期独立需求输入 |
| MRP 计算 | 按 LLC 自顶向下展开：毛需求 → 净需求 → 计划订单 → 需求时间 |
| 页面 | JSP + Bootstrap 管理界面 |

## 运行

需要 JDK 8、Tomcat 与 MySQL。数据库连接通过环境变量注入，不写入配置文件：

```powershell
$env:MRP_DB_URL = "jdbc:mysql://localhost:3306/MRP?useUnicode=true&characterEncoding=UTF-8"
$env:MRP_DB_USERNAME = "mrp_user"
$env:MRP_DB_PASSWORD = "change-me"
```

可选设置：`MRP_DB_DRIVER`、`MRP_DB_DIALECT`、`MRP_HBM2DDL_AUTO`、`MRP_HIBERNATE_SHOW_SQL`、`MRP_HIBERNATE_FORMAT_SQL`，也支持 `-Dmrp.db.*` JVM 参数，见 `src/hibernate.properties.example`。

数据库初始化：`mrp.sql` 为基线结构；`db/migration/` 是 Flyway 风格的增量迁移脚本（V2 为库存、TPOP 与 MRP 结果引入规范化的期段表，旧固定列结构保留以兼容现有页面）。

## 构建与验证

```powershell
powershell -ExecutionPolicy Bypass -File scripts\verify.ps1
```

脚本用 `web\WEB-INF\lib` 中的依赖编译全部源码，并运行两个单元测试（期段计算、MRP 计算服务）；检测到 Maven 时额外执行 `mvn -B -DskipTests package` 打包 WAR。CI（`.github/workflows/ci.yml`）在 Windows 上重复同一套验证。

Struts 固定在 2.3.37（现有 `web.xml` 使用 2.3 版 dispatcher）；`pom.xml` 负责 WAR 打包，打包时排除 `WEB-INF/lib` 与编译产物。

## 说明

- 技术栈为 Java 8 / Struts2 / Hibernate 5，数据库脚本面向 MySQL。
- 仓库保留 `WEB-INF/lib` 依赖 jar，便于不安装 Maven 直接按 `verify.ps1` 完成编译与测试。
