package by.vorivoda.matvey.controller.component.folder.factory;

import by.vorivoda.matvey.controller.component.folder.ContainingElements;
import by.vorivoda.matvey.model.util.FilesMethods;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ElementFactory extends ContainingElements {

    public static final String pathToFolderWithImages = "/view/image/folderImage";
    public static final Image defaultImage = new Image(pathToFolderWithImages + "/plain.png");
    public static final Map<String, Image> imageByExtension = new HashMap<>() {{
        put("", new Image(pathToFolderWithImages + "/plain.png"));
        put("/", new Image(pathToFolderWithImages + "/folder.png"));
        put("txt", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("docx", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("doc", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("pdf", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("rtf", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("xml", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("yml", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("json" +
                "", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("html", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("css", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("js", new Image(pathToFolderWithImages +"/text32x32.png"));
        put("png", new Image(pathToFolderWithImages +"/image32x32.png"));
        put("jpeg", new Image(pathToFolderWithImages +"/image32x32.png"));
        put("jpg", new Image(pathToFolderWithImages +"/image32x32.png"));
        put("gif", new Image(pathToFolderWithImages +"/image32x32.png"));
        put("svg", new Image(pathToFolderWithImages +"/image32x32.png"));
        put("mp3", new Image(pathToFolderWithImages +"/audio32x32.png"));
        put("mp4", new Image(pathToFolderWithImages +"/video32x32.png"));
    }};

    private static Image getFileIcon(File file) {
        Image image = imageByExtension.get(getFileExtension(file));
        return image == null ? defaultImage : image;
    }

    private static void deleteTempFile(File file) {
        boolean ignored = file.delete();
    }

    private static File createTempFile(String path) throws IOException {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
            return Files.createTempDirectory(path).toFile();
        }


        return Files.createTempFile(FilesMethods.getFileName(path),
                FilesMethods.getFileExtension(path)).toFile();
    }

    private static String getFileExtension(File file) {
        if (file.isDirectory()) return "/";

        int dotIndex = file.getName().lastIndexOf(".");
        return dotIndex == -1 ? "" : file.getName().substring(dotIndex + 1);
    }

    private final double rowHeight;

    public ElementFactory() {
        rowHeight = 30;
    }

    public ElementFactory(double rowHeight) {
        this.rowHeight = rowHeight;
    }

    public Node newElement(String path) {
        int lastSlash = path.lastIndexOf("/");
        lastSlash = lastSlash == path.length() - 1 ?
                path.lastIndexOf("/", lastSlash - 1)
                : lastSlash;
        path = path.substring(lastSlash + 1);

        HBox container = new HBox();
        container.setPadding(new Insets(4));
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("folder-element");

        try {
            File tempFile = createTempFile(path);
            ImageView fileIcon = new ImageView(getFileIcon(tempFile));
            deleteTempFile(tempFile);

            fileIcon.setFitHeight(rowHeight - 4);
            fileIcon.setFitWidth(rowHeight - 4);

            Label name = new Label();
            name.setPadding(new Insets(0, 0, 0, 10));
            name.getStyleClass().add("font-small");
            name.setText(path.replace("/", ""));

            container.getChildren().add(fileIcon);
            container.getChildren().add(name);
            setContextMenu(container, name.getText());
            container.setOnMouseClicked(mouseEvent -> onMouseClicked(mouseEvent, name.getText()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return container;
    }

    private void setContextMenu(Node owner, String name) {
        ContextMenu folderViewElementMenu = new ContextMenu();
        folderViewElementMenu.setStyle("-fx-background-color: #000");
        folderViewElementMenu.getStyleClass().add("font-small");
        folderViewElementMenu.setOnCloseRequest(windowEvent -> folderViewElementMenu.hide());

        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(actionEvent ->
                onOpenRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(openItem);

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(actionEvent ->
                onSaveRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(saveItem);

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(actionEvent ->
                onDeleteRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(deleteItem);

        MenuItem moveItem = new MenuItem("Move");
        moveItem.setOnAction(actionEvent ->
                onMoveRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(moveItem);

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(actionEvent ->
                onCopyRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(copyItem);

        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(actionEvent -> onPasteRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(pasteItem);

        MenuItem sizeItem = new MenuItem("Size");
        sizeItem.setOnAction(actionEvent -> onSizeRequest(actionEvent, name));
        folderViewElementMenu.getItems().add(sizeItem);

        owner.setOnContextMenuRequested(contextMenuEvent -> folderViewElementMenu.show(owner,
                contextMenuEvent.getSceneX() + owner.getScene().getWindow().getX(),
                contextMenuEvent.getSceneY() + owner.getScene().getWindow().getY()));
    }
}
