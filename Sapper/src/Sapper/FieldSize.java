package Sapper;

public enum FieldSize  {
    SMALL(12, 10),
    MEDIUM(24, 15),
    LARGE(48, 20);

    int _bombCount;
    int _fieldWidth;

    FieldSize(int bombCount, int fieldWidth) {
        this._bombCount = bombCount;
        this._fieldWidth = fieldWidth;
    }
}
