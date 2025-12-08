import java.util.*;

public class IDAStar implements Algorithm {

  private final boolean clockwise;
  private final boolean withOpen;

  // Statistics
  private int generatedNodesCount = 0;
  private int maxSpace = 0;
  private int creationCounter = 0;

  public IDAStar(String order, boolean withOpen) {
    this.clockwise = order.equalsIgnoreCase("clockwise");
    this.withOpen = withOpen;
  }

  @Override
  public String solve(Board board) {
    State start = board.getStart();
    State goal = board.getGoal();

    // 1. L <- make_stack and H <- make_hash_table
    Stack<State> L = new Stack<>();
    // H stores the State object to compare F-values later
    Map<String, State> H = new HashMap<>();
    // Helper to track which nodes are marked "out" (simulating the mark on the node)
    Set<String> outSet = new HashSet<>();

    // 2. t <- h(start)
    double t = calculateHeuristic(start, goal, board);

    // 3. While t != infinity
    while (t != Double.MAX_VALUE) {

      // 1. minF <- infinity
      double minF = Double.MAX_VALUE;

      // 2. L.insert(start) and H.insert(start)
      // start.setG(0); // Reset G for fresh iteration if needed
      L.clear();
      H.clear();
      outSet.clear();

      L.push(start);
      H.put(start.getId(), start);

      // 3. While L is not empty
      while (!L.isEmpty()) {

        // Optional: Print Stack if withOpen is requested
        if (withOpen) {
          printStack(L);
        }
        // System.out.println(generatedNodesCount); // for testing purposes

        // Track max space
        maxSpace = Math.max(maxSpace, L.size() + H.size());

        // 1. n <- L.remove_front() (Pop in Java Stack)
        State n = L.pop();

        // 2. If n is marked as "out"
        if (outSet.contains(n.getId())) {
          // 1. H.remove(n)
          H.remove(n.getId());
          outSet.remove(n.getId()); // Clear mark
        }
        // Else (First time visiting n)
        else {
          // 2. mark n as "out" and L.insert(n)
          outSet.add(n.getId());
          L.push(n);

          // 3. For each allowed operator on n
          Direction[] dirs = clockwise ? Direction.CLOCKWISE : Direction.COUNTERCLOCKWISE;

          for (Direction dir : dirs) {
            int newR, newC;

            // Tunnel logic
            if (board.isTunnel(n.row, n.col) && dir == Direction.Ent) {
              int[] exit = board.getTunnelExit(n.row, n.col);
              newR = exit[0];
              newC = exit[1];
            } else if (dir == Direction.Ent) {
              continue;
            } else {
              newR = n.row + dir.dr;
              newC = n.col + dir.dc;
            }

            // 4. g <- operator(n)
            if (board.isLegal(newR, newC, n.SupplyStation, n.parent)) {
              double newG = n.getG() + board.getPositionValue(newR, newC, dir);
              State g = new State(newR, newC, newG, 0, n, dir.name(), board);
              generatedNodesCount++;
              g.setCreationTime(++creationCounter);
              g.setH(calculateHeuristic(g, goal, board)); // Calculate F implicitly (G+H)

              // 1. If f(g) > t
              if (g.getF() > t) {
                if (g.onGoal) {
                  return returnResult(g);
                }
                // 1. minF <- min(minF, f(g))
                minF = Math.min(minF, g.getF());
                // 2. continue
                continue;
              }

              // 2. If H contains g' (same ID) and g' marked "out"
              if (H.containsKey(g.getId()) && outSet.contains(g.getId())) {
                // 1. continue (Cycle in current path)
                continue;
              }

              // 3. If H contains g' and g' NOT marked "out"
              if (H.containsKey(g.getId()) && !outSet.contains(g.getId())) {
                State gPrime = H.get(g.getId());
                // 1. If f(g') > f(g)
                if (gPrime.getF() > g.getF()) {
                  // 1. remove g' from L and H
                  L.remove(gPrime); // O(N) operation in Java Stack
                  H.remove(gPrime.getId());
                } else {
                  // 2. Else continue
                  continue;
                }
              }

              // 4. If goal(g) then return path(g)
              if (g.onGoal) {
                return returnResult(g);
              }

              // 5. L.insert(g) and H.insert(g)
              L.push(g);
              H.put(g.getId(), g);
            }
          }
        }
      }

      // 4. t <- minF
      t = minF;
    }

    // 4. Return false
    return "no path" + "\nNum: " + generatedNodesCount + "\nMax space: " + maxSpace + "\nCost: inf";
  }

  // --- Helper Methods (Same as A*) ---

  /**
   * Recommended Heuristic: Tunnel-Aware Chebyshev
   */
  private double calculateHeuristic(State current, State goal, Board board) {
    double minCost = chebyshevDistance(current.row, current.col, goal.row, goal.col);

    // Check tunnel shortcuts
    for (int[] tunnel : board.getAllTunnels()) {
      int tInR = tunnel[0];
      int tInC = tunnel[1];
      int tOutR = tunnel[2];
      int tOutC = tunnel[3];

      double tunnelPathCost = chebyshevDistance(current.row, current.col, tInR, tInC) + 2
          + chebyshevDistance(tOutR, tOutC, goal.row, goal.col);

      if (tunnelPathCost < minCost) {
        minCost = tunnelPathCost;
      }
    }
    return minCost;
  }

  private double chebyshevDistance(int r1, int c1, int r2, int c2) {
    return Math.max(Math.abs(r1 - r2), Math.abs(c1 - c2));
  }

  // Print stack for debugging
  private void printStack(Stack<State> stack) {
    System.out.println("Stack: " + stack.toString());
  }

  @Override
  public String returnResult(State next) {
    return buildPath(next) + "\nNum: " + generatedNodesCount + "\nMax space: " + maxSpace
        + "\nCost: " + (int) next.getG();
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
}
