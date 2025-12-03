import java.util.*;

public class BFS implements Algorithm {

    private final boolean clockwise;
    private final boolean withOpen; // must implement!!!!

    public BFS(String order, boolean withOpen) {
        this.clockwise = order.equalsIgnoreCase("clockwise");
        this.withOpen = withOpen;
    }

    @Override
    public String solve(Board board) {
        State start = board.getStart();
        State goal = board.getGoal();

        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start.getId());

        Direction[] dirs = clockwise ? Direction.CLOCKWISE : Direction.COUNTERCLOCKWISE;
        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.row == goal.row && current.col == goal.col)
                return buildPath(current);

            for (Direction dir : dirs) {
                int newR = current.row + dir.dr;
                int newC = current.col + dir.dc;

                if (board.isLegal(newR, newC)) {
                    State next = new State(newR, newC, current.g + 1, 0, current, dir.name());
                    if (!visited.contains(next.getId())) {
                        queue.add(next);
                        visited.add(next.getId());
                    }
                }
            }
        }

        return "no path";
    }

    private String buildPath(State goal) {
        List<String> moves = new ArrayList<>();
        State curr = goal;
        while (curr.parent != null) {
            moves.add(curr.move);
            curr = curr.parent;
        }
        Collections.reverse(moves);
        return String.join(",", moves);
    }
}
