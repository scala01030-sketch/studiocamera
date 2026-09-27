param([ValidateSet('17','21','all')][string]$Java='all')
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
if([IO.Path]::GetPathRoot($root) -ne 'E:\'){throw 'E: storage required'}
$env:TEMP=Join-Path $root '.toolchain\temp';$env:TMP=$env:TEMP
New-Item -ItemType Directory -Force $env:TEMP,(Join-Path $root '.toolchain\user-home') | Out-Null
$versions=if($Java -eq 'all'){@('17','21')}else{@($Java)}
foreach($version in $versions){
    $target=Join-Path $root ('.toolchain\jdk'+$version)
    if(Test-Path -LiteralPath (Join-Path $target 'bin\java.exe')){continue}
    $asset=Invoke-RestMethod ('https://api.adoptium.net/v3/assets/latest/'+$version+'/hotspot?architecture=x64&image_type=jdk&os=windows&vendor=eclipse')
    $package=$asset[0].binary.package
    $archive=Join-Path $root ('.toolchain\jdk'+$version+'.zip')
    Invoke-WebRequest -UseBasicParsing $package.link -OutFile $archive
    if((Get-FileHash -LiteralPath $archive -Algorithm SHA256).Hash -ne $package.checksum){throw 'JDK SHA-256 mismatch'}
    $extract=Join-Path $root ('.toolchain\jdk'+$version+'-unpack')
    Expand-Archive -LiteralPath $archive -DestinationPath $extract -Force
    $directory=Get-ChildItem -LiteralPath $extract -Directory | Select-Object -First 1
    $resolved=[IO.Path]::GetFullPath($directory.FullName)
    if(!$resolved.StartsWith([IO.Path]::GetFullPath($root)+[IO.Path]::DirectorySeparatorChar)){throw 'JDK source escaped project root'}
    Move-Item -LiteralPath $resolved -Destination $target
    [IO.File]::WriteAllText((Join-Path $root ('.toolchain\jdk'+$version+'-download.json')),(@{url=$package.link;sha256=$package.checksum}|ConvertTo-Json),[Text.UTF8Encoding]::new($false))
    & (Join-Path $target 'bin\java.exe') -version
}
