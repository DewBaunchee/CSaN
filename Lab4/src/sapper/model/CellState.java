package sapper.model;

public enum CellState {
    HIDDEN,
    OPENED,
    MARKED;

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
