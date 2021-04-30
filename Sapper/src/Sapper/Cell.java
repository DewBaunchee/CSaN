package Sapper;

public class Cell {
    private CellState _state;

    private int _bombAround;

    public Cell() {
        _state = CellState.HIDDEN;
        _bombAround = 0;
    }

    public void setBombAround(int _bombAround) {
        this._bombAround = _bombAround;
    }

    public int getBombAround() {
        return _bombAround;
    }

    public void setState(CellState newState) {
        _state = newState;
    }

    public CellState getState() {
        return _state;
    }

    @Override
    public String toString() {
        switch (_state) {
            case HIDDEN: return "X";
            case MARKED: return "P";
            case MINE: return "*";
        }
        return _bombAround > 0 ? _bombAround + "" : " ";
    }
}
