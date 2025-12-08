import java.util.*; // Required for List, ArrayList, Map, HashMap

public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;

    public Board(int rows, int cols, char[][] grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
    }

    public boolean isLegal(int r, int c, boolean hasSupplyStation, State parent) {
        // inside board
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            return false;

        // smooth floor check
        if (grid[r][c] == '~' && !hasSupplyStation)
            return false;

        // not going back to parent
        if (parent != null && parent.row == r && parent.col == c
                && parent.SupplyStation == hasSupplyStation)
            return false;

        // not a wall
        return grid[r][c] != '#'; // or whatever blocks movement

    }

    public State getStart() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 'S')
                    return new State(r, c, 0, 0, null, null, this);
        return null;
    }

    public State getGoal() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 'G')
                    return new State(r, c, 0, 0, null, null, null);
        return null;
    }

    public int[] getTunnelExit(int row, int col) {
        for (int r = 0; r < this.getRows(); r++) {
            for (int c = 0; c < this.getCols(); c++) {
                if (this.getCell(row, col) == this.getCell(r, c) && (r != row || c != col)) {
                    // found matching tunnel exit
                    return new int[] {r, c};
                }
            }
        }
        return null; // no exit found
    }

    public int getPositionValue(int r, int c, Direction dir) { // does not include entering tunnel
                                                               // cost
        char cell = getCell(r, c);
        switch (cell) {
            case '-':
                return 1;
            case '^':
                return (dir == Direction.RU || dir == Direction.LU || dir == Direction.LD
                        || dir == Direction.RD) ? 10 : 5; // only if moving diagonally
            case '~':
                return 3; // only if allowed
            case '*':
                return 1;
            case 'S':
                return 1;
            case 'G':
                return 5;
            default: // defult means that it is a tunnel
                if (this.isTunnel(r, c))
                    return (dir == Direction.Ent) ? 2 : 1; // entering tunnel has cost of 2, else
                                                           // normal cost
                return Integer.MAX_VALUE; // should not happen
        }
    }

    public boolean isTunnel(int r, int c) {
        char cell = getCell(r, c);
        return cell >= '0' && cell <= '9';
    }

    public boolean isSmoothFloor(int r, int c) {
        return getCell(r, c) == '~';
    }

    public boolean isSupplyStation(int r, int c) {
        return getCell(r, c) == '*';
    }

    public char getCell(int r, int c) {
        return grid[r][c];
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

    public List<int[]> getAllTunnels() {
        List<int[]> tunnels = new ArrayList<>();
        Map<Character, int[]> tempMap = new HashMap<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (isTunnel(r, c)) {
                    char tunnelId = grid[r][c];

                    if (tempMap.containsKey(tunnelId)) {
                        // We found the second end of the tunnel
                        int[] exit1 = tempMap.get(tunnelId);
                        // Add the pair: {row1, col1, row2, col2}
                        tunnels.add(new int[] {exit1[0], exit1[1], r, c});
                    } else {
                        // Found the first end, store it and wait for the second
                        tempMap.put(tunnelId, new int[] {r, c});
                    }
                }
            }
        }
        return tunnels;
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
