package Sapper;

import java.util.Random;

public class SapperModel {
    private static final double MAX_BOMB_PROPORTION = 0.8;
    private Cell[][] _field;
    private int _bombCount;

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
        initField(fieldSize._bombCount, fieldSize._fieldWidth);
    }

    private void initField(int bombCount, int fieldWidth) {
        _field = new Cell[fieldWidth][fieldWidth];
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldWidth; j++) _field[i][j] = new Cell();
        }
        _bombCount = bombCount;
    }

    public void firstStep(int row, int col) {
        Random random = new Random();
        for (int i = 0; i < _bombCount; i++) {
            int bombRow, bombCol;
            do {
                bombRow = random.nextInt(_field.length);
                bombCol = random.nextInt(_field.length);
            } while (Math.abs(bombRow - row) < 2 && Math.abs(bombCol - col) < 2
                    || _field[bombRow][bombCol].getState() == CellState.MINE);
            _field[bombRow][bombCol].setState(CellState.MINE);
        }

        for (int i = 0; i < _field.length; i++) {
            for (int j = 0; j < _field.length; j++) {
                if (_field[i][j].getState() == CellState.MINE) continue;
                _field[i][j].setBombAround(countBombAround(i, j));
            }
        }
        openCell(row, col);
    }

    private int countBombAround(int row, int col) {
        int count = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (isCorrectIndexes(row + i, col + j))
                    count += _field[row + i][col + j].getState() == CellState.MINE ? 1 : 0;
            }
        }
        return count;
    }

    private boolean isCorrectIndexes(int row, int col) {
        return row > -1 && row < _field.length
                && col > -1 && col < _field.length;
    }

    public boolean openCell(int row, int col) {
        if (_field[row][col].getState() == CellState.MINE) return false;

        recOpen(row, col);
        return true;
    }

    private void recOpen(int row, int col) {
        if (!isCorrectIndexes(row, col)) return;
        if (_field[row][col].getState() == CellState.OPENED) return;

        _field[row][col].setState(CellState.OPENED);
        if (_field[row][col].getBombAround() == 0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    recOpen(row + i, col + j);
                }
            }
        }
    }

    public boolean toggleMark(int row, int col) {
        if (_field[row][col].getState() == CellState.MARKED) {
            _field[row][col].setState(CellState.HIDDEN);
            return true;
        }

        boolean canChange = canChange(row, col, CellState.MARKED);
        if (canChange) {
            _field[row][col].setState(CellState.MARKED);
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
