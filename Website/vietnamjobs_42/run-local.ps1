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

Write-Host "JAVA_HOME=$env:JAVA_HOME"
Write-Host "SPRING_PROFILES_ACTIVE=$env:SPRING_PROFILES_ACTIVE"

& "$PSScriptRoot\mvnw.cmd" spring-boot:run
