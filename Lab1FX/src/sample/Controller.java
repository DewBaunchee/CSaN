package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private Button scanBtn;

    @FXML
    private Button findBtn;

    @FXML
    private ChoiceBox<String> networkList;

    public ObservableList<String> list;

    @FXML
    void initialize() {
        list = FXCollections.observableArrayList();
        networkList.setItems(list);

        findBtn.setOnAction(e -> {
            try {
                list.addAll(MacScanner.findInterfaces());
            } catch (UnknownHostException | SocketException unknownHostException) {
                unknownHostException.printStackTrace();
            }
        });

        scanBtn.setOnAction(e -> {
            textArea.clear();
            try {
                ThreadScanner threadScanner = new ThreadScanner(textArea, networkList.getValue());
                Thread thread = new Thread(threadScanner);
                thread.start();
            } catch (Exception exception) {
                textArea.setText(exception.getMessage());
            }
        });
    }

    static class ThreadScanner implements Runnable {

        TextArea textArea;
        byte[] subnet;

        ThreadScanner(TextArea inTextArea, String inSubnet) {
            textArea = inTextArea;
            if(inSubnet == null) {
                subnet = null;
            } else {
                subnet = MacScanner.strToAddr(inSubnet, 3);
            }
        }

        @Override
        public void run() {
            try {
                textArea.setText("Scanning...");
                if(subnet == null) {
                    textArea.setText(MacScanner.scan());
                } else {
                    textArea.setText(MacScanner.scanLocalNetwork(subnet));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
