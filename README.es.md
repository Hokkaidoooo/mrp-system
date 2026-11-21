![Sistema de planificación de necesidades de materiales](assets/cover.svg)

# Sistema de planificación de necesidades de materiales (MRP)

[简体中文](README.md) · [English](README.en.md) · **Español** · [日本語](README.ja.md)

Aplicación Java 8, Struts2 e Hibernate para estudiar listas de materiales (BOM) de varios niveles, inventarios y necesidades de componentes. A partir de las órdenes planificadas de lanzamiento de los artículos de nivel superior, calcula necesidades brutas, existencias proyectadas y órdenes planificadas en 13 intervalos. Usa JSP y Bootstrap para la interfaz y MySQL para los datos. El cálculo puede probarse sin base de datos.

[Ejemplos resueltos y pruebas de despliegue](docs/optimization-validation.md) · [CI en Windows y Linux](.github/workflows/ci.yml)

## Preparación y ejecución

Se necesitan JDK 8, Maven 3.9, Tomcat 9 y MySQL. Crea y selecciona un esquema de ejemplo dedicado, como `mrp_demo`. Importa [mrp.sql](mrp.sql) o su [base V1](db/migration/V1__legacy_schema.sql), que es idéntica, y ejecuta [V3](db/migration/V3__atomic_batches_and_past_due.sql) una vez. La base contiene `DROP TABLE` y datos de ejemplo, y no crea ni selecciona el esquema. No la ejecutes en una base empresarial existente. V3 añade la fila de bloqueo y las tablas de necesidades fuera del horizonte. [V2](db/migration/V2__period_line_tables.sql) no se usa y no hay migraciones automáticas.

Desde la raíz del repositorio en PowerShell, sustituye las credenciales de ejemplo y ejecuta la compilación:

```powershell
$env:MRP_DB_URL = 'jdbc:mysql://localhost:3306/mrp_demo?useUnicode=true&characterEncoding=UTF-8'
$env:MRP_DB_USERNAME = 'mrp_user'
$env:MRP_DB_PASSWORD = '<database-password>'
powershell -NoProfile -File scripts\verify.ps1
```

El script ejecuta `mvn -B clean verify` y comprueba en el WAR las clases, la configuración y las dependencias. Despliega `target/mrp-1.0.0-SNAPSHOT.war` en Tomcat como `mrp.war`. Inicia Tomcat con las tres variables de conexión o con las propiedades JVM equivalentes del [ejemplo de configuración](src/hibernate.properties.example). Si falta una opción obligatoria, se produce un error explícito.

Abre `/mrp/index.jsp`. Desde las pantallas de artículos, BOM, existencias y entradas de nivel superior puedes consultar o introducir datos. La pantalla MRP recalcula el lote mediante `/mrp/MRP_domrp.action`; usa el esquema de ejemplo para este recorrido.

## Modelo de planificación

| Etapa | Función |
| --- | --- |
| Entradas | Mantener [artículos, relaciones de BOM e inventario](src/entity/). [TPOP](src/action/TPOPAction.java) recibe lanzamientos planificados en `POR0`–`POR12`; no genera un plan maestro a partir de pedidos. |
| Explosión de la BOM | [Validar el grafo](src/service/BomGraph.java) y [acumular necesidades](src/service/MrpCalculationService.java) en orden topológico, incluso cuando un componente tiene varios padres. |
| Cálculo por periodo | [Calcular](src/util/MrpPeriodCalculator.java) necesidades netas y recepciones con existencias, asignaciones, stock de seguridad y reglas de lote. Aplicar el plazo más el tiempo de seguridad para obtener el periodo de lanzamiento. |
| Guardado | [Recalcular](src/service/MrpRecalculationService.java) a partir de una sola instantánea y sustituir todo el lote de resultados de forma atómica. |

- Hay exactamente 13 intervalos, numerados del 0 al 12. En el 0, las existencias proyectadas, el saldo disponible proyectado y las necesidades netas usan `-1` como marcador. Las necesidades brutas y recepciones previstas del 0 pasan al periodo 1. El cálculo ordinario cubre los periodos 1 a 12.
- FOQ redondea al múltiplo superior del lote fijo: con necesidad neta 25 y tamaño 10, se reciben 30. LFL iguala la necesidad neta. Las cantidades o reglas inválidas, el tamaño FOQ cero, el plazo negativo y el desbordamiento aritmético dan error.
- Los lanzamientos previos al periodo 0 conservan su periodo negativo en `pastDueReleases`. El componente hijo suma la cantidad a la necesidad bruta del intervalo 0 y guarda el periodo original en `pastDueGrossRequirements`. Son cantidades vencidas pendientes de resolver.
- La BOM exige artículos existentes, cantidades de uso positivas y ausencia de ciclos o aristas duplicadas. El código de nivel inferior (LLC) debe coincidir con el camino más largo desde una raíz. La falta de entradas superiores obligatorias o de inventario de un componente, y los registros duplicados, dan error.
- Con InnoDB y `REPEATABLE_READ`, un bloqueo de fila serializa los recálculos. Un error revierte todo el lote. Las ediciones posteriores a la instantánea se leen en un recálculo posterior.

## Pruebas y licencia

La compilación normal ejecuta las [pruebas por periodo](test/util/MrpPeriodCalculatorTest.java) y las [pruebas de explosión de la BOM](test/service/MrpCalculationServiceTest.java). Con Python 3, `python scripts\check_verify_failures.py` comprueba en una copia aparte los errores de compilación, aserción y empaquetado, y la ausencia de Maven.

Para las [pruebas de integración](test/service/MrpWorkflowIT.java), utiliza un MySQL dedicado a pruebas. La cuenta debe poder crear y eliminar esquemas aleatorios `mrp_test_*`; no uses un servidor empresarial. Se verifican la reversión real en MySQL, los recálculos concurrentes, los mapeos y el WAR en una JVM aparte con Tomcat 9.

```powershell
$env:MRP_TEST_MYSQL_URL = 'jdbc:mysql://127.0.0.1:33317/'
$env:MRP_TEST_MYSQL_USER = 'test_admin'
$env:MRP_TEST_MYSQL_PASSWORD = '<test-server-password>'
powershell -NoProfile -File scripts\verify.ps1 -Integration
```

No se declara una licencia para el repositorio en su conjunto.
