package sapper.view.factories;

import javafx.scene.Node;

import java.io.Serializable;

public interface CellFactory extends Serializable {
    Node createCell(int bombAround);
}
