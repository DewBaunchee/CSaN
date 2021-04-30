package Sapper;

public enum CellState {
    HIDDEN,
    OPENED,
    MARKED,
    MINE;

    public static final boolean[][] canChangeState = new boolean[][]{
            /*       HIDDEN OPENED MARKED MINE
            * HIDDEN
            * OPENED
            * MARKED
            * MINE
            * */
            {false, true, true, true},
            {false, false, false, false},
            {true, false, false, false},
            {false, false, false, false}
    };
}
