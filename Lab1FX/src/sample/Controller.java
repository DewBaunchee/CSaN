package sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea textArea;

    @FXML
    private Button scanBtn;

    @FXML
    void initialize() throws Exception {
        scanBtn.setOnAction(e -> {
            textArea.clear();
            try {
                textArea.setText(MacScanner.scanLocalNetwork());
            } catch (Exception exception) {
                textArea.setText(exception.getMessage());
            }
        });
    }
}
