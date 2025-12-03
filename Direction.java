public enum Direction {
    UP(-1, 0),
    UP_RIGHT(-1, 1),
    RIGHT(0, 1),
    DOWN_RIGHT(1, 1),
    DOWN(1, 0),
    DOWN_LEFT(1, -1),
    LEFT(0, -1),
    UP_LEFT(-1, -1),
    ENT(0, 0); // tunnel entry

    public final int dr;
    public final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    // Clockwise order array (without tunnel)
    public static final Direction[] CLOCKWISE = {
            UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT
    };

    // Counterclockwise order array (without tunnel)
    public static final Direction[] COUNTERCLOCKWISE = {
            UP, UP_LEFT, LEFT, DOWN_LEFT, DOWN, DOWN_RIGHT, RIGHT, UP_RIGHT
    };
}
