$ErrorActionPreference = "Stop"

$envFile = Join-Path $PSScriptRoot ".env.local.ps1"
if (Test-Path $envFile) {
  . $envFile
} else {
  Write-Host "Local env file not found: .env.local.ps1"
  Write-Host "Copy .env.local.ps1.example to .env.local.ps1 and edit values."
}

if (-not $env:JAVA_HOME) {
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
}

$env:SPRING_PROFILES_ACTIVE = "local"

$port = if ($env:SERVER_PORT) { [int]$env:SERVER_PORT } else { 8087 }

function Stop-ProcessOnPort {
  param(
    [Parameter(Mandatory = $true)]
    [int]$Port
  )

  $connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue |
    Where-Object { $_.State -in @("Listen", "Established", "Bound") } |
    Select-Object -ExpandProperty OwningProcess -Unique

  if (-not $connections) {
    Write-Host "No active process found on port $Port"
    return
  }

  foreach ($owningProcessId in $connections) {
    if ($owningProcessId -eq $PID) {
      continue
    }

    try {
      $process = Get-Process -Id $owningProcessId -ErrorAction Stop
      Write-Host "Stopping process on port ${Port}: PID=$owningProcessId Name=$($process.ProcessName)"
      Stop-Process -Id $owningProcessId -Force -ErrorAction Stop
    } catch {
      Write-Warning "Could not stop PID=$owningProcessId on port $Port. $($_.Exception.Message)"
    }
  }

  Start-Sleep -Milliseconds 500
}

Write-Host "JAVA_HOME=$env:JAVA_HOME"
Write-Host "SPRING_PROFILES_ACTIVE=$env:SPRING_PROFILES_ACTIVE"
Write-Host "SERVER_PORT=$port"

Stop-ProcessOnPort -Port $port

& "$PSScriptRoot\mvnw.cmd" spring-boot:run
