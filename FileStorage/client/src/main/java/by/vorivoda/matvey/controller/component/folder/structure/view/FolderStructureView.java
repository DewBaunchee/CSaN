package by.vorivoda.matvey.controller.component.folder.structure.view;

import by.vorivoda.matvey.controller.component.folder.ContainingElements;
import by.vorivoda.matvey.controller.component.folder.factory.ElementFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FolderStructureView extends ContainingElements {

    private final ElementFactory elementFactory;
    private final TreeView<HBox> elementsView;
    private final ListProperty<String> currentElements;

    public FolderStructureView(TreeView<HBox> container) {
        this.elementsView = container;
        currentElements = new SimpleListProperty<>();
        currentElements.addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null && oldValue.size() == newValue.size()) {
                for(int i =0; i < oldValue.size(); i++) {
                    if(!oldValue.get(i).equals(newValue.get(i))) {
                        draw(newValue);
                        break;
                    }
                }
            } else {
                draw(newValue);
            }
        });
        elementFactory = new ElementFactory(20);
    }

    public ObservableList<String> getCurrentElements() {
        return currentElements.get();
    }

    public ListProperty<String> currentElementsProperty() {
        return currentElements;
    }

    public void draw(List<String> elements) {
        elementsView.setRoot(recFill(elements, "/"));
    }

    private String reconstructPath(TreeItem<AnchorPane> item) {
        StringBuilder sb = new StringBuilder();
        TreeItem<AnchorPane> current = item;
        while (current.getParent() != null) {
            sb.insert(0, "/" + getTreeViewText(current));
            current = current.getParent();
        }
        return sb.toString();
    }

    private boolean isDirectChild(String parent, String child) {
        child = child.substring(0, child.length() - 1);
        if (child.startsWith(parent)) {
            return child.lastIndexOf("/") == parent.length() - 1;
        }
        return false;
    }

    private String getTreeViewText(TreeItem<AnchorPane> item) {
        return ((Label) item.getValue().getChildren().get(1)).getText();
    }

    private TreeItem<HBox> recFill(List<String> paths, String current) {
        TreeItem<HBox> currentItem = new TreeItem<>((HBox) elementFactory.newElement(current));
        currentItem.setGraphic(null);

        if (current.endsWith("/")) {
            for (String path : paths) {
                if (isDirectChild(current, path)) {
                    currentItem.getChildren().add(recFill(paths, path));
                }
            }
        }

        return currentItem;
    }
}
