package sapper.view.factories;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MinedCellFactory implements CellFactory {

    private final int _cellSize;

    public MinedCellFactory(int cellSize) {
        _cellSize = cellSize;
    }

    @Override
    public Node createCell(int bombAround) {
        AnchorPane markedCell = new AnchorPane();
        markedCell.setStyle("-fx-background-color: #0072BE; -fx-border-color: #FFF;");
        markedCell.setPrefWidth(_cellSize);
        markedCell.setPrefHeight(_cellSize);

        ImageView image = new ImageView();
        image.setImage(new Image("sapper/view/images/mine.png"));
        image.setFitWidth(_cellSize - 2);
        image.setFitHeight(_cellSize - 2);

        markedCell.getChildren().add(image);
        AnchorPane.setBottomAnchor(image, 0.0);
        AnchorPane.setRightAnchor(image, 0.0);
        AnchorPane.setLeftAnchor(image, 0.0);
        AnchorPane.setTopAnchor(image, 0.0);

        return markedCell;
    }
}
