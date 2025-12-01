Ex1.java                // main class – reads input.txt, chooses algorithm, writes output.txt
State.java              // THE MOST IMPORTANT CLASS – represents a state
Board.java              // reads and holds the map, finds S and G
Algorithm.java          // interface or abstract class for all algorithms
BFS.java                // implements Algorithm
AStar.java              // implements Algorithm
DFID.java               // recursive
IDAStar.java            // iterative deepening A*
DFBnB.java              // branch and bound with pruning
Direction.java          // enum for the 8 directions + Ent (tunnel entry)
TunnelManager.java      // manages tunnel pairs (tunnel number → two positions)

