param([string]$JavaHome = $env:JAVA_HOME)
$ErrorActionPreference='Stop'
if (-not $JavaHome -or -not (Test-Path (Join-Path $JavaHome 'bin/javac.exe'))) { throw 'Set JAVA_HOME to an existing Java 21 JDK.' }
$root=Split-Path -Parent $PSScriptRoot
$env:JAVA_HOME=$JavaHome
$env:GRADLE_USER_HOME=Join-Path $root '.gradle-user-home'
Push-Location $root
try {
    & .\gradlew.bat :common:test :fabric:build :neoforge:build --console=plain
    exit $LASTEXITCODE
} finally { Pop-Location }
