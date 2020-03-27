package dialogs;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ErrorDialogBuilder
{
    public static ErrorMessageSetter create()
    {
        return new Dialog();
    }

    private static class Dialog implements ErrorDialogBuilder.ErrorMessageSetter, ErrorDialogBuilder.DialogActions
    {
        private Alert alert;
        private Runnable onOk;
        private Runnable onCancel;
        private Runnable onExit;

        @Override
        public DialogActions withErrorMsg(String errMsg)
        {
            this.alert = new Alert(Alert.AlertType.ERROR, errMsg, ButtonType.OK);
            return this;
        }

        @Override
        public DialogActions onOk(Runnable onOk)
        {
            this.onOk = onOk;
            return this;
        }

        @Override
        public DialogActions onCancel(Runnable onCancel)
        {
            this.onCancel = onCancel;
            return this;
        }

        /**
         * Triggered when no button is pressed, the user just exists the dialog
         */
        @Override
        public DialogActions onExit(Runnable onExit)
        {
            this.onExit = onExit;
            return this;
        }

        /**
         * Regardless of what the user does in the dialog, it'll shut down the program
         */
        @Override
        public void showAndShutdown()
        {
            alert.showAndWait();
            Platform.exit();
        }

        /**
         * Runs any of the runnables set after the user does something with the dialog
         */
        @Override
        public void show()
        {
            Optional<ButtonType> result = alert.showAndWait();
            if(!result.isPresent())
            {
                Optional.ofNullable(onExit).ifPresent(Runnable::run);
            }
            else if(result.get() == ButtonType.OK)
            {
                Optional.ofNullable(onOk).ifPresent(Runnable::run);
            }
            else if(result.get() == ButtonType.CANCEL)
            {
                Optional.ofNullable(onCancel).ifPresent(Runnable::run);
            }
        }
    }

    public interface ErrorMessageSetter
    {
        DialogActions withErrorMsg(String errMsg);
    }
    public interface DialogActions
    {
        DialogActions onOk(Runnable onOk);
        DialogActions onCancel(Runnable onCancel);
        DialogActions onExit(Runnable onExit);
        void show();
        void showAndShutdown();
    }
}
