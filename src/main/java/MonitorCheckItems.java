import java.awt.CheckboxMenuItem;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dimmer.DimmerManager;
import dimmer.MonitorInfo;
import dimmer.OSType;

class MonitorCheckItems
{
    private static List<CheckboxMenuItem> CHECK_MENU_ITEMS;
    private static final String PRIMARY_IDENTIFIER = " - [Primary]";

    static List<? extends CheckboxMenuItem> from(DimmerManager dimmerManager)
    {
        if (CHECK_MENU_ITEMS == null)
        {
            CHECK_MENU_ITEMS = dimmerManager.getInformationOfAllMonitors().stream()
                    .map(mInfo -> createMonitorItem(dimmerManager, mInfo))
                    .collect(Collectors.toList());
        }
        return CHECK_MENU_ITEMS;
    }

    private static CheckboxMenuItem createMonitorItem(DimmerManager dimmerManager, MonitorInfo monitorInfo)
    {
        CheckboxMenuItem checkMenuItem = new CheckboxMenuItem(monitorInfo.getMenuItemName() + ((monitorInfo.isPrimary()) ? PRIMARY_IDENTIFIER : ""));
        checkMenuItem.setName(monitorInfo.getMonitorId()); // The checkmenuitem's name will store the monitor's model
        checkMenuItem.addItemListener(e -> {
            MonitorInfo changedMonitorInfo = dimmerManager.findByMonitorId(
                    checkMenuItem.getName().replace(PRIMARY_IDENTIFIER, ""));
            boolean isTicked = checkMenuItem.getState();
            if (isTicked)
            {
                dimmerManager.dimAllExcept(getCheckedMonitorIds(dimmerManager));
            }
            else
            {
                // When a monitor was ticked and becomes unticked, then we'll check if all the monitors are unticked.
                // If they are then revert the brightness back to original values. Otherwise dim the monitor that was just unticked.
                if (areAllMonitorsUnchecked())
                {
                    dimmerManager.undim(changedMonitorInfo);
                } else
                {
                    dimmerManager.dim(changedMonitorInfo);
                }
            }

            /**
             * In Windows there's a limitation with the WMI API which is in charge of
             * adjusting the brightness of screens which are adjustable through software
             * (such as laptop screens). When you adjust the brightness of one of these screens
             * it will adjust for all other screens too.
             *
             * So if you have two laptop screens connected then it'll adjust the brightness for all these screens at once.
             *
             * We're going to handle the ticks differently in Windows case due to this. If you tick
             * a dynamically adjustable brightness monitor, then it'll tick all of those. Same with unticking.
             *
             * This peculiarity is handled in {@link dimmer.DimmerForWindows}
             */
            if (OSDetector.getOperatingSystemType() == OSType.Windows && changedMonitorInfo.isBrightnessDynamicallyAdjustable())
            {
                // Tick/untick the other dynamically adjustable monitors too
                dimmerManager.getInformationOfAllMonitors()
                        .stream()
                        .filter(MonitorInfo::isBrightnessDynamicallyAdjustable)
                        .forEach(dynamicBrightnessAdjustMonitor -> {
                            CHECK_MENU_ITEMS.stream()
                                    .filter(monitorCheckBoxItem -> monitorCheckBoxItem.getName().equalsIgnoreCase(dynamicBrightnessAdjustMonitor.getMonitorId()))
                                    .findFirst()
                                    .get()
                                    .setState(isTicked);
                        });
            }
        });
        return checkMenuItem;
    }

    private static List<MonitorInfo> getCheckedMonitorIds(DimmerManager dimmerManager)
    {
        return CHECK_MENU_ITEMS.stream()
                .filter(CheckboxMenuItem::getState)
                .map(menuItem -> menuItem.getName().replace(PRIMARY_IDENTIFIER, ""))
                .map(dimmerManager::findByMonitorId)
                .collect(Collectors.toList());
    }

    private static boolean areAllMonitorsUnchecked()
    {
        return CHECK_MENU_ITEMS.stream().noneMatch(CheckboxMenuItem::getState);
    }
}
