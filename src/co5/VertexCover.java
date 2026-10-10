package co5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CO5 - NP-Hard Approximation: Vertex Cover (2-Approximation)
 *
 * PROBLEM (NP-Hard):
 *   Given a graph G=(V,E), find the smallest set C ⊆ V such that every
 *   edge has at least one endpoint in C.
 *
 * REDUCTION CHAIN:
 *   3-SAT -->  Vertex Cover -->  Independent Set
 *   (Every vertex cover complement is an independent set and vice versa.)
 *
 * 2-APPROXIMATION ALGORITHM (Maximal Matching):
 *   While there exists an uncovered edge (u,v):
 *     Add BOTH u and v to the cover.
 *   => Always ≤ 2 * OPT  (since OPT must pick at least one endpoint of each
 *      matched edge, and we pick matched pairs).
 *
 * Airport use-case:
 *   Nodes = airports,  Edges = direct flight routes.
 *   Vertex Cover = minimum set of airports to station inspectors so that
 *   every route is monitored by at least one endpoint airport.
 */
public class VertexCover {

    private final int n;
    private final List<int[]> edges; // each int[2] = {u, v}

    public VertexCover(int vertices) {
        n     = vertices;
        edges = new ArrayList<>();
    }

    /** Add an undirected edge (u, v). */
    public void addEdge(int u, int v) {
        edges.add(new int[]{u, v});
    }

    /**
     * 2-approximation for Vertex Cover using greedy maximal matching.
     * Approximation ratio: 2   (i.e., |cover| <= 2 * |OPT|)
     *
     * @return set of vertex indices in the approximate cover
     */
    public Set<Integer> approx2() {
        Set<Integer> cover   = new HashSet<>();
        boolean[] edgeCovered = new boolean[edges.size()];

        for (int i = 0; i < edges.size(); i++) {
            if (edgeCovered[i]) continue;
            int u = edges.get(i)[0];
            int v = edges.get(i)[1];
            // Add both endpoints to cover
            cover.add(u);
            cover.add(v);
            // Mark all edges incident to u or v as covered
            for (int j = i + 1; j < edges.size(); j++) {
                int a = edges.get(j)[0], b = edges.get(j)[1];
                if (a == u || a == v || b == u || b == v)
                    edgeCovered[j] = true;
            }
        }
        return cover;
    }

    /**
     * Verify that cover is valid (every edge has at least one endpoint in cover).
     */
    public boolean verify(Set<Integer> cover) {
        for (int[] e : edges)
            if (!cover.contains(e[0]) && !cover.contains(e[1])) return false;
        return true;
    }

    public List<int[]> getEdges() { return edges; }
    public int getN()             { return n; }
}
