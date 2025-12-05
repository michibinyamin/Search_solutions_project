import java.util.*;

public class AStar implements Algorithm {

  private final boolean clockwise;
  private final boolean withOpen;
  private final String tieBreaker; // "new-first" or "old-first"

  // Statistics
  private int generatedNodesCount = 0; // "Num" in output
  private int maxSpace = 0;

  // Counter for tie-breaking based on creation time
  private int creationCounter = 0;

  public AStar(String order, boolean withOpen, String tieBreaker) {
    this.clockwise = order.equalsIgnoreCase("clockwise");
    this.withOpen = withOpen;
    if (tieBreaker == null) {
      this.tieBreaker = "old-first"; // default tie-breaking
    } else {
      this.tieBreaker = tieBreaker;
    }
  }

  @Override
  public String solve(Board board) {
    State start = board.getStart();
    State goal = board.getGoal();

    // Priority Queue with custom comparator for F-value and Tie-Breaking
    PriorityQueue<State> openList = new PriorityQueue<>(new Comparator<State>() {
      @Override
      public int compare(State s1, State s2) {
        // 1. Compare f(n) values
        int fCompare = Double.compare(s1.getF(), s2.getF());
        if (fCompare != 0)
          return fCompare;

        // 2. Tie-Breaker: Compare Creation Time according to the input
        if (tieBreaker.equals("new-first")) {
          // Prefer newer nodes (Higher creation time comes first)
          return Integer.compare(s2.getCreationTime(), s1.getCreationTime());
        } else {
          // Prefer older nodes (Lower creation time comes first)
          return Integer.compare(s1.getCreationTime(), s2.getCreationTime());
        }
      }
    });

    // Map to track best g-score found so far for a unique state (row, col, wheels)
    // Key: State ID (String), Value: Double (g-cost)
    Map<String, Double> openMap = new HashMap<>();
    Set<String> closedList = new HashSet<>();

    // Initialize Start
    // start.setG(0);
    start.setH(calculateHeuristic(start, goal, board));
    start.setCreationTime(++creationCounter);
    // Note: Start node is not counted in "Num" according to instructions,
    // but we increment counter to keep time tracking consistent.

    openList.add(start);
    openMap.put(start.getId(), start.getG());
    maxSpace = 1;

    Direction[] dirs = clockwise ? Direction.CLOCKWISE : Direction.COUNTERCLOCKWISE;

    while (!openList.isEmpty()) {

      // Print Open List if requested (For debugging)
      if (withOpen)
        printOpenSet(openList);

      State current = openList.poll();
      openMap.remove(current.getId());

      // If already in closed with a better or equal cost, skip (Lazy deletion)
      if (closedList.contains(current.getId())) {
        continue;
      }

      closedList.add(current.getId());

      // // Check Goal
      // if (current.onGoal) {
      // return returnResult(current);
      // }

      // Expand Neighbors
      for (Direction dir : dirs) {
        int newR, newC;

        // 1. Handle Tunnel Entry
        if (board.isTunnel(current.row, current.col) && dir == Direction.Ent) {
          int[] exit = board.getTunnelExit(current.row, current.col);
          newR = exit[0];
          newC = exit[1];
        } else if (dir == Direction.Ent) {
          continue; // Cannot enter if not on tunnel
        } else {
          // 2. Normal Move
          newR = current.row + dir.dr;
          newC = current.col + dir.dc;
        }

        // checks for wall, bounds, smooth floor without supply station and not going back to parent
        if (board.isLegal(newR, newC, current.SupplyStation, current.parent)) {

          double newG = current.getG() + board.getPositionValue(newR, newC, dir);

          // Create potential next state
          State next = new State(newR, newC, newG, 0, current, dir.name(), board);

          // Check Goal (all of the edges to the goal will be the same weight so no need to check
          // after)
          if (next.onGoal) {
            return returnResult(next);
          }

          // Calculate H and F
          next.setH(calculateHeuristic(next, goal, board));

          // Update Creation Time and Global Counter
          generatedNodesCount++;
          next.setCreationTime(++creationCounter);

          // 4. Algorithm Logic (Open/Closed checks)
          if (closedList.contains(next.getId())) {
            // If we found a cheaper path to a closed node, strictly we should re-open it.
            // But with a consistent heuristic, this rarely happens.
            continue;
          }

          // Check if in Open List with better G
          if (openMap.containsKey(next.getId())) {
            double existingG = openMap.get(next.getId());
            if (newG < existingG) {
              // We found a better path to a node currently in Open.
              // In Java PQ, we can't easily "update", so we add the new one
              // and ignore the old one when popped (lazy approach).
              openList.add(next);
              openMap.put(next.getId(), newG);
            }
          } else {
            // Not in Open or Closed -> Add it
            openList.add(next);
            openMap.put(next.getId(), newG);
            maxSpace = Math.max(maxSpace, openList.size());
          }
        }
      }
    }

    return "no path" + "\nNum: " + generatedNodesCount + "\nMax space: " + maxSpace + "\nCost: inf";
  }

  /**
   * Recommended Heuristic: Tunnel-Aware Chebyshev h(n) = min(Chebyshev(n, goal), Chebyshev(n,
   * tunnel_in) + 2 + Chebyshev(tunnel_out, goal)...)
   */
  private double calculateHeuristic(State current, State goal, Board board) {
    // 1. Direct distance
    double minCost = chebyshevDistance(current.row, current.col, goal.row, goal.col);

    // 2. Check all tunnels for shortcuts
    // Assuming board.getAllTunnels() returns a List of int[]{r1, c1, r2, c2}
    // where (r1,c1) is entrance and (r2,c2) is exit.
    for (int[] tunnel : board.getAllTunnels()) {
      int tInR = tunnel[0];
      int tInC = tunnel[1];
      int tOutR = tunnel[2];
      int tOutC = tunnel[3];

      // Distance to tunnel entrance + cost of tunnel (2) + distance from exit to goal
      double tunnelPathCost = chebyshevDistance(current.row, current.col, tInR, tInC) + 2
          + chebyshevDistance(tOutR, tOutC, goal.row, goal.col);

      if (tunnelPathCost < minCost) {
        minCost = tunnelPathCost;
      }

      // Note: Since tunnels are bidirectional (or numbered pairs),
      // ensure your getAllTunnels provides both directions or check both here.
    }
    return minCost;
  }

  private double chebyshevDistance(int r1, int c1, int r2, int c2) {
    return Math.max(Math.abs(r1 - r2), Math.abs(c1 - c2));
  }

  // Printing method for "with open"
  private void printOpenSet(PriorityQueue<State> openSet) {
    // Create a copy to iterate without destroying order
    PriorityQueue<State> copy = new PriorityQueue<>(openSet.comparator());
    copy.addAll(openSet);

    StringBuilder sb = new StringBuilder("Open list: [");
    while (!copy.isEmpty()) {
      sb.append(copy.poll().toString()); // Ensure State.toString() is concise
      if (!copy.isEmpty())
        sb.append(", ");
    }
    sb.append("]");
    System.out.println(sb.toString());
  }

  @Override
  public String returnResult(State next) {
    return buildPath(next) + "\nNum: " + generatedNodesCount + "\nMax space: " + maxSpace
        + "\nCost: " + (int) next.getG(); // Cast to int if costs are integers
  }

  @Override
  public String buildPath(State goal) {
    // Same as your BFS implementation
    List<String> moves = new ArrayList<>();
    State curr = goal;
    while (curr.parent != null) {
      moves.add(curr.move);
      curr = curr.parent;
    }
    Collections.reverse(moves);
    return String.join("-", moves);
  }
}
