package by.vorivoda.matvey.controller.component.folder;

import by.vorivoda.matvey.controller.component.folder.factory.event.FolderElementEvent;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public abstract class ContainingElements {
    private FolderElementEvent<MouseEvent> onMouseClicked;
    private FolderElementEvent<ActionEvent> onDeleteRequest;
    private FolderElementEvent<ActionEvent> onOpenRequest;
    private FolderElementEvent<ActionEvent> onSaveRequest;
    private FolderElementEvent<ActionEvent> onMoveRequest;
    private FolderElementEvent<ActionEvent> onCopyRequest;
    private FolderElementEvent<ActionEvent> onPasteRequest;
    private FolderElementEvent<ActionEvent> onSizeRequest;

    public void setOnMouseClicked(FolderElementEvent<MouseEvent> event) {
        onMouseClicked = event;
    }

    public void onMouseClicked(MouseEvent event, String name) {
        if(onMouseClicked != null) onMouseClicked.handle(event, name);
    }

    public void setOnDeleteRequest(FolderElementEvent<ActionEvent> onDeleteRequest) {
        this.onDeleteRequest = onDeleteRequest;
    }

    public void onDeleteRequest(ActionEvent event, String name) {
        if(onDeleteRequest != null) onDeleteRequest.handle(event, name);
    }

    public void setOnOpenRequest(FolderElementEvent<ActionEvent> onOpenRequest) {
        this.onOpenRequest = onOpenRequest;
    }

    public void onOpenRequest(ActionEvent event, String name) {
        if(onOpenRequest != null) onOpenRequest.handle(event, name);
    }

    public void setOnMoveRequest(FolderElementEvent<ActionEvent> onMoveRequest) {
        this.onMoveRequest = onMoveRequest;
    }

    public void onMoveRequest(ActionEvent event, String name) {
        if(onMoveRequest != null) onMoveRequest.handle(event, name);
    }

    public void setOnCopyRequest(FolderElementEvent<ActionEvent> onCopyRequest) {
        this.onCopyRequest = onCopyRequest;
    }

    public void onCopyRequest(ActionEvent event, String name) {
        if(onCopyRequest != null) onCopyRequest.handle(event, name);
    }

    public void setOnPasteRequest(FolderElementEvent<ActionEvent> onPasteRequest) {
        this.onPasteRequest = onPasteRequest;
    }

    public void onPasteRequest(ActionEvent event, String name) {
        if(onPasteRequest != null) onPasteRequest.handle(event, name);
    }

    public void setOnSaveRequest(FolderElementEvent<ActionEvent> onSaveRequest) {
        this.onSaveRequest = onSaveRequest;
    }

    public void onSaveRequest(ActionEvent event, String name) {
        if(onSaveRequest != null) onSaveRequest.handle(event, name);
    }

    public void setOnSizeRequest(FolderElementEvent<ActionEvent> onSizeRequest) {
        this.onSizeRequest = onSizeRequest;
    }

    public void onSizeRequest(ActionEvent event, String name) {
        if(onSizeRequest != null) onSizeRequest.handle(event, name);
    }
}
