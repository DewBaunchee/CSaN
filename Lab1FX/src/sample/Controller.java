package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private Button scanBtn;

    @FXML
    private ProgressBar bar;

    @FXML
    void initialize() throws Exception {
        textArea.setText(MacScanner.scan());
        scanBtn.setOnAction(e -> {
            textArea.clear();
            try {
                textArea.setText(MacScanner.scan());
            } catch (Exception exception) {
                textArea.setText(exception.getMessage());
            }
        });
    }
}
