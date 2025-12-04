import java.nio.file.*;
import java.io.IOException;
import java.io.PrintWriter;

public class Ex1 {
    public static void main(String[] args) throws IOException {
        // Board board = new Board("input.txt");
        String[] lines = Files.readAllLines(Path.of("input.txt")).toArray(new String[0]);

        String algorithm = lines[0]; // first line: algorithm
        String[] line2_split = lines[1].split(" ");
        String order = line2_split[0]; // second line: order
        String tieBreaking = (line2_split.length > 1) ? line2_split[1] : null; // second line:
                                                                               // tie-breaking
        boolean withTime = lines[2].equals("with time"); // third line: with time
        boolean withOpen = lines[3].equals("with open"); // fourth line: with open

        // parse grid size
        String[] sizeSplit = lines[4].split("x");
        int N = Integer.parseInt(sizeSplit[0]); // rows
        int M = Integer.parseInt(sizeSplit[1]); // columns

        // parse grid
        char[][] grid = new char[N][M];
        for (int i = 0; i < N; i++) {
            grid[i] = lines[5 + i].toCharArray();
        }

        Board board = new Board(N, M, grid);
        board.displayBoard();

        Algorithm solver = null;
        switch (algorithm) {
            case "BFS":
                solver = new BFS(order, withOpen);
                break;
            // case "A*":
            // solver = new AStar(board, order, tieBreaking, withOpen);
            // break;
            case "DFID":
                solver = new DFID(order, withOpen);
                break;
            // case "IDA*":
            // solver = new IDAStar(board, order, tieBreaking, withOpen);
            // break;
            // case "DFBnB":
            // solver = new DFBnB(board, order, tieBreaking, withOpen);
            // break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        long startTime = System.nanoTime();
        String result = solver.solve(board);
        double timeSec = (System.nanoTime() - startTime) / 1e9;

        writeToOutput(result, withTime ? timeSec : -1, withOpen);

    }

    public static void writeToOutput(String path, double timeSec, boolean withOpen) {
        try (PrintWriter out = new PrintWriter("output.txt")) {
            out.println(path); // the moves/path
            if (timeSec >= 0) // optional runtime
                out.println("Time: " + timeSec + "s");
            // if (withOpen)
            // out.println("Open list info..."); // if you want
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
