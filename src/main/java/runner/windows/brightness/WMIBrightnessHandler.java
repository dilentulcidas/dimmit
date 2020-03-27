package runner.windows.brightness;

import java.util.Arrays;
import java.util.Map;

import com.profesorfalken.wmi4java.WMI4Java;

/**
 * For monitors where the brightness is adjusted dynamically through software. Example: laptops, etc
 *
 * We'll use powershell queries to get/set this.
 *
 * todo: find a way to pass the instancename
 */
public class WMIBrightnessHandler implements BrightnessHandler
{
    @Override
    public void setBrightness()
    {

    }

    @Override
    public String getCurrentBrightness()
    {
        Map<String, String> wmiObjectProperties =
                WMI4Java.get()
                        .namespace("root/WMI")
                        .properties(Arrays.asList("InstanceName", "CurrentBrightness"))
                        .getWMIObject("WmiMonitorBrightness");
        System.out.println(wmiObjectProperties);

        return "-1";
    }
}
