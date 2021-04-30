package sapper.view.factories;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;

public class OpenedCellFactory implements CellFactory {
    public static final HashMap<Integer, Color> colors = new HashMap<>();
    static {
        colors.put(0, Color.rgb(190, 190, 190));
        colors.put(1, Color.rgb(67, 190, 63));
        colors.put(2, Color.rgb(150, 33, 0));
        colors.put(3, Color.rgb(0, 28, 255));
        colors.put(4, Color.rgb(255, 231, 0));
        colors.put(5, Color.rgb(255, 148, 0));
        colors.put(6, Color.rgb(136, 0, 255));
        colors.put(7, Color.rgb(255, 255, 255));
        colors.put(8, Color.rgb(0, 255, 225));
    }

    private final int _cellSize;

    public OpenedCellFactory(int cellSize) {
        _cellSize = cellSize;
    }

    @Override
    public Node createCell(int bombAround) {
        Label digit = new Label();
        digit.setText(bombAround > 0 ? bombAround + "" : "");
        digit.setStyle("-fx-font-weight: bold; -fx-border-color: #FFF");
        digit.setAlignment(Pos.CENTER);
        digit.setTextFill(colors.get(bombAround));
        digit.setMinWidth(_cellSize);
        digit.setMinHeight(_cellSize);
        digit.setFont(Font.font("Courier New", _cellSize * 0.80));

        return digit;
    }
}
