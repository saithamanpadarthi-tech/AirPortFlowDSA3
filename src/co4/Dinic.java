package co4;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * CO4 - Network Flow: Dinic's Algorithm
 *
 * Key idea (intuition level):
 *   1. BFS builds a LAYERED GRAPH (level graph) from source to sink.
 *   2. DFS pushes a BLOCKING FLOW through the layered graph.
 *   3. Repeat until sink is unreachable.
 *
 * Complexity:
 *   - General graphs:        O(V^2 * E)
 *   - Unit-capacity graphs:  O(E * sqrt(V))  <- ideal for bipartite matching
 *
 * Why faster than Edmonds-Karp?
 *   - Each phase saturates an entire level, not just one path.
 *   - At most O(V) phases, each blocking flow in O(VE).
 *
 * Airport use-case: Higher-throughput flow computation on larger
 * gate-runway networks where Edmonds-Karp is too slow.
 */
public class Dinic {

    private final int[][] cap;
    private final int n;
    private int[] level;  // BFS level of each node
    private int[] iter;   // current edge pointer for each node (DFS optimization)

    public Dinic(int n) {
        this.n  = n;
        cap     = new int[n][n];
        level   = new int[n];
        iter    = new int[n];
    }

    /** Add directed edge u->v with given capacity. */
    public void addEdge(int u, int v, int c) {
        cap[u][v] += c;
    }

    /**
     * BFS to build the level graph.
     * @return true if sink t is reachable from source s.
     */
    private boolean bfs(int s, int t) {
        Arrays.fill(level, -1);
        Queue<Integer> q = new LinkedList<>();
        level[s] = 0;
        q.add(s);
        while (!q.isEmpty()) {
            int v = q.poll();
            for (int u = 0; u < n; u++) {
                if (cap[v][u] > 0 && level[u] < 0) {
                    level[u] = level[v] + 1;
                    q.add(u);
                }
            }
        }
        return level[t] >= 0;
    }

    /**
     * DFS to find a blocking flow in the level graph.
     * @param v   current node
     * @param t   sink
     * @param f   flow we can push
     * @return    flow pushed to sink
     */
    private int dfs(int v, int t, int f) {
        if (v == t) return f;
        for (; iter[v] < n; iter[v]++) {
            int u = iter[v];
            if (cap[v][u] > 0 && level[v] < level[u]) {
                int d = dfs(u, t, Math.min(f, cap[v][u]));
                if (d > 0) {
                    cap[v][u] -= d;
                    cap[u][v] += d;
                    return d;
                }
            }
        }
        return 0;
    }

    /**
     * Compute max flow from s to t.
     * @return max flow value
     */
    public int maxFlow(int s, int t) {
        int flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(iter, 0);
            int f;
            while ((f = dfs(s, t, Integer.MAX_VALUE)) > 0)
                flow += f;
        }
        return flow;
    }

    /** @return level array after the last BFS (for analysis display). */
    public int[] getLevels() { return level; }
}
