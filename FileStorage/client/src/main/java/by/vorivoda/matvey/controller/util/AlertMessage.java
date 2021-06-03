package by.vorivoda.matvey.controller.util;

import javafx.scene.control.Alert;

public enum AlertMessage {
    ERROR_WHEN_SWITCHING_SCENES("Error", "Error during switching scenes", Alert.AlertType.ERROR),
    ERROR_WHEN_OPENING_FILES("Error", "Cannot open file", Alert.AlertType.ERROR);

    private final String title;
    private final String message;
    private final Alert.AlertType alertType;

    AlertMessage(String title, String message, Alert.AlertType alertType) {
        this.title = title;
        this.message = message;
        this.alertType = alertType;
    }

    public void showAndWait() {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
