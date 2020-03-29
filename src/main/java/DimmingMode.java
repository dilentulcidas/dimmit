import java.awt.CheckboxMenuItem;

import dimmer.DimmerManager;
import dimmer.MonitorInfo;

/**
 * Choose between having the dimmed monitor to have a black sreen or just have low brightness.
 */
enum DimmingMode
{
    BLACK("Black")
            {
                @Override
                void apply(CheckboxMenuItem checkMenuItem, DimmerManager dimmerManager)
                {
                    DimmingMode.changeBrightness(checkMenuItem, dimmerManager);
                    if (MonitorMenuItemsManager.areAllMonitorsTicked() || MonitorMenuItemsManager.areAllMonitorsUnticked())
                    {
                        ScreenBlacker.clearBlackLayersIfExist(dimmerManager);
                    }
                    else
                    {
                        ScreenBlacker.addBlackLayerTo(MonitorMenuItemsManager.getUntickedMonitors(dimmerManager), dimmerManager, this);
                    }
                }
            },
    LOW_BRIGHT("Low bright")
            {
                @Override
                void apply(CheckboxMenuItem checkMenuItem, DimmerManager dimmerManager)
                {
                    ScreenBlacker.clearBlackLayersIfExist(dimmerManager);
                    DimmingMode.changeBrightness(checkMenuItem, dimmerManager);
                }
            };

    private final String displayName;

    DimmingMode(String displayName)
    {
        this.displayName = displayName;
    }

    String getDisplayName()
    {
        return displayName;
    }

    /**
     * Adjusts the brightness of the given monitorMenuItem's monitor. Adds black layer or removes, depending on scenario.
     */
    abstract void apply(CheckboxMenuItem monitorMenuItem, DimmerManager dimmerManager);

    /**
     * Either dims or reverts the brightness back to former values depending on the checkmenuitem's state.
     */
    private static void changeBrightness(CheckboxMenuItem monitorMenuItem, DimmerManager dimmerManager)
    {
        MonitorInfo changedMonitorInfo = dimmerManager.findByMonitorId(monitorMenuItem.getName());
        boolean isTicked = monitorMenuItem.getState();
        if (isTicked)
        {
            dimmerManager.dimAllExcept(MonitorMenuItemsManager.getTickedMonitors(dimmerManager));
        }
        else
        {
            // When a monitor was ticked and becomes unticked, then we'll check if all the monitors are unticked.
            // If they are then revert the brightness back to original values. Otherwise dim the monitor that was just unticked.
            if (MonitorMenuItemsManager.areAllMonitorsUnticked())
            {
                dimmerManager.undimAll();
            } else
            {
                dimmerManager.dim(changedMonitorInfo);
            }
        }
    }
}
