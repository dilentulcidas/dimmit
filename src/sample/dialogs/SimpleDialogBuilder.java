package sample.dialogs;

import java.util.function.Supplier;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Creates a simple dialog with `Ok` button.
 *
 * Note: if there's error `IllegalStateException: Not on FX application thread` then you
 * have to run this under `Platform.runLater(...)`.
 */
public class SimpleDialogBuilder
{
    public static TitleSetter create()
    {
        return new Dialog();
    }

    private static class Dialog implements TitleSetter, DialogOptions
    {
        private javafx.scene.control.Dialog dialog = new javafx.scene.control.Dialog();


        @Override
        public DialogOptions setTitle(String title)
        {
            dialog.setTitle(title);
            return this;
        }

        @Override
        public DialogOptions setHeaderText(String headerText)
        {
            dialog.setHeaderText(headerText);
            return this;
        }

        @Override
        public DialogOptions setHeaderIcon(String imgUrl)
        {
            dialog.setGraphic(new ImageView(new Image(imgUrl)));
            return this;
        }

        @Override
        public DialogOptions setContentText(String text)
        {
            dialog.getDialogPane().setContentText(text);
            return this;
        }

        @Override
        public void show()
        {
            dialog.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
            dialog.showAndWait();
        }
    }

    public interface TitleSetter
    {
        DialogOptions setTitle(String title);
    }
    public interface DialogOptions
    {
        DialogOptions setHeaderText(String headerText);
        DialogOptions setHeaderIcon(String imgUrl);
        DialogOptions setContentText(String text);
        void show();
    }
}