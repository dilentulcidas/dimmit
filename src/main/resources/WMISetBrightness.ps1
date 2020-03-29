 param (
    [Parameter(Mandatory=$true)][string]$monitorId,
    [Parameter(Mandatory=$true)][string]$brightness
 )

$monitorToActOn = Get-WmiObject -Namespace root\wmi -Class WmiMonitorBrightnessMethods | Where-Object {$_.InstanceName -like ($monitorId + "*")}
$monitorToActOn.wmisetbrightness(5, $brightness)