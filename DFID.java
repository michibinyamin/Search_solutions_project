import java.util.*;


public class DFID implements Algorithm {

  private final boolean clockwise;
  private final boolean withOpen;

  private int visitedNodes = 0;
  // For DFID, maxSpace represents the maximum depth reached (O(d))
  private int maxSpace = 0;

  // Helper signals to distinguish between "hit depth limit" and "dead end"
  private static final String CUTOFF = "cutoff";
  private static final String FAIL = "fail";

  public DFID(String order, boolean withOpen) {
    this.clockwise = order.equalsIgnoreCase("clockwise");
    this.withOpen = withOpen;
  }

  @Override
  public String solve(Board board) {
    State start = board.getStart();

    // Iterative Deepening Loop
    // We start with limit 1 and increase indefinitely until found or fail
    for (int limit = 1; limit < Integer.MAX_VALUE; limit++) {

      // Loop avoidance set: tracks only nodes on the CURRENT recursion stack
      Set<String> currentPath = new LinkedHashSet<>(); // !!!!

      // Update maxSpace (in DFID this tracks the depth)
      maxSpace = Math.max(maxSpace, limit);

      String result = limitedDFS(start, limit, currentPath, board);

      if (!result.equals(CUTOFF) && !result.equals(FAIL)) {
        return result; // Found the goal!
      }

      // If we searched the whole tree (FAIL) and didn't hit a cutoff,
      // the goal is unreachable.
      if (result.equals(FAIL)) {
        return "no path";
      }

      // If result was CUTOFF, the loop continues with limit++
    }
    return "no path";
  }

  // Recursive function
  private String limitedDFS(State current, int limit, Set<String> path, Board board) {

    // 1. Check Goal
    if (current.onGoal) {
      return returnResult(current);
    }

    // 2. Check Limit
    if (limit == 0) {
      return CUTOFF;
    }

    // 3. Loop Avoidance Logic
    // Add current to path to prevent children from looping back to here
    path.add(current.getId());

    if (withOpen) {
      printOpenSet(path);
    }

    boolean cutoffOccurred = false;
    Direction[] dirs = clockwise ? Direction.CLOCKWISE : Direction.COUNTERCLOCKWISE;

    for (Direction dir : dirs) {
      int newR, newC;

      // --- Tunnel and Move Logic (Same as BFS) ---
      if (board.isTunnel(current.row, current.col) && dir == Direction.Ent) {
        int[] exit = board.getTunnelExit(current.row, current.col);
        newR = exit[0];
        newC = exit[1];
      } else if (dir == Direction.Ent) {
        continue;
      } else {
        newR = current.row + dir.dr;
        newC = current.col + dir.dc;
      }
      // -------------------------------------------

      if (board.isLegal(newR, newC, current.SupplyStation, current.parent)) {
        State next = new State(newR, newC, current.getG() + board.getPositionValue(newR, newC, dir),
            0, current, dir.name(), board);

        // LOOP AVOIDANCE CHECK:
        // Only visit if 'next' is NOT currently in our ancestors list
        if (!path.contains(next.getId())) {

          visitedNodes++; // Count generation

          // RECURSIVE CALL
          String result = limitedDFS(next, limit - 1, path, board);

          if (result.equals(CUTOFF)) {
            cutoffOccurred = true; // Record that we hit a limit down this branch
          } else if (!result.equals(FAIL)) {
            return result; // We found the goal, bubble it up
          }
        }
      }
    }

    // 4. Backtracking (Crucial for "No Closed List")
    // Remove current from path so it can be visited via other routes in the future
    path.remove(current.getId());

    // If we hit a limit anywhere below, return CUTOFF. Otherwise, it's a dead end (FAIL).
    return cutoffOccurred ? CUTOFF : FAIL;
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
    return buildPath(next) + "\n" + "Num: " + visitedNodes + "\n" + "Max space: " + maxSpace + "\n"
        + "Cost: " + next.getG();
  }

  private void printOpenSet(Set<String> path) {
    // In DFS, the "Open Set" is visually the current recursion stack
    String content = String.join(" -> ", path);
    System.out.println("Current Path: [" + content + "]");
  }
}
