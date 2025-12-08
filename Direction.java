public enum Direction {
    R(0, 1), RD(1, 1), D(1, 0), LD(1, -1), L(0, -1), LU(-1, -1), U(-1, 0), RU(-1, 1), Ent(0, 0); // tunnel
                                                                                                 // entry

    public final int dr;
    public final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    // Clockwise order array (without tunnel)
    public static final Direction[] CLOCKWISE = {R, RD, D, LD, L, LU, U, RU, Ent,};

    // Counterclockwise order array (without tunnel)
    public static final Direction[] COUNTERCLOCKWISE = {R, RU, U, LU, L, LD, D, RD, Ent,};
}
