package sapper.model;

public enum FieldSize  { // Готовые поля
    SMALL(12, 10),
    MEDIUM(24, 15),
    LARGE(48, 20);

    public final int bombCount;
    public final int fieldWidth;

    FieldSize(int bombCount, int fieldWidth) {
        this.bombCount = bombCount;
        this.fieldWidth = fieldWidth;
    }
}
