package dimmer.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import dimmer.MonitorInfo;
import dimmer.windows.brightness.BrightnessHandlerFactory;
import dimmer.windows.brightness.BrightnessValuesHolder;

/**
 * Gets information of the each of the connected monitors
 */
public class WindowsMonitorsInfoRetriever
{
    private static List<MonitorInfo> MONITOR_INFO_LIST = Collections.synchronizedList(new ArrayList<>());

    /**
     * Gets the information for each monitor connected
     */
    public static List<MonitorInfo> get()
    {
        loadMonitorInfosToList();
        return MONITOR_INFO_LIST;
    }

    /**
     * Loads the information of all monitors into the list
     */
    private static void loadMonitorInfosToList()
    {
        User32.INSTANCE.EnumDisplayMonitors(null, null, new WinUser.MONITORENUMPROC() {
            @Override
            public int apply(WinUser.HMONITOR hMonitor, WinDef.HDC hdc, WinDef.RECT rect, WinDef.LPARAM lparam)
            {
                extractInfoIntoList(hMonitor);
                return 1;
            }

        }, new WinDef.LPARAM(0));
    }

    /**
     * Loads the monitor info, extract what we need then add it to the list
     */
    private static synchronized void extractInfoIntoList(WinUser.HMONITOR hMonitor)
    {
        // Populate the monitor info into the hMonitor variable
        WinUser.MONITORINFOEX info = new WinUser.MONITORINFOEX();
        User32.INSTANCE.GetMonitorInfo(hMonitor, info);

        String monitorId = hMonitor.getPointer().toString();
        boolean isPrimary = (info.dwFlags & WinUser.MONITORINFOF_PRIMARY) != 0;

        // Add the monitor info we gathered into the list
        MONITOR_INFO_LIST.add(new MonitorInfo(monitorId, isPrimary));
    }
}
