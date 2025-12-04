import java.util.*;

public class BFS implements Algorithm {

    private final boolean clockwise;
    private final boolean withOpen; // must implement!!!! must have only closed list?

    private int visitedNodes = 0; // to count expanded nodes
    private int maxSpace = 0;

    public BFS(String order, boolean withOpen) {
        this.clockwise = order.equalsIgnoreCase("clockwise");
        this.withOpen = withOpen;
    }

    @Override
    public String solve(Board board) {
        State start = board.getStart();
        Queue<State> queue = new LinkedList<>();
        Set<String> openList = new HashSet<>();
        Set<String> closedList = new HashSet<>();

        queue.add(start);
        openList.add(start.getId());

        Direction[] dirs = clockwise ? Direction.CLOCKWISE : Direction.COUNTERCLOCKWISE;
        while (!queue.isEmpty()) {
            if (withOpen)
                printOpenSet(openList);
            State current = queue.poll();
            openList.remove(current.getId());
            closedList.add(current.getId());

            for (Direction dir : dirs) {
                int newR;
                int newC;

                // handle tunnel separately
                if (board.isTunnel(current.row, current.col) && dir == Direction.Ent) {
                    int[] exit = board.getTunnelExit(current.row, current.col);
                    newR = exit[0];
                    newC = exit[1];
                } else if (dir == Direction.Ent) { // unnessary check(not standing on a tunnel)
                    continue; // can't enter tunnel if not on one
                } else { // normal move
                    newR = current.row + dir.dr;
                    newC = current.col + dir.dc;
                }

                // checks for wall, bounds, smooth floor without supply station and not going back
                // to
                // parent
                if (board.isLegal(newR, newC, current.SupplyStation, current.parent)) {
                    State next = new State(newR, newC,
                            current.g + board.getPositionValue(newR, newC, dir), 0, current,
                            dir.name(), current.SupplyStation, board);

                    visitedNodes++;

                    // check goal
                    if (next.onGoal)
                        return returnResult(next);

                    if (!closedList.contains(next.getId()) && !openList.contains(next.getId())) {
                        queue.add(next);
                        openList.add(next.getId());
                        maxSpace = Math.max(maxSpace, queue.size());
                    }
                }
            }
        }
        return "no path";
    }

    @Override
    public String buildPath(State goal) {
        List<String> moves = new ArrayList<>();
        State curr = goal;
        while (curr.parent != null) {
            moves.add(curr.move);
            curr = curr.parent;
        }
        Collections.reverse(moves);
        return String.join("-", moves);
    }

    @Override
    public String returnResult(State next) {
        return buildPath(next) + "\n" + "Num: " + visitedNodes + "\n" + "Max space: " + maxSpace
                + "\n" + "Cost: " + next.g;
    }

    private void printOpenSet(Set<String> openSet) {
        String content = String.join("  ", openSet);
        System.out.println("Open list: [" + content + "]");
    }
}
