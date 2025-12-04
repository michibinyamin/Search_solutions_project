public interface Algorithm {
    String solve(Board board);

    String buildPath(State goal);

    String returnResult(State next);
}
