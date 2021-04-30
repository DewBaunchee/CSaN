package client.view.joinToServerDialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class JoinToServerDialog extends Dialog<JoinToServerDialogResult> {

    public JoinToServerDialogResult result;

    public JoinToServerDialog(Window owner, JoinToServerDialogResult inputResult) throws IOException {
        result = inputResult;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("joinToServer.fxml"));
        Parent root = loader.load();
        getDialogPane().setContent(root);
        getDialogPane().setMinWidth(400);
        getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {
            ((Stage) getDialogPane().getScene().getWindow()).close();
        });
        ((JoinToServerDialogController) loader.getController()).setResult(result);

        setTitle("Joining server...");
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
    }
}
