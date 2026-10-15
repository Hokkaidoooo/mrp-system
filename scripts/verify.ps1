param([switch]$Integration)
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$root = Split-Path -Parent $PSScriptRoot

function Invoke-Checked([string]$Tool, [string[]]$Arguments) {
    & $Tool @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$Tool failed with exit code $LASTEXITCODE"
    }
}

Push-Location $root
try {
    $maven = Get-Command mvn -ErrorAction Stop
    $jar = Get-Command jar -ErrorAction Stop
    $mavenArgs = @('-B', 'clean', 'verify')
    if ($Integration) { $mavenArgs += '-Pintegration' }
    Invoke-Checked $maven.Source $mavenArgs
    $war = Join-Path 'target' 'mrp-1.0.0-SNAPSHOT.war'
    $entries = & $jar.Source tf $war
    if ($LASTEXITCODE -ne 0) { throw "Cannot inspect WAR: $LASTEXITCODE" }
    foreach ($required in @('WEB-INF/classes/action/MRPAction.class', 'WEB-INF/classes/service/MrpRecalculationService.class', 'WEB-INF/classes/hibernate.cfg.xml', 'WEB-INF/classes/entity/MRP.hbm.xml', 'WEB-INF/classes/struts.xml', 'WEB-INF/web.xml')) {
        if ($entries -notcontains $required) { throw "WAR is missing $required" }
    }
    foreach ($dependency in @('struts2-core-', 'hibernate-core-', 'mysql-connector-j-')) {
        if (-not ($entries -match ('^WEB-INF/lib/' + $dependency))) { throw "WAR is missing runtime dependency $dependency" }
    }
    Write-Host 'Build, tests and WAR contract passed.'
} finally {
    Pop-Location
}
