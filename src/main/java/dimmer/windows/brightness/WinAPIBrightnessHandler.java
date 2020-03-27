package dimmer.windows.brightness;

import com.sun.jna.platform.win32.WinDef;

/**
 * For monitors where you can't change brightness dynamically. Example: external monitors with
 * physical brightness adjusting buttons.
 *
 * We'll use the following API: https://docs.microsoft.com/en-us/windows/win32/api/highlevelmonitorconfigurationapi/nf-highlevelmonitorconfigurationapi-setmonitorbrightness?redirectedfrom=MSDN
 */
public class WinAPIBrightnessHandler implements BrightnessHandler
{
    private final WinDef.DWORDByReference pdwCurrentBrightness;

    WinAPIBrightnessHandler(WinDef.DWORDByReference pdwCurrentBrightness)
    {
        this.pdwCurrentBrightness = pdwCurrentBrightness;
    }

    @Override
    public void setBrightness()
    {

    }

    @Override
    public String getCurrentBrightness()
    {
        return pdwCurrentBrightness.getValue().toString();
    }
}
