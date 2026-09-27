$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
if ([IO.Path]::GetPathRoot($root) -ne 'E:\') { throw 'E: project required' }
$release = Join-Path $root 'release\0.1.0'
if (Test-Path -LiteralPath $release) { throw 'Release already exists; choose a new version or remove the old local release first' }
New-Item -ItemType Directory -Path $release | Out-Null

foreach ($target in @('forge', 'fabric')) {
    $mc = if ($target -eq 'forge') { '1.20.1' } else { '1.21.11' }
    $name = "studiocamera-$target-$mc-0.1.0.jar"
    $source = Join-Path $root "$target\build\libs\$name"
    if (!(Test-Path -LiteralPath $source)) { throw "Missing build: $source" }
    Copy-Item -LiteralPath $source -Destination $release
}
Copy-Item -LiteralPath (Join-Path $root 'README.md'), (Join-Path $root 'LICENSE') -Destination $release

$sourceZip = Join-Path $release 'studiocamera-0.1.0-source.zip'
Push-Location $root
try {
    & git archive --format=zip --prefix=StudioCamera/ -o $sourceZip HEAD
    if ($LASTEXITCODE -ne 0) { throw 'git archive failed' }
} finally { Pop-Location }

Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive = [IO.Compression.ZipFile]::OpenRead($sourceZip)
try {
    $forbidden = $archive.Entries | Where-Object {
        $_.FullName -match '(?i)(^|/)(verification|evidence|screenshots?|game-checks?|saves?|logs?|run)(/|$)|\.(png|jpg|jpeg|webp|log)$'
    }
    if ($forbidden) { throw ('Forbidden source archive entries: ' + (($forbidden | Select-Object -ExpandProperty FullName) -join ', ')) }
} finally { $archive.Dispose() }

$hashes = Get-ChildItem -LiteralPath $release -File | Sort-Object Name | ForEach-Object {
    (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash + '  ' + $_.Name
}
[IO.File]::WriteAllLines((Join-Path $release 'SHA256SUMS.txt'), $hashes, [Text.UTF8Encoding]::new($false))
$bundle = Join-Path $root 'release\studiocamera-0.1.0-release.zip'
[IO.Compression.ZipFile]::CreateFromDirectory($release, $bundle, [IO.Compression.CompressionLevel]::Optimal, $true)
Write-Output "PACKAGED $bundle"
