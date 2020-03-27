package sample;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import javafx.application.Platform;
import sample.dialogs.ErrorDialogBuilder;
import sample.dialogs.SimpleDialogBuilder;

/**
 * Adds this app to the system tray when first run.
 * Using Java Swing for this as JavaFX doesn't support this yet.
 */
public class TrayAdder
{
    private static final String TRAY_ICON_URL =
            "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";

    public static void addDimmerIconToTray()
    {
        javax.swing.SwingUtilities.invokeLater(TrayAdder::setTray);
    }

    private static void setTray()
    {
        try
        {
            // Ensure awt toolkit is initialized.
            Toolkit.getDefaultToolkit();

            // App requires system tray support, just exit if there is no support
            if (!SystemTray.isSupported())
            {
                ErrorDialogBuilder.create()
                        .withErrorMsg("System tray support required to run this application! Exiting.")
                        .showAndShutdown();
            }

            // Set up the system tray
            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = createTrayIcon(TRAY_ICON_URL);
            trayIcon.setToolTip("Dimmer");
            trayIcon.setPopupMenu(createPopupMenu(systemTray, trayIcon));
            systemTray.add(trayIcon);
        }
        catch (Exception e)
        {
            ErrorDialogBuilder.create()
                    .withErrorMsg(e.getMessage())
                    .showAndShutdown();
        }
    }

    private static TrayIcon createTrayIcon(String imgUrl) throws IOException
    {
        URL imageLoc = new URL(imgUrl);
        Image image = ImageIO.read(imageLoc);
        return new TrayIcon(image);
    }

    private static PopupMenu createPopupMenu(SystemTray systemTray, TrayIcon trayIcon)
    {
        // Set up exit menu item for the popup
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(event -> {
            Platform.exit();
            systemTray.remove(trayIcon);
        });

        // Set up `About` menu item
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(event -> {
            Platform.runLater(() ->
                SimpleDialogBuilder.create()
                        .setTitle("About")
                        .setContentText("Dimmer is a lightweight application suitable for multiple displays.\n\n" +
                                "Dims the other displays except the displays which have a full screen app open. " +
                                "Reverts to former brightness levels when full screen app is exited.\n\n" +
                                "Developed by Johnny Deep ©2020")
                        .show());
        });

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(aboutItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
        return popupMenu;
    }
}
