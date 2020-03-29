import java.awt.CheckboxMenuItem;
import java.awt.MenuComponent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dimmer.DimmerManager;
import dimmer.MonitorInfo;

class MonitorMenuItemsManager
{
    private static List<CheckboxMenuItem> CHECK_MENU_ITEMS;

    private static final String PRIMARY_IDENTIFIER = " - [Primary]";

    /**
     * Creates a new list of monitor menu items. If already exists then simply
     * gets the already existing list reference.
     */
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

    /**
     * Gets the monitor menu item reference from {@link MonitorInfo} object
     */
    static CheckboxMenuItem findMonitorMenuItemFrom(MonitorInfo monitorInfo)
    {
        return CHECK_MENU_ITEMS.stream()
                .filter(monitorMenuItem -> monitorMenuItem.getName().equals(monitorInfo.getMonitorId()))
                .findFirst()
                .get();
    }

    /**
     * Updates the itemlisteners of all the checkmenuitems
     * @param dimmingMode Chosen dimming mode
     */
    static void setDimmingMode(DimmingMode dimmingMode, DimmerManager dimmerManager)
    {
        List<CheckboxMenuItem> monitorItems = (List<CheckboxMenuItem>) from(dimmerManager);
        monitorItems.forEach(monitorItem -> {
            // Remove all previous listeners
            Arrays.asList(monitorItem.getItemListeners()).forEach(monitorItem::removeItemListener);

            // Set new listener
            monitorItem.addItemListener(e -> dimmingMode.apply(monitorItem, dimmerManager));
        });

        // Update current monitor selections
        monitorItems.forEach(monitorItem -> dimmingMode.apply(monitorItem, dimmerManager));
    }

    static List<MonitorInfo> getTickedMonitors(DimmerManager dimmerManager)
    {
        return CHECK_MENU_ITEMS.stream()
                .filter(CheckboxMenuItem::getState)
                .map(MenuComponent::getName)
                .map(dimmerManager::findByMonitorId)
                .collect(Collectors.toList());
    }

    static List<MonitorInfo> getUntickedMonitors(DimmerManager dimmerManager)
    {
        return CHECK_MENU_ITEMS.stream()
                .filter(checkboxMenuItem -> !checkboxMenuItem.getState())
                .map(MenuComponent::getName)
                .map(dimmerManager::findByMonitorId)
                .collect(Collectors.toList());
    }

    static boolean areAllMonitorsTicked()
    {
        return CHECK_MENU_ITEMS.stream().allMatch(CheckboxMenuItem::getState);
    }

    static boolean areAllMonitorsUnticked()
    {
        return CHECK_MENU_ITEMS.stream().noneMatch(CheckboxMenuItem::getState);
    }

    private static CheckboxMenuItem createMonitorItem(DimmerManager dimmerManager, MonitorInfo monitorInfo)
    {
        CheckboxMenuItem checkMenuItem = new CheckboxMenuItem(monitorInfo.getMenuItemName() + ((monitorInfo.isPrimary()) ? PRIMARY_IDENTIFIER : ""));
        checkMenuItem.setName(monitorInfo.getMonitorId()); // The checkmenuitem's name will store the monitor's model
        checkMenuItem.addItemListener(e -> DimModeMenuItemsManager.getTickedDimmingMode().apply(checkMenuItem, dimmerManager));
        return checkMenuItem;
    }
}
