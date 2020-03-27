package dimmer;

import java.util.List;

/**
 * Covers the implementation of the dimmer for each of the supported operating systems.
 * Puts the app running in the background with a system tray.
 */
public interface DimmerManager
{
    List<MonitorInfo> getInformationOfAllMonitors();

    /**
     * Dims all the monitors except the monitors with the given ids
     *
     * @param selectedMonitorIds monitor items that are ticked
     */
    void dimAllExcept(List<String> selectedMonitorIds);

    /**
     * Dims the monitor with the provided id
     *
     * @param monitorId Monitor to dim
     */
    void dim(String monitorId);

    /**
     * Undims the monitor with the provided id
     * @param monitorId Monitor to undim
     */
    void undim(String monitorId);
}
