import java.nio.file.*;
import java.io.IOException;

public class Ex1 {
    public static void main(String[] args) throws IOException {
        Board board = new Board("input.txt");
        String[] lines = Files.readAllLines(Path.of("input.txt")).toArray(new String[0]);

        String algorithm = lines[0];
        String[] line2_split = lines[1].split(" ");
        String order = line2_split[0];
        String tieBreaking = (line2_split.length > 1) ? line2_split[1] : null;
        boolean withTime = lines[2].equals("with time");
        boolean withOpen = lines[3].equals("with open");

        // parse grid size
        String[] sizeSplit = lines[4].split("x");
        int N = Integer.parseInt(sizeSplit[0]); // rows
        int M = Integer.parseInt(sizeSplit[1]); // columns

        // parse grid
        char[][] grid = new char[N][M];
        for (int i = 0; i < N; i++) {
            grid[i] = lines[5 + i].toCharArray();
        }

        Algorithm solver = null;
        switch (algorithm) {
            case "BFS":
                solver = new BFS(board, order, withOpen);
                break;
            case "A*":
                solver = new AStar(board, order, tieBreaking, withOpen);
                break;
            case "DFID":
                solver = new DFID(board, order, withOpen);
                break;
            case "IDA*":
                solver = new IDAStar(board, order, withOpen);
                break;
            case "DFBnB":
                solver = new DFBnB(board, order, tieBreaking, withOpen);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        long startTime = System.nanoTime();
        Result result = solver.solve();
        double timeSec = (System.nanoTime() - startTime) / 1e9;

        writeToOutput(result, withTime ? timeSec : -1, withOpen);
    }
}
