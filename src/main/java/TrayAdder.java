import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dimmer.DimmerManager;
import javafx.application.Platform;

/**
 * Adds this app to the system tray when first run.
 * Using Java Swing for this as JavaFX doesn't support this yet.
 *
 * Loads the monitors available and puts them selectable in the menu item when right clicked.
 * The monitor item you tick will be kept undimmed, while the rest will automatically be dimmed.
 * Untick all monitors and everything will be back to the former brightness values.
 */
class TrayAdder
{
    private static final String TRAY_ICON_URL =
            "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";

    public static void addDimmerIconToTray(DimmerManager dimmerManager)
    {
        SwingUtilities.invokeLater(() -> setTray(dimmerManager));
    }

    private static void setTray(DimmerManager dimmerManager)
    {
        try
        {
            // Ensure awt toolkit is initialized.
            Toolkit.getDefaultToolkit();

            // App requires system tray support, just exit if there is no support
            if (!SystemTray.isSupported())
            {
                JOptionPane.showMessageDialog(null, "System tray support is required to run this application! Exiting.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Set up the system tray
            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = createTrayIcon(TRAY_ICON_URL);
            trayIcon.setToolTip("Dimmer");
            trayIcon.setPopupMenu(createPopupMenu(systemTray, trayIcon, dimmerManager));
            systemTray.add(trayIcon);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static TrayIcon createTrayIcon(String imgUrl) throws IOException
    {
        URL imageLoc = new URL(imgUrl);
        Image image = ImageIO.read(imageLoc);
        return new TrayIcon(image);
    }

    private static PopupMenu createPopupMenu(SystemTray systemTray, TrayIcon trayIcon, DimmerManager dimmerManager)
    {
        // Set up monitors selection question menu item
        MenuItem monitorChoicesMenuItem = new MenuItem("Choose which to keep undimmed");
        monitorChoicesMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        // Set up `About` menu item
        MenuItem aboutItem = new MenuItem("About");
        String aboutMessage =
                "Dimmer is a lightweight application suitable for multiple displays.\n\n" +
                "Select which monitors you want to keep undimmed and this app will dim the remaining connected monitors to the minimum brightness.\n" +
                "Reverts to the previous brightness values once you're happy to have all monitors undimmed.\n\n" +
                "Developed by Johnny Deep ©2020";
        aboutItem.addActionListener(event ->
        {
            try
            {
                Image iconImg = new ImageIcon(ImageIO.read(new URL("https://i.imgur.com/dx99cio.png"))).getImage();
                JOptionPane.showMessageDialog(null, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(iconImg));
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(null, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Set up exit menu item for the popup
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(event -> {
            Platform.exit();
            systemTray.remove(trayIcon);
        });

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(monitorChoicesMenuItem);
        createMonitorItems(dimmerManager).forEach(popupMenu::add); // Add the monitor checkbox selections
        popupMenu.addSeparator();
        popupMenu.add(aboutItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
        return popupMenu;
    }

    private static List<? extends MenuItem> createMonitorItems(DimmerManager dimmerManager)
    {
        return MonitorCheckItems.from(dimmerManager);
    }
}
