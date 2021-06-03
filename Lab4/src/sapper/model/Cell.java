package sapper.model;

import java.io.Serializable;

public class Cell implements Serializable { // Клетка поля
    private CellState _state; // Состояние
    private int _bombAround; // Кол-во бомб вокруг
    private boolean _isMined; // Заминирована?

    public Cell() {
        _state = CellState.HIDDEN;
        _bombAround = 0;
        _isMined = false;
    }

    public void makeMined() {
        _isMined = true;
    }

    public boolean isMined() {
        return _isMined;
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
        if (_isMined) return "*";
        switch (_state) {
            case HIDDEN:
                return "X";
            case MARKED:
                return "P";
        }
        return _bombAround > 0 ? _bombAround + "" : " ";
    }
}
