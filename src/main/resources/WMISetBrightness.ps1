param(
[string]$a
)

$monitorToActOn = Get-WmiObject -Namespace root\wmi -Class WmiMonitorBrightnessMethods | Where-Object {$_.InstanceName -like "DISPLAY\LGD0470\5&4729faa&0&UID4353_0*"}
$monitorToActOn.wmisetbrightness(5, $a)