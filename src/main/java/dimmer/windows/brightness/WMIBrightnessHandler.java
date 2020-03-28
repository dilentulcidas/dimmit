package dimmer.windows.brightness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.profesorfalken.wmi4java.WMI4Java;
import dimmer.MonitorInfo;

/**
 * Deals with a monitor where the brightness is adjusted dynamically through software. Example: laptops, etc
 *
 * We'll use the WMI4Java library that runs the queries through VBSScript.
 *
 */
class WMIBrightnessHandler implements WindowsBrightnessHandler
{
    private static final String NEWLINE_REGEX = "\\r?\\n";

    private final MonitorInfo monitorInfo;

    WMIBrightnessHandler(MonitorInfo monitorInfo)
    {
        this.monitorInfo = monitorInfo;
    }

    @Override
    public String getCurrentBrightness()
    {
        String getBrightnessWMIQueryResponse =
                WMI4Java.get()
                        .VBSEngine()
                        .namespace("root/WMI")
                        .properties(Arrays.asList("InstanceName", "CurrentBrightness"))
                        .getRawWMIObjectOutput("WmiMonitorBrightness");

        return processGetBrightnessWMIQueryResponse(getBrightnessWMIQueryResponse)
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().startsWith(monitorInfo.getMonitorId().toLowerCase()))
                .map(Map.Entry::getValue)
                .findFirst()
                .get();
    }

    /**
     * Process returned string from WMI4Java query in {@link #getCurrentBrightness()}
     * so that we get the following map format {monitor_id, brightness_value}
     */
    private Map<String, String> processGetBrightnessWMIQueryResponse(String response)
    {
        List<String> modelNames = Arrays.asList(response.split(NEWLINE_REGEX))
                .stream()
                .filter(line -> line.startsWith("InstanceName:"))
                .map(lineWithInstanceName -> lineWithInstanceName.replace("InstanceName: ", "").trim())
                .collect(Collectors.toList());
        List<String> brightnessValues = Arrays.asList(response.split(NEWLINE_REGEX))
                .stream()
                .filter(line -> line.startsWith("CurrentBrightness:"))
                .map(lineWithInstanceName -> lineWithInstanceName.replace("CurrentBrightness: ", "").trim())
                .collect(Collectors.toList());
        Map<String, String> modelAndBrightnessMap = new HashMap<>();
        for (int i = 0; i < modelNames.size(); i++)
        {
            modelAndBrightnessMap.put(modelNames.get(i), brightnessValues.get(i));
        }
        return modelAndBrightnessMap;
    }

    /**
     * LIMITATION
     * ================================
     * Sets brightness to ALL displays that support adjusting the brightness through software!
     * This is a limitation of the WMI api, can't choose which monitor we want to set the brightness to.
     *
     * @param brightnessNumber
     */
    @Override
    public void setBrightness(int brightnessNumber) throws Exception
    {
        String setBrightnessCommand = "powershell.exe -command \"{$brightness = " + brightnessNumber + " $delay = 5 $myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods $myMonitor.wmisetbrightness($delay, $brightness)}\"";
        Runtime.getRuntime().exec(setBrightnessCommand);
    }
}
