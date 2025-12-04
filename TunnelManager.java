public class TunnelManager {
    Board board;

    public TunnelManager(Board board) {
        this.board = board;
    }

    public int[] getTunnelExit(int row, int col) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCell(row, col) == board.getCell(r, c) && (r != row || c != col)) { // found matching tunnel
                                                                                                // exit(but not itself)
                    return new int[] { r, c };
                }
            }
        }
        return null; // no exit found
    }
}
