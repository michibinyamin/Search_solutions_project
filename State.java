public class State {
    public int row;
    public int col;

    public int g; // cost so far
    public int h; // heuristic
    public State parent; // previous state
    public String move; // direction name
    public boolean SupplyStation = false;
    public boolean onGoal = false;

    public State(int row, int col, int g, int h, State parent, String move, boolean SupplyStation,
            Board board) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.parent = parent;
        this.move = move;
        this.SupplyStation = SupplyStation;
        // State gets null board to indicate goal state(prevent infinite recursice calls)
        if (board != null && board.isSupplyStation(row, col)) {
            this.SupplyStation = true;
        }
        if (board == null || (board.getGoal().row == row && board.getGoal().col == col)) {
            this.onGoal = true;
        }
    }

    public int f() {
        return g + h;
    }

    public String getId() {
        return row + "," + col;
    }

    // public void setSupplyStation() {
    // this.SupplyStation = true;
    // }

}
