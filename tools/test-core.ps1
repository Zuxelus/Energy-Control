param([string]$JavaHome = $env:JAVA_HOME)
$ErrorActionPreference = 'Stop'
if (-not $JavaHome) { throw 'Set JAVA_HOME to an existing Java 21 JDK.' }
$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    $runner = '.tools/junit-platform-console-standalone-1.11.4.jar'
    if (-not (Test-Path $runner)) {
        New-Item -ItemType Directory -Force .tools | Out-Null
        Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar' -OutFile $runner
    }
    New-Item -ItemType Directory -Force common/build/core-tests | Out-Null
    $sources = @(Get-ChildItem common/src/main/java/com/zuxelus/energycontrol/port/core -Filter *.java) + @(Get-ChildItem common/src/test/java/com/zuxelus/energycontrol/port/core -Filter *.java)
    & (Join-Path $JavaHome 'bin/javac.exe') --release 21 -cp $runner -d common/build/core-tests $sources.FullName
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    & (Join-Path $JavaHome 'bin/java.exe') -jar $runner execute --class-path common/build/core-tests --scan-class-path --disable-ansi-colors
    exit $LASTEXITCODE
} finally { Pop-Location }
