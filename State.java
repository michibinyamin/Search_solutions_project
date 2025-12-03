public class State {
    public int row;
    public int col;

    public int g; // cost so far
    public int h; // heuristic
    public State parent; // previous state
    public String move; // direction name

    public State(int row, int col, int g, int h, State parent, String move) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.parent = parent;
        this.move = move;
    }

    public int f() {
        return g + h;
    }

    public String getId() {
        return row + "," + col;
    }

}
