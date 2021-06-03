package by.vorivoda.matvey.model;

import by.vorivoda.matvey.model.util.FileInfo;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class StorageStateBindings {

    public StorageStateBindings() {
        currentFolder = new SimpleStringProperty();
        currentFolderElements = new SimpleListProperty<>(FXCollections.observableArrayList());
        currentFile = new SimpleStringProperty();
        allPaths = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    // Current folder
    private final StringProperty currentFolder;

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder.set(currentFolder);
    }

    public String getCurrentFolder() {
        return currentFolder.get();
    }

    public StringProperty currentFolderProperty() {
        return currentFolder;
    }

    // Folder elements
    private final ListProperty<String> currentFolderElements;

    public ObservableList<String> getCurrentFolderElements() {
        return currentFolderElements.get();
    }

    public ListProperty<String> currentFolderElementsProperty() {
        return currentFolderElements;
    }

    public void setCurrentFolderElements(List<String> currentFolderElements) {
        this.currentFolderElements.set(FXCollections.observableList(currentFolderElements));
    }

    // Current file
    private final StringProperty currentFile;
    private FileInfo currentFileInfo;

    public String getCurrentFile() {
        return currentFile.get();
    }

    public StringProperty currentFileProperty() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile, FileInfo fileInfo) {
        this.currentFileInfo = fileInfo;
        this.currentFile.set(currentFile);
    }

    public FileInfo getCurrentFileInfo() {
        return currentFileInfo;
    }

    // All paths
    private final ListProperty<String> allPaths;

    public ObservableList<String> getAllPaths() {
        return allPaths.get();
    }

    public ListProperty<String> allPathsProperty() {
        return allPaths;
    }

    public void setAllPaths(List<String> currentFolderElements) {
        this.allPaths.set(FXCollections.observableList(currentFolderElements));
    }
}
