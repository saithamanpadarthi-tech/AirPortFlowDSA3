package co4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * CO4 - Network Flow: Ford-Fulkerson with Edmonds-Karp (BFS augmenting paths)
 *
 * Edmonds-Karp always picks the shortest augmenting path (BFS), giving
 * O(VE^2) worst-case time regardless of capacity values.
 *
 * Airport use-case: Model the gate-runway network as a flow graph and find
 * the maximum number of flights that can simultaneously depart.
 *
 *   Source --> Gates --> Runways --> Sink
 */
public class FordFulkerson {

    private final int[][] cap;      // residual capacity matrix
    private final int[][] origCap;  // original capacities (for min-cut)
    private final int n;

    public FordFulkerson(int nodes) {
        n       = nodes;
        cap     = new int[n][n];
        origCap = new int[n][n];
    }

    /** Add a directed edge u->v with the given capacity. */
    public void addEdge(int u, int v, int capacity) {
        cap[u][v]     += capacity;
        origCap[u][v] += capacity;
    }

    /** BFS to find any s-t augmenting path in the residual graph. */
    private boolean bfs(int s, int t, int[] parent) {
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        q.add(s);
        visited[s] = true;
        parent[s]  = -1;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++) {
                if (!visited[v] && cap[u][v] > 0) {
                    visited[v] = true;
                    parent[v]  = u;
                    if (v == t) return true;
                    q.add(v);
                }
            }
        }
        return false;
    }

    /**
     * Compute max flow from s to t using Edmonds-Karp.
     * Modifies residual capacities in place.
     *
     * @return max flow value
     */
    public int maxFlow(int s, int t) {
        int[] parent = new int[n];
        int flow = 0;

        while (bfs(s, t, parent)) {
            // Find bottleneck along the BFS path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parent[v])
                pathFlow = Math.min(pathFlow, cap[parent[v]][v]);

            // Augment residual capacities
            for (int v = t; v != s; v = parent[v]) {
                cap[parent[v]][v] -= pathFlow;
                cap[v][parent[v]] += pathFlow;
            }
            flow += pathFlow;
        }
        return flow;
    }

    /**
     * Find min-cut edges after maxFlow has been run.
     * An edge (u,v) is in the min-cut if u is reachable from s in the
     * residual graph but v is not.
     *
     * @param s source node
     * @return list of [u, v, originalCapacity] for each cut edge
     */
    public List<int[]> minCut(int s) {
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        q.add(s);
        visited[s] = true;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++)
                if (!visited[v] && cap[u][v] > 0) { visited[v] = true; q.add(v); }
        }
        List<int[]> cuts = new ArrayList<>();
        for (int u = 0; u < n; u++)
            if (visited[u])
                for (int v = 0; v < n; v++)
                    if (!visited[v] && origCap[u][v] > 0)
                        cuts.add(new int[]{u, v, origCap[u][v]});
        return cuts;
    }
}
