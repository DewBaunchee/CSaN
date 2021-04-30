package sapper.view.factories;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MarkedCellFactory implements CellFactory {

    private final int _cellSize;

    public MarkedCellFactory(int cellSize) {
        _cellSize = cellSize;
    }

    @Override
    public Node createCell(int bombAround) {
        AnchorPane minedCell = new AnchorPane();
        minedCell.setStyle("-fx-background-color: #0072BE; -fx-border-color: #FFF;");
        minedCell.setPrefWidth(_cellSize);
        minedCell.setPrefHeight(_cellSize);

        minedCell.setOnMouseEntered(event -> {
            minedCell.setStyle("-fx-background-color: #004793; -fx-border-color: #FFF;");
        });
        minedCell.setOnMouseExited(event -> {
            minedCell.setStyle("-fx-background-color: #0072BE; -fx-border-color: #FFF;");
        });

        ImageView image = new ImageView();
        image.setImage(new Image("sapper/view/images/flag.png"));
        image.setFitWidth(_cellSize - 2);
        image.setFitHeight(_cellSize - 2);
        image.setMouseTransparent(true);

        minedCell.getChildren().add(image);
        AnchorPane.setBottomAnchor(image, 0.0);
        AnchorPane.setRightAnchor(image, 0.0);
        AnchorPane.setLeftAnchor(image, 0.0);
        AnchorPane.setTopAnchor(image, 0.0);

        return minedCell;
    }
}
