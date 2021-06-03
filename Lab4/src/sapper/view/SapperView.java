package sapper.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import sapper.model.Cell;
import sapper.model.CellState;
import sapper.model.SapperModel;
import sapper.view.factories.*;

import java.io.Serializable;

public class SapperView implements Serializable {
    private SapperModel _sapper; // Движок
    // Фабрики клеток
    private final CellFactory _hiddenCellFactory;
    private final CellFactory _openedCellFactory;
    private final CellFactory _minedCellFactory;
    private final CellFactory _markedCellFactory;
    // Компонент в который отрисовывается поле
    private GridPane _sapperGrid;

    // События поля
    private SapperViewAction onWinAction;
    private SapperViewAction onLoseAction;
    private SapperViewAction onFirstStepAction;
    private SapperViewAction onOpenAction;
    private SapperViewAction onMarkToggledAction;

    public SapperView(SapperModel sapper) {
        _sapper = sapper;
        _hiddenCellFactory = new HiddenCellFactory(30);
        _openedCellFactory = new OpenedCellFactory(30);
        _minedCellFactory = new MinedCellFactory(30);
        _markedCellFactory = new MarkedCellFactory(30);

        onWinAction = (row, col) -> System.out.println("You win!");
        onLoseAction = (row, col) -> System.out.println("You lose!");
        onFirstStepAction = (row, col) -> System.out.println("You made the first step.");
        onOpenAction = (row, col) -> System.out.println("You opened cell.");
        onMarkToggledAction = (row, col) -> System.out.println("You toggled mark.");

        initGrid();
        reloadGrid();
    }

    private void initGrid() { // Инициализация
        _sapperGrid = new GridPane();
        _sapperGrid.setAlignment(Pos.CENTER);

        // Обработчик события нажатия на сетку
        _sapperGrid.setOnMouseClicked(event -> {
            Node node = event.getPickResult().getIntersectedNode();

            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            if (col == null || row == null) return;

            CellState currentState = _sapper.getCellState(row, col);
            if (event.getButton() == MouseButton.PRIMARY) {
                if(currentState != CellState.MARKED) {
                    if (_sapper.getCell(row, col).isMined()) { // Если нажато на заминированую, то проигрыш
                        lose(row, col);
                    } else if (currentState == CellState.HIDDEN) {
                        openCell(row, col);
                        if (_sapper.getHiddenCells() == _sapper.getBombCount()) { // Если кол-во спрятанных равно кол-ву бомб, то победа
                            win();
                        }
                    }
                }
            } else {
                switch (currentState) {
                    case MARKED:
                    case HIDDEN:
                        toggleMark(row, col);
                }
            }
        });
    }

    public void removeGridOnMouseClick() {
        _sapperGrid.setOnMouseClicked(null);
    } // Удаление обработчика нажатия на сетку

    private void reloadGrid() { // Переотрисовка сетки
        _sapperGrid.getChildren().clear(); // Очистка

        Cell[][] cells = _sapper.getField();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                addCell(cells[i][j], i, j); // Добавление клетки в сетку
            }
        }
    }

    private void addCell(Cell cell, int row, int col) {
        switch (cell.getState()) {
            case OPENED:
                _sapperGrid.add(_openedCellFactory.createCell(cell.getBombAround()), col, row);
                break;
            case MARKED:
                _sapperGrid.add(_markedCellFactory.createCell(cell.getBombAround()), col, row);
                break;
            case HIDDEN:
                _sapperGrid.add(_hiddenCellFactory.createCell(cell.getBombAround()), col, row);
                break;
        }
    }

    public void openCell(int row, int col) { // Открытие клетки
        _sapper.openCell(row, col);
        if(_sapper.getStep() == 1) onFirstStepAction.run(row, col);

        reloadGrid();
        onOpenAction.run(row, col);
    }

    public void toggleMark(int row, int col) { // Установка/снятие отметки
        _sapper.toggleMark(row, col);

        reloadGrid();
        onMarkToggledAction.run(row, col);
    }

    public void win() { // Победа
        reloadGrid();
        _sapperGrid.setOnMouseClicked(null);
        onWinAction.run(0, 0);
    }

    public void lose(int row, int col) { // Проигрыш
        _sapperGrid.setOnMouseClicked(null);
        Cell[][] cells = _sapper.getField();

        _sapperGrid.getChildren().clear();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].isMined()) {
                    _sapperGrid.add(_minedCellFactory.createCell(cells[i][j].getBombAround()), j, i);
                } else {
                    addCell(cells[i][j], i, j);
                }
            }
        }

        _sapperGrid.getChildren().get(row * cells[row].length + col).setStyle("-fx-background-color: red; -fx-border-color: #000;");
        onLoseAction.run(row, col);
    }

    public void setOnFirstStepAction(SapperViewAction onFirstStepAction) {
        this.onFirstStepAction = onFirstStepAction;
    }

    public void setOnWinAction(SapperViewAction winAction) {
        this.onWinAction = winAction;
    }

    public void setOnLoseAction(SapperViewAction loseAction) {
        this.onLoseAction = loseAction;
    }

    public void setOnOpenAction(SapperViewAction onOpenAction) {
        this.onOpenAction = onOpenAction;
    }

    public void setOnMarkToggledAction(SapperViewAction onMarkToggledAction) {
        this.onMarkToggledAction = onMarkToggledAction;
    }

    public GridPane getSapperGrid() {
        return _sapperGrid;
    }

    public void setSapper(SapperModel model) {
        _sapper = model;
        reloadGrid();
    }

    public SapperModel getSapper() {
        return _sapper;
    }
}
