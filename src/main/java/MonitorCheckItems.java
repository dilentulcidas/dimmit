import java.awt.CheckboxMenuItem;
import java.util.List;
import java.util.stream.Collectors;

import dimmer.DimmerManager;

class MonitorCheckItems
{
    private static List<CheckboxMenuItem> CHECK_MENU_ITEMS;

    static List<? extends CheckboxMenuItem> from(DimmerManager dimmerManager)
    {
        if (CHECK_MENU_ITEMS == null)
        {
            CHECK_MENU_ITEMS = dimmerManager.getInformationOfAllMonitors().stream()
                    .map(mInfo -> createMonitorItem(dimmerManager, mInfo.getMonitorId(), mInfo.isPrimary()))
                    .collect(Collectors.toList());
        }
        return CHECK_MENU_ITEMS;
    }

    private static CheckboxMenuItem createMonitorItem(DimmerManager dimmerManager, String monitorId, boolean isPrimary)
    {
        CheckboxMenuItem checkMenuItem = new CheckboxMenuItem(monitorId + ((isPrimary) ? " [Primary]" : ""));
        checkMenuItem.addItemListener(e -> {
            String changedMonitorId = checkMenuItem.getLabel().replace(" [Primary]", "");
            if (checkMenuItem.getState())
            {
                dimmerManager.dimAllExcept(getCheckedMonitorIds());
            }
            else
            {
                // When a monitor was ticked and becomes unticked, then we'll check if all the monitors are unticked.
                // If they are then revert the brightness back to original values. Otherwise dim the monitor that was just unticked.
                if (areAllMonitorsUnchecked())
                {
                    dimmerManager.undim(changedMonitorId);
                }
                else
                {
                    dimmerManager.dim(changedMonitorId);
                }
            }
        });
        return checkMenuItem;
    }

    private static List<String> getCheckedMonitorIds()
    {
        return CHECK_MENU_ITEMS.stream()
                .filter(CheckboxMenuItem::getState)
                .map(menuItem -> menuItem.getLabel().replace(" [Primary]", ""))
                .collect(Collectors.toList());
    }

    private static boolean areAllMonitorsUnchecked()
    {
        return CHECK_MENU_ITEMS.stream().noneMatch(CheckboxMenuItem::getState);
    }
}
