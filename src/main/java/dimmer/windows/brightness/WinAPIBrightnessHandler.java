package dimmer.windows.brightness;

import java.util.Optional;

import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import dimmer.MonitorInfo;

/**
 * Deals with a monitor where you can't change brightness dynamically. Example: external monitors with
 * physical brightness adjusting buttons.
 *
 * We'll use JNA library and go through methods from the winapi, docs here https://docs.microsoft.com/en-us/windows/win32/api/highlevelmonitorconfigurationapi/nf-highlevelmonitorconfigurationapi-setmonitorbrightness?redirectedfrom=MSDN
 */
class WinAPIBrightnessHandler implements WindowsBrightnessHandler
{
    private final MonitorInfo monitorInfo;

    WinAPIBrightnessHandler(MonitorInfo monitorInfo)
    {
        this.monitorInfo = monitorInfo;
    }

    @Override
    public String getCurrentBrightness()
    {
        final StringBuilder currentBrightnessBuilder = new StringBuilder();
        // Go through the available monitors. Filter to the monitor with our monitorInfo's id. Get its brightness.
        User32.INSTANCE.EnumDisplayMonitors(null, null, new WinUser.MONITORENUMPROC() {
            @Override
            public int apply(WinUser.HMONITOR hMonitor, WinDef.HDC hdc, WinDef.RECT rect, WinDef.LPARAM lparam)
            {
                WinUser.MONITORINFOEX info = new WinUser.MONITORINFOEX();
                User32.INSTANCE.GetMonitorInfo(hMonitor, info);
                String displayIndex = new String(info.szDevice);
                // Compare the index of the current display from the enum against our current monitorInfo obj's index (Could be unreliable...)
                if (displayIndex.trim().equals("\\\\.\\DISPLAY" + monitorInfo.getMonitorIndex()))
                {
                    // Get monitor count
                    WinDef.DWORDByReference pdwNumberOfPhysicalMonitors = new WinDef.DWORDByReference();
                    Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
                    int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();

                    // Load the monitor HANDLE obj
                    PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[] physMons = new PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[monitorCount];
                    Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);
                    WinNT.HANDLE physicalMonitorHandle = physMons[0].hPhysicalMonitor;

                    // Get current brightness
                    WinDef.DWORDByReference pdwMinimumBrightness = new WinDef.DWORDByReference();
                    WinDef.DWORDByReference pdwCurrentBrightness = new WinDef.DWORDByReference();
                    WinDef.DWORDByReference pdwMaximumBrightness = new WinDef.DWORDByReference();
                    Dxva2.INSTANCE.GetMonitorBrightness(physicalMonitorHandle, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);

                    currentBrightnessBuilder.append(pdwCurrentBrightness.getValue().toString());
                }
                return 1;
            }
        }, new WinDef.LPARAM(0));

        String currentBrightness = currentBrightnessBuilder.toString();
        if (!currentBrightness.isEmpty())
        {
            return currentBrightness;
        }
        else
        {
            throw new IllegalStateException("Failed to get current brightness using WinAPI!");
        }
    }

    @Override
    public void setBrightnessToZero()
    {
        // todo
    }
}
