package by.vorivoda.matvey.controller.component.folder.view;

import by.vorivoda.matvey.controller.component.folder.ContainingElements;
import by.vorivoda.matvey.controller.component.folder.factory.ElementFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.util.List;

public class FolderView  extends ContainingElements {

    private final ElementFactory elementFactory;
    private final VBox elementsView;
    private final ListProperty<String> currentElements;

    public FolderView(VBox container) {
        elementsView = container;
        currentElements = new SimpleListProperty<>();
        currentElements.addListener((observableValue, oldValue, newValue) -> drawList(newValue));
        elementFactory = new ElementFactory();
        elementFactory.setOnMouseClicked(this::onMouseClicked);
        elementFactory.setOnCopyRequest(this::onCopyRequest);
        elementFactory.setOnMoveRequest(this::onMoveRequest);
        elementFactory.setOnPasteRequest(this::onPasteRequest);
        elementFactory.setOnDeleteRequest(this::onDeleteRequest);
        elementFactory.setOnOpenRequest(this::onOpenRequest);
        elementFactory.setOnSaveRequest(this::onSaveRequest);
        elementFactory.setOnSizeRequest(this::onSizeRequest);
    }

    public ObservableList<String> getCurrentElements() {
        return currentElements.get();
    }

    public ListProperty<String> currentElementsProperty() {
        return currentElements;
    }

    public void drawList(List<String> elements) {
        elementsView.getChildren().clear();
        for(String path : elements) {
            elementsView.getChildren().add(elementFactory.newElement(path));
        }
    }
}
