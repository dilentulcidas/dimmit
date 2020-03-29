package dimmer.windows.brightness;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

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
                .orElseThrow(() -> new IllegalStateException("Failed to get current brightness using WMI!"));
    }

    /**
     * Process returned string from WMI4Java query in {@link #getCurrentBrightness()}
     * so that we get the following map format {monitor_id, brightness_value}.
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
     * Gets our monitor instance from the WmiMonitorBrightnessMethods then
     * acts on it to set the brightness.
     */
    @Override
    public void setBrightness(int brightnessNumber) throws Exception
    {
        String scriptPath = getClass().getClassLoader().getResource("WMISetBrightness.ps1").getPath().substring(1);
        String setBrightnessCommand = "Powershell.exe -ExecutionPolicy Bypass -File " + scriptPath + " \"" + brightnessNumber + "\"";
        Process result = Runtime.getRuntime().exec(setBrightnessCommand);
        String errMsg = IOUtils.toString(result.getErrorStream(), Charset.defaultCharset()).trim();
        if (!errMsg.isEmpty())
        {
            throw new IllegalStateException("Failure while setting brightness through WMI: " + errMsg);
        }
    }
}
