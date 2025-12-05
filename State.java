public class State {
    public int row;
    public int col;

    private double g = 0; // cost so far
    private double h; // heuristic

    public State parent; // previous state
    public String move; // direction name

    public boolean SupplyStation = false;
    public boolean onGoal = false;

    // REQUIRED for the "new-first" / "old-first" tie-breaking
    private int creationTime;

    public State(int row, int col, double g, double h, State parent, String move, Board board) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.parent = parent;
        this.move = move;
        this.SupplyStation = parent != null && parent.SupplyStation;

        // State gets null board to indicate goal state(prevent infinite recursice calls)
        if (board != null && board.isSupplyStation(row, col)) {
            this.SupplyStation = true;
            // System.out.println("Supply station acquired at: " + row + "," + col);
        }
        if (board == null || (board.getGoal().row == row && board.getGoal().col == col)) {
            this.onGoal = true;
        }
    }

    // Getters and Setters
    public double getG() {
        return g;
    }

    // public void setG(double g) {
    // this.g = g;
    // }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getF() {
        return g + h;
    }

    public int getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(int creationTime) {
        this.creationTime = creationTime;
    }

    public String getId() {
        return row + "," + col + "," + SupplyStation;
    }

    @Override
    public String toString() {
        // Helpful for the "with open" printout requirements
        return "(" + row + "," + col + ")";
    }

}
