import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JWindow;

import dimmer.DimmerManager;
import dimmer.MonitorInfo;

/**
 * Adds/removes a black layer to the screen with the given index.
 * Uses a cache system to keep track of the blacked monitors.
 */
class ScreenBlacker
{
    private static Map<Integer, Rectangle> MONITOR_BOUNDS_MAP = new HashMap<>(); // Starts with 0
    private static Map<String, LayerInstance> CACHED_BLACKED_SCREENS = new HashMap<>();

    private ScreenBlacker() {}

    private static class LayerInstance extends JWindow
    {
        LayerInstance() {}
    }

    /**
     * On initialization it fetches the sizes of all the monitors and stores
     * them in the `MONITOR_BOUNDS_MAP`.
     */
    public static void init()
    {
        List<GraphicsDevice> graphicsDevices = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices());
        for (int i = 0; i < graphicsDevices.size(); i++)
        {
            MONITOR_BOUNDS_MAP.put(i, graphicsDevices.get(i).getDefaultConfiguration().getBounds());
        }
    }

    static void addBlackLayerTo(List<MonitorInfo> monitors, DimmerManager dimmerManager, DimmingMode dimmingMode)
    {
        monitors.forEach(monitor -> {
            if (CACHED_BLACKED_SCREENS.containsKey(monitor.getMonitorId()))
            {
                System.out.println("Monitor with id ["+monitor.getMonitorId()+"] is already blacked.");
                return;
            }

            // Get the size of the given monitor from its index
            Rectangle bounds = MONITOR_BOUNDS_MAP.get(monitor.getMonitorIndex() - 1);

            // Set up layerinstance object and cache it so that we can keep track of the monitors which are blacked
            LayerInstance blackLayerInstance = new LayerInstance();
            blackLayerInstance.getContentPane().setBackground(Color.black);
            blackLayerInstance.setLocation(bounds.getLocation());
            blackLayerInstance.setSize(bounds.getSize());
            blackLayerInstance.setVisible(true);

            // Add exit button
            final JButton button = new JButton();
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.addActionListener(e ->
            {
                // When clicking the black layer, remove the black layer.
                // Also tick the monitor's menu item and revert the brightness back to default
                removeBlackLayerFrom(monitor);
                CheckboxMenuItem monitorMenuItem = MonitorMenuItemsManager.findMonitorMenuItemFrom(monitor);
                monitorMenuItem.setState(true);
                dimmingMode.apply(monitorMenuItem, dimmerManager);
            });

            blackLayerInstance.add(button, BorderLayout.CENTER);
            blackLayerInstance.validate();
            blackLayerInstance.setAlwaysOnTop(true);
            CACHED_BLACKED_SCREENS.put(monitor.getMonitorId(), blackLayerInstance);
        });
    }

    /**
     * Removes black layer from all monitors
     */
    static void clearBlackLayersIfExist(DimmerManager dimmerManager)
    {
        if (!CACHED_BLACKED_SCREENS.isEmpty())
        {
            dimmerManager.getInformationOfAllMonitors().forEach(ScreenBlacker::removeBlackLayerFrom);
        }
    }

    private static void removeBlackLayerFrom(MonitorInfo monitor)
    {
        if (!CACHED_BLACKED_SCREENS.containsKey(monitor.getMonitorId()))
        {
            System.out.println("No black layer on monitor with id ["+monitor.getMonitorId()+"]. Can't remove black layer if there ain't any.");
            return;
        }

        // Scrape the black layer and remove from cache
        LayerInstance layerInstance = CACHED_BLACKED_SCREENS.get(monitor.getMonitorId());
        layerInstance.getContentPane().removeAll();
        layerInstance.repaint();
        layerInstance.setVisible(false);
        CACHED_BLACKED_SCREENS.remove(monitor.getMonitorId());
    }
}
