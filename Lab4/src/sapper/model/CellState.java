package sapper.model;

public enum CellState { // Состояние клетки
    HIDDEN,
    OPENED,
    MARKED;

    // Матрица для проверки возможности изменения состояния из (строка) в (столбец)
    public static final boolean[][] canChangeState = new boolean[][]{
            /*       HIDDEN OPENED MARKED MINE
            * HIDDEN
            * OPENED
            * MARKED
            * MINE
            * */
            {false, true, true},
            {false, false, false},
            {true, false, false},
    };
}
