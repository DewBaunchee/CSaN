package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import javax.crypto.Mac;

public class Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private Button scanBtn;

    @FXML
    private ProgressBar bar;

    @FXML
    void initialize() {
        scanBtn.setOnAction(e -> {
            textArea.clear();
            try {
                textArea.setText(MacScanner.scan());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
