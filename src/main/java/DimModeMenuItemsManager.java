import java.awt.CheckboxMenuItem;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dimmer.DimmerManager;
import dimmer.MonitorInfo;

/**
 * Creates menu items for each of the dim modes in {@link DimmingMode}.
 * Defaults to having `Black` as ticked on first launch.
 */
class DimModeMenuItemsManager
{
    private static List<CheckboxMenuItem> DIM_MODE_ITEMS;

    /**
     * Gets the dim modes available. Creates if not already created
     */
    static List<? extends CheckboxMenuItem> get(DimmerManager dimmerManager)
    {
        if (DIM_MODE_ITEMS == null)
        {
            DIM_MODE_ITEMS = Arrays.asList(DimmingMode.values())
                    .stream()
                    .map(dimmingMode -> createDimModeItem(dimmingMode, dimmerManager))
                    .collect(Collectors.toList());
        }
        return DIM_MODE_ITEMS;
    }

    private static CheckboxMenuItem createDimModeItem(DimmingMode dimmingMode, DimmerManager dimmerManager)
    {
        CheckboxMenuItem checkMenuItem = new CheckboxMenuItem(dimmingMode.getDisplayName());
        checkMenuItem.setName(dimmingMode.name());
        checkMenuItem.addItemListener(e -> {
            boolean isTicked = checkMenuItem.getState();
            if (isTicked)
            {
                MonitorMenuItemsManager.setDimmingMode(dimmingMode, dimmerManager);
                // Only one tick allowed from all the options. When you select this menu item, then unselect the rest
                DIM_MODE_ITEMS.stream()
                        .filter(dimMenuItem -> !dimMenuItem.getLabel().equals(checkMenuItem.getLabel()))
                        .forEach(dimMenuItemToUntick -> dimMenuItemToUntick.setState(false));

                MonitorMenuItemsManager.setDimmingMode(DimmingMode.valueOf(checkMenuItem.getName()), dimmerManager);
            }
            else
            {
                // Choose to tick the next one in line except the one that was just unticked
                CheckboxMenuItem dimMenuItemNextInLineToTick = DIM_MODE_ITEMS.stream()
                        .filter(dimMenuItem -> !dimMenuItem.getLabel().equals(checkMenuItem.getLabel()))
                        .findFirst()
                        .get();
                dimMenuItemNextInLineToTick.setState(true);

                // Update the current monitor selection's listeners and executes
                MonitorMenuItemsManager.setDimmingMode(DimmingMode.valueOf(dimMenuItemNextInLineToTick.getName()), dimmerManager);
            }
        });
        // Set BLACK ticked as default
        if (dimmingMode == DimmingMode.BLACK)
        {
            checkMenuItem.setState(true);
        }
        return checkMenuItem;
    }

    static DimmingMode getTickedDimmingMode()
    {
        return DIM_MODE_ITEMS.stream()
                .filter(CheckboxMenuItem::getState)
                .map(tickedDimItem -> DimmingMode.valueOf(tickedDimItem.getName()))
                .findFirst()
                .get();
    }
}
