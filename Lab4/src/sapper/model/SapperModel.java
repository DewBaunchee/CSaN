package sapper.model;

import java.io.Serializable;
import java.util.Random;

public class SapperModel implements Serializable {
    private static final double MAX_BOMB_PROPORTION = 0.8; //максимальное количество бомб относительно количества клеток
    private Cell[][] _field; // Поле
    private int _bombCount; // Количество бомб
    private int _markCount; // Количество отмеченных клеток
    private int _hiddenCells; // Количество ещё не открытых клеток
    private int _step; // Текущий шаг

    public SapperModel(int bombCount, int fieldWidth) {
        if (bombCount < 1) {
            throw new RuntimeException("Bomb count is lesser than 1");
        }
        if (fieldWidth * fieldWidth * MAX_BOMB_PROPORTION < bombCount) {
            throw new RuntimeException("Too much bombs");
        }

        initField(bombCount, fieldWidth);
    }

    public SapperModel(FieldSize fieldSize) {
        initField(fieldSize.bombCount, fieldSize.fieldWidth);
    }

    private void initField(int bombCount, int fieldWidth) { // Инициализация поля
        _field = new Cell[fieldWidth][fieldWidth];
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldWidth; j++) _field[i][j] = new Cell();
        }
        _step = 0;
        _hiddenCells = fieldWidth * fieldWidth;
        _bombCount = bombCount;
        _markCount = 0;
    }

    private int countBombAround(int row, int col) { // Посчитать количество бомб вокруг клетки
        int count = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (isCorrectIndexes(row + i, col + j))
                    count += _field[row + i][col + j].isMined() ? 1 : 0;
            }
        }
        return count;
    }

    private boolean isCorrectIndexes(int row, int col) { // Проверка индекса
        return row > -1 && row < _field.length
                && col > -1 && col < _field.length;
    }

    public void setMines(int row, int col) { // Расстановка мин
        Random random = new Random();
        for (int i = 0; i < _bombCount; i++) {
            int bombRow, bombCol;
            // Генерация в пустой клетке не ближе, чем за одну клетку до указанной
            do {
                bombRow = random.nextInt(_field.length);
                bombCol = random.nextInt(_field.length);
            } while (Math.abs(bombRow - row) < 2 && Math.abs(bombCol - col) < 2
                    || _field[bombRow][bombCol].isMined());
            _field[bombRow][bombCol].makeMined();
        }

        // Высчитывание для каждой клетки бомб вокруг
        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field.length; j++) {
                if (_field[i][j].isMined()) continue;
                _field[i][j].setBombAround(countBombAround(i, j));
            }
        }
    }

    public boolean openCell(int row, int col) { // Открыть клетку
        if(_step == 0) setMines(row, col);
        _step++;
        if (_field[row][col].isMined()) return false;

        recOpen(row, col); // Рекурсивно обходим остальные клетки пока кол-во бомб вокруг равно 0
        return true;
    }

    private void recOpen(int row, int col) {
        if (!isCorrectIndexes(row, col)) return; // Проверка
        if (_field[row][col].getState() == CellState.OPENED) return; // Проверка

        _hiddenCells--; // Декремент количества закрытых клеток
        _field[row][col].setState(CellState.OPENED); // Открытие
        if (_field[row][col].getBombAround() == 0) { // Если количество бомб вокруг равно 0, то открыть все клетки вокруг
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    recOpen(row + i, col + j);
                }
            }
        }
    }

    public boolean toggleMark(int row, int col) { // Установка/удаление отметки
        if (_field[row][col].getState() == CellState.MARKED) {
            _field[row][col].setState(CellState.HIDDEN);
            _markCount--;
            return true;
        }

        boolean canChange = canChange(row, col, CellState.MARKED);
        if (canChange && _markCount < _bombCount) {
            _field[row][col].setState(CellState.MARKED);
            _markCount++;
        }
        return canChange;
    }

    public CellState getCellState(int row, int col) {
        return _field[row][col].getState();
    }

    public void setCellState(int row, int col, CellState state) {
        boolean canChange = canChange(row, col, state);
        if (canChange) {
            _field[row][col].setState(state);
        }
    }

    public Cell getCell(int row, int col) {
        return _field[row][col];
    }

    public Cell[][] getField() {
        return _field;
    }

    public int getBombCount() {
        return _bombCount;
    }

    public int getHiddenCells() {
        return _hiddenCells;
    }

    public int getStep() {
        return _step;
    }

    public boolean canChange(int row, int col, CellState newState) {
        return CellState.canChangeState[_field[row][col].getState().ordinal()][newState.ordinal()];
    }

    @Override
    public String toString() {
        StringBuilder field = new StringBuilder();

        for (Cell[] cells : _field) {
            for (int j = 0; j < _field.length; j++) {
                field.append("|").append(cells[j].toString());
            }
            field.append("|\n");
        }

        return field.toString();
    }
}
