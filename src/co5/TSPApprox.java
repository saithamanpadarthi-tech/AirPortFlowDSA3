package co5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CO5 - NP-Hard Approximation: Travelling Salesman Problem (TSP)
 *
 * PROBLEM (NP-Hard):
 *   Find the shortest Hamiltonian cycle visiting all n nodes exactly once.
 *
 * NEAREST NEIGHBOUR HEURISTIC:
 *   Start at node 0. Always visit the nearest unvisited node.
 *   No guaranteed ratio for general TSP, but in practice ≈ 20-25% above OPT.
 *   For METRIC TSP (triangle inequality):
 *     - Nearest Neighbour: O(log n) approx in worst case
 *     - Christofides:      3/2 approx (best known polynomial algorithm)
 *
 * Airport use-case:
 *   Nodes = airport gates.
 *   Ground crew must visit all gates for pre-flight checks.
 *   TSP finds the shortest route.
 */
public class TSPApprox {

    /**
     * Nearest-Neighbour heuristic for TSP.
     * Starts at node 0 and greedily visits the nearest unvisited node.
     *
     * @param dist symmetric distance matrix (dist[i][j] >= 0)
     * @return tour as ordered list of node indices (returns to 0 at end)
     */
    public static List<Integer> nearestNeighbour(int[][] dist) {
        int n = dist.length;
        boolean[]    visited = new boolean[n];
        List<Integer> tour   = new ArrayList<>();

        int current = 0;
        visited[0] = true;
        tour.add(0);

        for (int step = 1; step < n; step++) {
            int nearest = -1, minD = Integer.MAX_VALUE;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && dist[current][v] < minD) {
                    minD    = dist[current][v];
                    nearest = v;
                }
            }
            visited[nearest] = true;
            tour.add(nearest);
            current = nearest;
        }
        tour.add(0); // return to start
        return tour;
    }

    /** Compute total cost of a tour. */
    public static int tourCost(List<Integer> tour, int[][] dist) {
        int cost = 0;
        for (int i = 0; i < tour.size() - 1; i++)
            cost += dist[tour.get(i)][tour.get(i + 1)];
        return cost;
    }

    /**
     * Generate a deterministic distance matrix seeded with a fixed random.
     * Distances satisfy triangle inequality (Euclidean distances on random points).
     *
     * @param n       number of nodes
     * @param gridMax coordinate grid size
     * @return n x n integer distance matrix
     */
    public static int[][] generateDistMatrix(int n, int gridMax) {
        Random rnd = new Random(42);
        int[] x = new int[n], y = new int[n];
        for (int i = 0; i < n; i++) { x[i] = rnd.nextInt(gridMax); y[i] = rnd.nextInt(gridMax); }
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                dist[i][j] = (int) Math.round(Math.sqrt((x[i]-x[j])*(double)(x[i]-x[j]) + (y[i]-y[j])*(double)(y[i]-y[j])));
        return dist;
    }
}
