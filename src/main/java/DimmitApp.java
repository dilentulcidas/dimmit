
import dialogs.ErrorDialogBuilder;
import dimmer.DimmerManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class DimmitApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try
        {
            // At first launch will fetches all the monitors connected to the system
            DimmerManager dimmerManager = OSDetector.getOperatingSystemType().getDimmerRunner();

            // Add system tray
            TrayAdder.addDimmerIconToTray(dimmerManager);
        }
        catch (Exception e)
        {
            ErrorDialogBuilder.create()
                    .withErrorMsg(e.getMessage())
                    .showAndShutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
