package dimmer;

import java.util.List;

/**
 * Covers the implementation of the dimmer for each of the supported operating systems.
 * Puts the app running in the background with a system tray.
 */
public interface DimmerManager
{
    List<MonitorInfo> getInformationOfAllMonitors();

    MonitorInfo findByMonitorId(String monitorId);

    /**
     * Dims all the monitors except the monitors with the given ids
     *
     * @param selectedMonitors monitor items that are ticked
     */
    void dimAllExcept(List<MonitorInfo> selectedMonitors);

    /**
     * Dims the monitor with the provided id
     *
     * @param monitorInfo Monitor to dim
     */
    void dim(MonitorInfo monitorInfo);

    /**
     * Undims the monitor with corresponds to the provided obj
     *
     * @param monitorInfo Monitor to undim
     */
    void undim(MonitorInfo monitorInfo);
}
