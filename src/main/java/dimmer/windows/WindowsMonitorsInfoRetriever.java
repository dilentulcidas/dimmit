package dimmer.windows;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.profesorfalken.wmi4java.WMI4Java;
import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import dimmer.MonitorInfo;

/**
 * Gets information of the each of the connected monitors. Only gets run at first launch.
 */
public class WindowsMonitorsInfoRetriever
{
    private static List<MonitorInfo> MONITOR_INFO_LIST = new ArrayList<>();

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
        List<String> uniqueMonitorIds = getUniqueMonitorIds();
        User32.INSTANCE.EnumDisplayMonitors(null, null, new WinUser.MONITORENUMPROC() {
            @Override
            public int apply(WinUser.HMONITOR hMonitor, WinDef.HDC hdc, WinDef.RECT rect, WinDef.LPARAM lparam)
            {
                extractInfoIntoList(hMonitor, uniqueMonitorIds);
                return 1;
            }

        }, new WinDef.LPARAM(0));
    }

    /**
     * The WinAPI call used in {@link #loadMonitorInfosToList()} doesn't output the unique monitor id
     * for each of the returned displays. So we're using a WMI call to get the device ids.
     * @return Device id of each of the monitors
     */
    private static List<String> getUniqueMonitorIds()
    {
        String rawQueryOutput =
                WMI4Java.get()
                        .VBSEngine()
                        .namespace("root/CIMV2")
                        .properties(Arrays.asList("DeviceID"))
                        .getRawWMIObjectOutput("Win32_PnPEntity");
        String newLineRegex = "\\r?\\n";
        return Arrays.asList(rawQueryOutput.split(newLineRegex))
                .stream()
                .map(deviceIdLine -> deviceIdLine.split(":")[1].trim()) // Get the value portion
                .filter(deviceId -> deviceId.startsWith("DISPLAY\\")) // Filter to only the monitor displays
                .collect(Collectors.toList());
    }
    /**
     * Loads the monitor info, extract what we need then add it to the list
     */
    private static void extractInfoIntoList(WinUser.HMONITOR hMonitor, List<String> uniqueMonitorIds)
    {
        // Populate the monitor info into the hMonitor variable
        WinUser.MONITORINFOEX info = new WinUser.MONITORINFOEX();
        User32.INSTANCE.GetMonitorInfo(hMonitor, info);

        // Get what we can from MONITORINFOEX output
        boolean isPrimary = (info.dwFlags & WinUser.MONITORINFOF_PRIMARY) != 0;
        int monitorIndex = Integer.parseInt(new String(info.szDevice).replace("\\\\.\\DISPLAY", "").trim());

        // Get the monitor id (Using WMI call to get this as MONITORINFOEX doesn't output the unique id.)
        // CONCERN: since I'm relying on the monitor index number, the WMI call output must be
        // output in an ascending order. Otherwise there'll be a mismatch of the ids and the incorrect
        // monitor will get its brightness dimmed/undimmed.
        String monitorId = uniqueMonitorIds.get(monitorIndex-1);

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

            // Get current brightness - if the result is false means that the monitor doesn't support
            // the `getMonitorBrightness` function. Which means it changes the brightness dynamically through software.
            WinDef.DWORDByReference pdwMinimumBrightness = new WinDef.DWORDByReference();
            WinDef.DWORDByReference pdwCurrentBrightness = new WinDef.DWORDByReference();
            WinDef.DWORDByReference pdwMaximumBrightness = new WinDef.DWORDByReference();
            WinDef.BOOL isBrightnessAdjustablePhysically = Dxva2.INSTANCE.GetMonitorBrightness(physicalMonitorHandle, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
            boolean isBrightnessDynamicallyAdjustable = !isBrightnessAdjustablePhysically.booleanValue();

            // Add the monitor info we gathered into the list
            MONITOR_INFO_LIST.add(new MonitorInfo(extractModelFromMonitorId(monitorId), monitorIndex, monitorId, isPrimary, isBrightnessDynamicallyAdjustable));
        }
        else
        {
            System.out.println("Device "+ info.szDevice + "has zero physical monitors linked! Do nothing...");
        }
    }

    /**
     * Monitor id has the following format: DISPLAY\LGD0470\5&4729FAA&0&UID4353
     * We want to simplify this by just returning the model number: LGD0470
     */
    private static String extractModelFromMonitorId(String monitorId)
    {
        // `DISPLAY\` has been removed
        String modelNumberWithRemovedDisplayPrefix = monitorId.substring(monitorId.indexOf("\\") + 1);
        // Keep everything until the next backslash
        return modelNumberWithRemovedDisplayPrefix.substring(0, modelNumberWithRemovedDisplayPrefix.indexOf("\\"));
    }
}
