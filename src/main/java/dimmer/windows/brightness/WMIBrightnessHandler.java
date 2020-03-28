package dimmer.windows.brightness;

import java.util.Arrays;
import java.util.Map;

import com.profesorfalken.wmi4java.WMI4Java;

/**
 * For monitors where the brightness is adjusted dynamically through software. Example: laptops, etc
 *
 * We'll use powershell queries to get/set this.
 *
 * todo: find a way to know which monitor id (InstanceName) we're dealing with so we get the correct brightness value
 */
public class WMIBrightnessHandler implements WindowsBrightnessHandler
{
    @Override
    public String getCurrentBrightness()
    {
        // todo
        Map<String, String> wmiObjectProperties =
                WMI4Java.get()
                        .namespace("root/WMI")
                        .properties(Arrays.asList("InstanceName", "CurrentBrightness"))
                        .getWMIObject("WmiMonitorBrightness");
        System.out.println(wmiObjectProperties);

        return "-1";
    }

    @Override
    public void setBrightnessToZero()
    {
        // todo
    }
}
