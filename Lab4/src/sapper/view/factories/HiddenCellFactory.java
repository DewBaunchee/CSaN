package sapper.view.factories;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;


public class HiddenCellFactory implements CellFactory {
    private final int _cellSize;

    public HiddenCellFactory(int cellSize) {
        _cellSize = cellSize;
    }

    @Override
    public Node createCell(int bombAround) {
        AnchorPane hiddenCell = new AnchorPane();
        hiddenCell.setStyle("-fx-background-color: #0072BE; -fx-border-color: #FFF;");
        hiddenCell.setMinWidth(_cellSize);
        hiddenCell.setMinHeight(_cellSize);

        hiddenCell.setOnMouseEntered(event -> {
            hiddenCell.setStyle("-fx-background-color: #004793; -fx-border-color: #FFF;");
        });
        hiddenCell.setOnMouseExited(event -> {
            hiddenCell.setStyle("-fx-background-color: #0072BE; -fx-border-color: #FFF;");
        });

        return hiddenCell;
    }
}
