param(
    [ValidateSet('fabric','neoforge')][string]$Loader='fabric',
    [ValidateSet('server','client')][string]$Role='server',
    [string]$JavaHome=$env:JAVA_HOME,
    [switch]$AcceptMinecraftEula,
    [switch]$ResetFixture,
    [switch]$Offline
)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
if(-not $JavaHome -or -not(Test-Path (Join-Path $JavaHome 'bin/javac.exe'))) {throw 'Provide an existing Java 21 JDK.'}
$run=Join-Path $root "$Loader/run/$Role"
New-Item -ItemType Directory -Force $run | Out-Null
if($Role -eq 'server') {
    $eula=Join-Path $run 'eula.txt'
    if($AcceptMinecraftEula) {Set-Content -LiteralPath $eula 'eula=true'}
    if(-not(Test-Path $eula) -or -not(Select-String -LiteralPath $eula -SimpleMatch 'eula=true' -Quiet)) {throw 'Read https://aka.ms/MinecraftEULA and explicitly accept before starting your own test server.'}
    $properties=Join-Path $run 'server.properties'
    if(-not(Test-Path $properties)) {
        $port=if($Loader -eq 'fabric'){25571}else{25572}
        @("server-ip=127.0.0.1","server-port=$port","online-mode=false","enforce-secure-profile=false","gamemode=creative","difficulty=peaceful","spawn-protection=0","generate-structures=false","level-type=minecraft:flat","view-distance=5","simulation-distance=4","max-players=3","enable-rcon=false","motd=Energy Control isolated QA") | Set-Content -LiteralPath $properties
    }
    if($ResetFixture) {Set-Content -LiteralPath (Join-Path $run 'qa-reset.flag') 'Explicitly requested reset of the QA 3x2 panel fixture.'}
}
$env:JAVA_HOME=$JavaHome
$env:GRADLE_USER_HOME=Join-Path $root '.gradle-user-home'
$task=if($Role -eq 'server'){":${Loader}:runServer"}else{":${Loader}:runClient"}
$arguments=@($task,'-Pqa','--console=plain')
if($Offline){$arguments+='--offline'}
Push-Location $root
try {& .\gradlew.bat @arguments; exit $LASTEXITCODE} finally {Pop-Location}
