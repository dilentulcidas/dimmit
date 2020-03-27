package dimmer.windows.brightness;

import com.sun.jna.platform.win32.WinDef;

/**
 * Class which stores the variables that may be useful to be transferred to {@link BrightnessHandler} concrete
 * classes.
 */
public class BrightnessValuesHolder
{
    private String monitorInstanceName;
    private WinDef.DWORDByReference pdwCurrentBrightness;

    public BrightnessValuesHolder(String monitorInstanceName, WinDef.DWORDByReference pdwCurrentBrightness)
    {
        this.monitorInstanceName = monitorInstanceName;
        this.pdwCurrentBrightness = pdwCurrentBrightness;
    }

    public String getMonitorInstanceName()
    {
        return monitorInstanceName;
    }

    public WinDef.DWORDByReference getPdwCurrentBrightness()
    {
        return pdwCurrentBrightness;
    }
}
