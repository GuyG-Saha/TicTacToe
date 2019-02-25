public class Board {
    private static final int SIZE = 3;
    private Cell[][] mCells;

    public Board() {
        mCells = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                mCells[i][j] = new Cell();
            }
        }
    }
}
