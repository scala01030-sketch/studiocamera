param([ValidateSet('forge','fabric','all')][string]$Platform='all', [Parameter(ValueFromRemainingArguments=$true)][string[]]$Tasks)
$ErrorActionPreference='Stop'
$root=$PSScriptRoot
if([IO.Path]::GetPathRoot($root) -ne 'E:\') {throw 'All project storage must be on E:.'}
$env:TEMP=Join-Path $root '.toolchain\temp'; $env:TMP=$env:TEMP
$env:JAVA_OPTS='-Djava.io.tmpdir=E:/StudioCamera/.toolchain/temp -Duser.home=E:/StudioCamera/.toolchain/user-home -Dfile.encoding=UTF-8'
if(!$Tasks){$Tasks=@('build')}
$platforms=if($Platform -eq 'all'){@('forge','fabric')}else{@($Platform)}
foreach($target in $platforms){
    if($target -eq 'forge'){
        $env:JAVA_HOME=Join-Path $root '.toolchain\jdk17'
        $env:GRADLE_USER_HOME=Join-Path $root '.toolchain\gradle-user-home'
        # Optional E-local development cache. The published source can build independently.
        if($env:STUDIOCAMERA_FORGE_CACHE){
            if([IO.Path]::GetPathRoot($env:STUDIOCAMERA_FORGE_CACHE) -ne 'E:\'){throw 'Forge cache must be on E:'}
            $env:GRADLE_USER_HOME=$env:STUDIOCAMERA_FORGE_CACHE
        }
    }else{
        $env:JAVA_HOME=Join-Path $root '.toolchain\jdk21'
        $env:GRADLE_USER_HOME=Join-Path $root '.toolchain\gradle-user-home'
    }
    Push-Location (Join-Path $root $target)
    try { & '.\gradlew.bat' @Tasks '--no-daemon'; if($LASTEXITCODE -ne 0){throw "$target build failed: $LASTEXITCODE"} } finally {Pop-Location}
}
