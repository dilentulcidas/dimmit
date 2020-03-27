package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.dialogs.ErrorDialogBuilder;

public class DimmitApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try
        {
            // Run the dimmer in the background
            OSDetector.getOperatingSystemType()
                    .getDimmerRunner()
                    .run();

            // Add system tray
            TrayAdder.addDimmerIconToTray();
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
