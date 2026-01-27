param(
    [string]$DbHost = "localhost",
    [int]$Port = 3306,
    [string]$RootUser = "root",
    [string]$RootPassword
)

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlPath = Join-Path $scriptRoot "setup-db.sql"

if (-not (Test-Path $sqlPath)) {
    throw "SQL file not found: $sqlPath"
}

$mysqlCmd = Get-Command mysql -ErrorAction SilentlyContinue
if (-not $mysqlCmd) {
    throw "mysql client not found in PATH. Install MySQL client and retry."
}

if ($null -eq $RootPassword) {
    if ($env:MYSQL_ROOT_PASSWORD) {
        $RootPassword = $env:MYSQL_ROOT_PASSWORD
    } else {
        $secure = Read-Host "MySQL root password (leave empty if none)" -AsSecureString
        if ($secure.Length -gt 0) {
            $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
            try {
                $RootPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
            } finally {
                [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
            }
        } else {
            $RootPassword = ""
        }
    }
}

$args = @(
    "--default-character-set=utf8mb4",
    "-h", $DbHost,
    "-P", $Port,
    "-u", $RootUser
)

if ($RootPassword -eq "") {
    $args += "--password="
} else {
    $args += "--password=$RootPassword"
}

Write-Host "Initializing database schema and seed data..."
Get-Content -Raw $sqlPath | & $mysqlCmd @args

if ($LASTEXITCODE -ne 0) {
    throw "mysql exited with code $LASTEXITCODE"
}

Write-Host "Done."
Write-Host "Test accounts:"
Write-Host "  admin1 / 123456  (role=admin)"
Write-Host "  user1  / 123456  (role=user)"
