package runner.windows;

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

/**
 * Gets information of the each of the connected monitors
 */
public class MonitorsInfoRetriever
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

        // Get monitor count
        WinDef.DWORDByReference pdwNumberOfPhysicalMonitors = new WinDef.DWORDByReference();
        Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
        int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();

        // Note: we're going to assume each HMONITOR obj is always linked to one physical monitor. Not sure how
        // you can have more than one linked to the same object...
        if (monitorCount > 0)
        {
            // Load the monitor HANDLE obj
            PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[] physMons = new PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[monitorCount];
            Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);

            WinNT.HANDLE physicalMonitorHandle = physMons[0].hPhysicalMonitor;

            // Get brightness levels
            WinDef.DWORDByReference pdwMinimumBrightness = new WinDef.DWORDByReference();
            WinDef.DWORDByReference pdwCurrentBrightness = new WinDef.DWORDByReference();
            WinDef.DWORDByReference pdwMaximumBrightness = new WinDef.DWORDByReference();
            Dxva2.INSTANCE.GetMonitorBrightness(physicalMonitorHandle, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);

            // Gather all the values we need
            String monitorId = hMonitor.getPointer().toString();
            WinDef.RECT screenSize = info.rcMonitor;
            boolean isPrimary = (info.dwFlags & WinUser.MONITORINFOF_PRIMARY) != 0;
            WinDef.DWORD currentBrightness = pdwCurrentBrightness.getValue();

            // Add the monitor info we gathered into the list
            MONITOR_INFO_LIST.add(new MonitorInfo(monitorId, screenSize, isPrimary, currentBrightness));

            // Close this now that we don't need physical monitors info anymore
            Dxva2.INSTANCE.DestroyPhysicalMonitors(monitorCount, physMons);
        }
        else
        {
            System.out.println("Device "+ Arrays.toString(info.szDevice) + "has zero physical monitors linked! Do nothing then...");
        }
    }
}
