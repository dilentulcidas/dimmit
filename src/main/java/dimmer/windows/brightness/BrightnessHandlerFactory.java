package dimmer.windows.brightness;

import com.sun.jna.platform.win32.WinDef;

public class BrightnessHandlerFactory
{
    private BrightnessHandlerFactory() {}

    public static BrightnessHandler get(WinDef.BOOL winApiIsSuccess, BrightnessValuesHolder valuesHolder)
    {
        if (winApiIsSuccess.booleanValue())
        {
            return new WinAPIBrightnessHandler(valuesHolder.getPdwCurrentBrightness());
        }
        else
        {
            return new WMIBrightnessHandler();
        }
    }
}
