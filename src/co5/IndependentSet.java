package co5;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CO5 - NP-Hard: Independent Set (derived from Vertex Cover)
 *
 * RELATIONSHIP TO VERTEX COVER:
 *   A set I ⊆ V is an INDEPENDENT SET iff V \ I is a VERTEX COVER.
 *   Therefore:
 *     Maximum Independent Set = V \ Minimum Vertex Cover
 *
 * NP-COMPLETENESS CHAIN:
 *   3-SAT  --poly-->  Vertex Cover  --complement-->  Independent Set
 *   Independent Set is NP-complete (Karp, 1972).
 *
 * APPROXIMATION:
 *   If we use the 2-approx vertex cover of size k, the derived independent
 *   set has size n - k, which is within factor 2 of the optimum OPT_IS
 *   when combined with the VC guarantee.
 *
 * Airport use-case:
 *   Nodes = airports.
 *   Independent Set = largest group of airports with NO direct routes
 *   between any two (e.g., for parallel independent scheduling zones).
 */
public class IndependentSet {

    /**
     * Derive a maximal independent set from a vertex cover using complement.
     * I = V \ VC
     *
     * @param n           total number of vertices
     * @param vertexCover approximate vertex cover
     * @return independent set (complement of cover)
     */
    public static Set<Integer> fromVertexCover(int n, Set<Integer> vertexCover) {
        Set<Integer> is = new HashSet<>();
        for (int i = 0; i < n; i++)
            if (!vertexCover.contains(i)) is.add(i);
        return is;
    }

    /**
     * Verify that the set is independent (no two members share an edge).
     *
     * @param is    candidate independent set
     * @param edges list of graph edges
     * @return true iff is is a valid independent set
     */
    public static boolean verify(Set<Integer> is, List<int[]> edges) {
        for (int[] e : edges)
            if (is.contains(e[0]) && is.contains(e[1])) return false;
        return true;
    }
}
