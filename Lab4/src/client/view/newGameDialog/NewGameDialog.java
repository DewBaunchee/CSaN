package client.view.newGameDialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class NewGameDialog extends Dialog<NewGameDialogResult> {

    public NewGameDialogResult result;

    public NewGameDialog(Window owner, NewGameDialogResult inputResult) throws IOException {
        result = inputResult;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newGameDialog.fxml"));
        Parent root = loader.load();
        getDialogPane().setContent(root);
        getDialogPane().setMinWidth(400);
        getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {
            ((Stage) getDialogPane().getScene().getWindow()).close();
        });
        ((NewGameDialogController) loader.getController()).setResult(result);

        setTitle("New game...");
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
    }
}
