public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;

    public Board(int rows, int cols, char[][] grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
    }

    public boolean isLegal(int r, int c) {
        // inside board
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            return false;

        // not a wall
        return grid[r][c] != 'W'; // or whatever blocks movement
    }

    public State getStart() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 'S')
                    return new State(r, c, 0, 0, null, null);
        return null;
    }

    public State getGoal() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 'G')
                    return new State(r, c, 0, 0, null, null);
        return null;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char[][] getGrid() {
        return grid;
    }

    public void displayBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}
