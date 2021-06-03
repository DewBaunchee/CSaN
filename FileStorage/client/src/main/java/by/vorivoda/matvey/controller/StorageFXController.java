package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.ApplicationScene;
import by.vorivoda.matvey.ClientApplication;
import by.vorivoda.matvey.controller.component.folder.structure.view.FolderStructureView;
import by.vorivoda.matvey.controller.component.folder.view.FolderView;
import by.vorivoda.matvey.controller.util.operating.system.CurrentOS;
import by.vorivoda.matvey.model.IRemoteStorageClient;
import by.vorivoda.matvey.model.RemoteStorageClient;
import by.vorivoda.matvey.model.StorageStateBindings;
import by.vorivoda.matvey.model.util.FilesMethods;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StorageFXController extends CommonController {

    private static final String ABOUT_PROGRAM = "Remote storage can save your files in a safe place.";
    private static final String ABOUT_AUTHOR = "Vorivoda Matvey (Student of group 951007, BSUIR - Belarus)";

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane structurePane;

    @FXML
    private TreeView<HBox> storageStructure;

    @FXML
    private ScrollPane folderElementsScrollBar;

    @FXML
    private VBox folderElements;

    @FXML
    private AnchorPane filePreviewPane;

    @FXML
    private VBox fileInfoVBox;

    @FXML
    private Button openFileBtn;

    @FXML
    private Button saveFileBtn;

    @FXML
    private AnchorPane closeBtn;

    @FXML
    private HBox titleHBox;

    @FXML
    private MenuItem mmOpen;

    @FXML
    private MenuItem mmSave;

    @FXML
    private MenuItem mmExit;

    @FXML
    private MenuItem mmCopy;

    @FXML
    private MenuItem mmMove;

    @FXML
    private MenuItem mmPaste;

    @FXML
    private MenuItem mmDelete;

    @FXML
    private MenuItem mmNewFolder;

    @FXML
    private MenuItem mmUpload;

    @FXML
    private MenuItem mmRefresh;

    @FXML
    private MenuItem mmBack;

    @FXML
    private CheckMenuItem mmShowStructure;

    @FXML
    private CheckMenuItem mmShowFilePreview;

    @FXML
    private MenuItem mmAboutProgram;

    @FXML
    private MenuItem mmAboutAuthor;

    @FXML
    private VBox backBtn;

    @FXML
    private VBox refreshBtn;

    @FXML
    private VBox uploadBtn;

    @FXML
    private TextField currentFolderPath;

    private IRemoteStorageClient storage;
    private StorageStateBindings storageState;
    private StringProperty folderCreatingName;
    private StringProperty src;
    private final double structureShowingOnThisDividerPosition = 0.2;
    private final double previewShowingOnThisDividerPosition = 0.75;
    private final double widthOfOpenedPane = 150;

    private enum COPY_MOVE {NOTHING, COPY, MOVE}

    private COPY_MOVE currentCopyMoveState;

    @FXML
    void initialize() {
        folderCreatingName = new SimpleStringProperty();
        src = new SimpleStringProperty();
        currentCopyMoveState = COPY_MOVE.NOTHING;

        filePreviewPane.setVisible(false);

        storage = (RemoteStorageClient) ClientApplication.getContext().getBean("StorageClient");
        storageState = storage.getState();

        folderElements.prefWidthProperty().bind(folderElementsScrollBar.widthProperty().multiply(0.9));
        folderElements.prefHeightProperty().bind(folderElementsScrollBar.heightProperty().multiply(0.9));

        currentFolderPath.textProperty().bind(storageState.currentFolderProperty());

        FolderView folderView = new FolderView(folderElements);
        folderView.currentElementsProperty().bind(storageState.currentFolderElementsProperty());

        FolderStructureView folderStructureView = new FolderStructureView(storageStructure);
        folderStructureView.currentElementsProperty().bind(storageState.allPathsProperty());

        currentFolderPath.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                currentFolderPath.textProperty().unbind();
            } else {
                if (!currentFolderPath.getText().equals(storageState.getCurrentFolder())) {
                    storageState.setCurrentFolder(currentFolderPath.getText());
                }
                currentFolderPath.textProperty().bind(storageState.currentFolderProperty());
            }
        });

        currentFolderPath.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                refreshBtn.requestFocus();
        });

        EventHandler<MouseEvent> refreshEvent = mouseEvent -> {
            try {
                storage.open(storageState.getCurrentFolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        EventHandler<MouseEvent> backEvent = mouseEvent -> {
            try {
                String current = storageState.getCurrentFolder();
                if (current.lastIndexOf("/") == 0) {
                    current = "/";
                } else {
                    current = current.substring(0, current.lastIndexOf("/"));
                }
                storage.open(current);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        EventHandler<MouseEvent> storageStructureClickEvent = mouseEvent -> {
            try {
                if (mouseEvent.getButton() == MouseButton.SECONDARY)
                    storage.refreshAllPaths();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        EventHandler<MouseEvent> uploadRequest = mouseEvent -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose file for uploading");
            File file = fc.showOpenDialog(uploadBtn.getScene().getWindow());

            if (file == null) return;

            try {
                storage.uploadFile(storage.getStoragePath(file.getName()), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        EventHandler<ActionEvent> openFileRequest = actionEvent -> {
            boolean isOpened = CurrentOS.open(new File(storageState.getCurrentFileInfo().getRealPath()));
            if (!isOpened) alert("Error when opening file.", openFileBtn.getScene().getWindow());
        };

        EventHandler<ActionEvent> saveFileRequest = actionEvent -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose safe place :)");
            File file = fc.showSaveDialog(saveFileBtn.getScene().getWindow());
            if (file == null) return;

            try {
                Files.write(
                        file.toPath(),
                        Files.readAllBytes(Paths.get(storageState.getCurrentFileInfo().getRealPath()))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        storageState.currentFileProperty().addListener((observableValue, oldValue, newValue) -> {
            fileInfoVBox.getChildren().clear();
            fileInfoVBox.getChildren().add(new Label("Location in storage: " + storageState.getCurrentFileInfo().getResourcePath()));
            fileInfoVBox.getChildren().add(new Label("Size: " + storageState.getCurrentFileInfo().getSize() + " bytes."));
            // TODO Resource Preview

            filePreviewPane.setVisible(true);
        });

        EventHandler<ActionEvent> createFolderRequest = actionEvent -> {
            showModal(folderCreatingName, ApplicationScene.FOLDER_NAME_REQUESTER, folderElements.getScene().getWindow());
            if (folderCreatingName.get().length() == 0) return;

            storage.createFolder(storage.getStoragePath(folderCreatingName.get()));
        };

        EventHandler<ActionEvent> folderPasteRequest = actionEvent -> {
            if(currentCopyMoveState != COPY_MOVE.NOTHING)
                pasteRequestHandled(storage.getStoragePath(FilesMethods.getFullName(src.get())));
        };

        ContextMenu folderViewMenu = new ContextMenu();
        folderViewMenu.setStyle("-fx-background-color: #000");
        folderViewMenu.setOnCloseRequest(windowEvent -> folderViewMenu.hide());

        MenuItem createFolderItem = new MenuItem("Create folder");
        createFolderItem.setOnAction(createFolderRequest);
        folderViewMenu.getItems().add(createFolderItem);

        MenuItem uploadFileItem = new MenuItem("Upload file");
        uploadFileItem.setOnAction(actionEvent -> uploadRequest.handle(null));
        folderViewMenu.getItems().add(uploadFileItem);

        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(actionEvent ->
                folderPasteRequest.handle(null));
        folderViewMenu.getItems().add(pasteItem);

        folderElements.setOnContextMenuRequested(contextMenuEvent -> {
            if (contextMenuEvent.getSource().equals(contextMenuEvent.getTarget()))
                folderViewMenu.show(folderElements,
                        contextMenuEvent.getSceneX() + folderElements.getScene().getWindow().getX(),
                        contextMenuEvent.getSceneY() + folderElements.getScene().getWindow().getY());
        });
        folderViewMenu.setOnCloseRequest(windowEvent -> folderViewMenu.hide());

        folderView.setOnMouseClicked((mouseEvent, name) -> {
            try {
                if (mouseEvent.getButton() == MouseButton.PRIMARY)
                    if(mouseEvent.getClickCount() == 2) {
                        storage.open(storage.getStoragePath(name));
                        openFileRequest.handle(null);
                    } else {
                        storage.open(storage.getStoragePath(name));
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        refreshBtn.setOnMouseClicked(refreshEvent);
        backBtn.setOnMouseClicked(backEvent);
        storageStructure.setOnMouseClicked(storageStructureClickEvent);
        saveFileBtn.setOnAction(saveFileRequest);
        openFileBtn.setOnAction(openFileRequest);
        uploadBtn.setOnMouseClicked(uploadRequest);

        folderView.setOnCopyRequest((actionEvent, name) -> {
            currentCopyMoveState = COPY_MOVE.COPY;
            src.set(storage.getStoragePath(name));
        });
        folderView.setOnMoveRequest((actionEvent, name) -> {
            currentCopyMoveState = COPY_MOVE.MOVE;
            src.set(storage.getStoragePath(name));
        });
        folderView.setOnPasteRequest((actionEvent, name) -> pasteRequestHandled(storageState.getCurrentFolder() + "/" + name));
        folderView.setOnDeleteRequest((actionEvent, name) -> storage.delete(storage.getStoragePath(name)));
        folderView.setOnOpenRequest((actionEvent, name) -> {
            try {
                storage.open(storage.getStoragePath(name));
                openFileRequest.handle(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        folderView.setOnSaveRequest((actionEvent, name) -> {
            try {
                storage.open(storage.getStoragePath(name));
                saveFileRequest.handle(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        folderView.setOnSizeRequest((actionEvent, name) -> {
            alert("Size of " + name + ": " + storage.sizeOf(storage.getStoragePath(name)) + "", folderElements.getScene().getWindow());
        });

// ----------------- MENU INITIALIZING ------------------------

        mmOpen.setOnAction(openFileRequest);
        mmSave.setOnAction(saveFileRequest);
        mmExit.setOnAction(actionEvent -> Platform.exit());
        mmCopy.setOnAction(actionEvent -> {
            String currentFile = storageState.getCurrentFile();
            if (currentFile != null && currentFile.length() > 0) {
                currentCopyMoveState = COPY_MOVE.COPY;
                src.set(storageState.getCurrentFileInfo().getResourcePath());
            }
        });
        mmMove.setOnAction(actionEvent -> {
            String currentFile = storageState.getCurrentFile();
            if (currentFile != null && currentFile.length() > 0) {
                currentCopyMoveState = COPY_MOVE.MOVE;
                src.set(storageState.getCurrentFileInfo().getResourcePath());
            }
        });
        mmPaste.setOnAction(folderPasteRequest);
        mmDelete.setOnAction(actionEvent -> {
            String currentFile = storageState.getCurrentFile();
            if (currentFile != null && currentFile.length() > 0)
                storage.delete(storageState.getCurrentFileInfo().getResourcePath());
        });
        mmNewFolder.setOnAction(createFolderItem.getOnAction());
        mmUpload.setOnAction(uploadFileItem.getOnAction());
        mmRefresh.setOnAction(actionEvent -> refreshEvent.handle(null));
        mmBack.setOnAction(actionEvent -> backEvent.handle(null));
        mmAboutProgram.setOnAction(actionEvent -> alert(ABOUT_PROGRAM, folderElements.getScene().getWindow()));
        mmAboutAuthor.setOnAction(actionEvent -> alert(ABOUT_AUTHOR, folderElements.getScene().getWindow()));

        mmOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        mmSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        mmCopy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        mmMove.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        mmPaste.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        mmDelete.setAccelerator(KeyCombination.keyCombination("Delete"));

        mmNewFolder.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        mmUpload.setAccelerator(KeyCombination.keyCombination("Ctrl+U"));
        mmRefresh.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        mmBack.setAccelerator(KeyCombination.keyCombination("Esc"));

        mmShowStructure.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        mmShowFilePreview.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+F"));
        mmAboutProgram.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        mmAboutAuthor.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));

        mmShowStructure.selectedProperty().addListener((observableValue, oldValue, newValue) ->
                splitPane.setDividerPosition(0, newValue ? structureShowingOnThisDividerPosition : 0.0));
        structurePane.widthProperty().addListener((observableValue, oldValue, newValue) ->
                mmShowStructure.setSelected(newValue.doubleValue() > widthOfOpenedPane)
        );

        mmShowFilePreview.selectedProperty().addListener((observableValue, oldValue, newValue) ->
                splitPane.setDividerPosition(1, newValue ? previewShowingOnThisDividerPosition : 1.0)
        );
        filePreviewPane.widthProperty().addListener((observableValue, oldValue, newValue) ->
                mmShowFilePreview.setSelected(newValue.doubleValue() > widthOfOpenedPane)
        );

        mmShowStructure.setSelected(true);
        mmShowFilePreview.setSelected(true);

// ----------------- END MENU INITIALIZING ------------------------

        try {
            storage.open("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            storage.refreshAllPaths();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setCloseBtn(closeBtn);
        setWindowGrabber(titleHBox);
    }

    private void pasteRequestHandled(String dest) {
        switch (currentCopyMoveState) {
            case COPY:
                storage.copy(src.get(), dest);
                break;
            case MOVE:
                storage.move(src.get(), dest);
                currentCopyMoveState = COPY_MOVE.NOTHING;
                break;
        }
    }
}
