public enum Direction {
    R(0, 1),
    DR(1, 1),
    D(1, 0),
    DL(1, -1),
    L(0, -1),
    UL(-1, -1),
    U(-1, 0),
    UR(-1, 1),
    Ent(0, 0); // tunnel entry

    public final int dr;
    public final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    // Clockwise order array (without tunnel)
    public static final Direction[] CLOCKWISE = {
            R, DR, D, DL, L, UL, U, UR, Ent,
    };

    // Counterclockwise order array (without tunnel)
    public static final Direction[] COUNTERCLOCKWISE = {
            R, UR, U, UL, L, DL, D, DR, Ent,
    };
}
