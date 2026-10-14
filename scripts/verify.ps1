$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

New-Item -ItemType Directory -Force -Path "out/test" | Out-Null

$sourceFiles = Get-ChildItem -Recurse -Path "src" -Filter "*.java" | ForEach-Object { $_.FullName }
javac -Xlint:unchecked -encoding UTF-8 -cp "src;web\WEB-INF\lib\*" -d "out\test" $sourceFiles

$testSources = @(
    "src\util\MrpPeriodCalculator.java",
    "src\service\MrpCalculationService.java",
    "test\util\MrpPeriodCalculatorTest.java",
    "test\service\MrpCalculationServiceTest.java"
)
javac -encoding UTF-8 -cp "src" -d "out\test" $testSources
java -cp "out\test" util.MrpPeriodCalculatorTest
java -cp "out\test" service.MrpCalculationServiceTest

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    mvn -B -DskipTests package
} else {
    Write-Host "Maven not found; skipped Maven package verification."
}
