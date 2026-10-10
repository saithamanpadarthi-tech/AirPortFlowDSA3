package co4;

import java.util.Arrays;

/**
 * CO4 - Bipartite Matching via Augmenting Paths (Hopcroft-Karp style DFS)
 *
 * Models the flight-to-gate assignment problem as maximum bipartite matching.
 * Left nodes  = flights
 * Right nodes = gates
 * Edge exists if a flight is compatible with a gate (airline, size, runway).
 *
 * Equivalent to max flow on:
 *   Source -> each left node (cap 1) -> edges -> each right node -> Sink (cap 1)
 *
 * Time: O(V * E) per augmenting path, O(E * sqrt(V)) with Hopcroft-Karp BFS.
 */
public class BipartiteMatching {

    private final int L, R;        // number of left/right nodes
    private final boolean[][] adj; // adjacency: adj[l][r] = edge exists
    private final int[] matchL;    // matchL[l] = right node matched to l (-1 if unmatched)
    private final int[] matchR;    // matchR[r] = left node matched to r (-1 if unmatched)

    public BipartiteMatching(int leftSize, int rightSize) {
        L      = leftSize;
        R      = rightSize;
        adj    = new boolean[L][R];
        matchL = new int[L];
        matchR = new int[R];
        Arrays.fill(matchL, -1);
        Arrays.fill(matchR, -1);
    }

    /** Add compatible edge between left node l and right node r. */
    public void addEdge(int l, int r) {
        adj[l][r] = true;
    }

    /** Augmenting-path DFS from left node u. */
    private boolean dfs(int u, boolean[] visited) {
        for (int v = 0; v < R; v++) {
            if (adj[u][v] && !visited[v]) {
                visited[v] = true;
                // Try to match v, or re-match its current partner
                if (matchR[v] == -1 || dfs(matchR[v], visited)) {
                    matchL[u] = v;
                    matchR[v] = u;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compute maximum matching.
     * @return size of maximum matching
     */
    public int maxMatching() {
        int result = 0;
        for (int u = 0; u < L; u++) {
            if (matchL[u] == -1) {
                boolean[] visited = new boolean[R];
                if (dfs(u, visited)) result++;
            }
        }
        return result;
    }

    /** @return matchL[l] = index of right node matched to left node l, or -1. */
    public int[] getMatchL() { return matchL; }
    /** @return matchR[r] = index of left node matched to right node r, or -1. */
    public int[] getMatchR() { return matchR; }
}
