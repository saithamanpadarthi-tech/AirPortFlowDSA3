package co3;

import dataset.Flight;

import java.util.Arrays;
import java.util.List;

/**
 * CO3 – Advanced DP: Bitmask DP for Gate / Resource Assignment
 *
 * Models the assignment of n flights to n gates as an exact-cover problem
 * solved with bitmask DP in O(n² · 2^n) time.  Each state dp[mask] is
 * the minimum total cost to assign the first popcount(mask) flights to the
 * gates indicated by the set bits in mask.
 *
 * Practical limit: n ≤ 20 (airport demo uses n ≤ 8 for fast output).
 *
 * Airport use-case: Minimise total turnaround cost when assigning a set of
 * arriving/departing flights to a fixed pool of available gates, factoring
 * in runway proximity, airline preference, or gate capacity.
 */
public class BitmaskDP {

    // ── Cost matrix generation ───────────────────────────────────────────────

    /**
     * Generate a synthetic cost matrix based on runway-gate compatibility.
     * Flight i → gate j costs less when flight's runway matches the gate's
     * primary runway cluster.
     *
     * @param flights list of flights (uses runway field)
     * @return n×n cost matrix
     */
    public static int[][] generateCostMatrix(List<Flight> flights) {
        int n = flights.size();
        String[] runways = {"R1", "R2", "R3"};
        int[][] cost = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String flightRunway = flights.get(i).getRunway();
                String gateRunway   = runways[j % runways.length];
                // Compatible runway → lower base cost; add a positional component
                cost[i][j] = flightRunway.equals(gateRunway)
                        ? 5  + j * 3
                        : 20 + j * 5;
            }
        }
        return cost;
    }

    // ── Bitmask DP ───────────────────────────────────────────────────────────

    /**
     * Compute minimum total cost to assign n flights to n distinct gates.
     * Flight ordering is 0 … n-1; bit j in a mask means gate j is taken.
     *
     * @param cost n×n cost matrix: cost[i][j] = cost of assigning flight i to gate j
     * @param n    number of flights (= number of gates)
     * @return minimum total assignment cost
     */
    public static int minCostAssignment(int[][] cost, int n) {
        int states = 1 << n;
        int[] dp   = new int[states];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;

        for (int mask = 0; mask < states; mask++) {
            if (dp[mask] >= Integer.MAX_VALUE / 2) continue;
            int i = Integer.bitCount(mask); // next flight index
            if (i == n) continue;
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) { // gate j not yet taken
                    int next = mask | (1 << j);
                    dp[next] = Math.min(dp[next], dp[mask] + cost[i][j]);
                }
            }
        }
        return dp[states - 1];
    }

    /**
     * Retrieve the actual gate assignment that achieves minimum cost.
     *
     * @param cost n×n cost matrix
     * @param n    number of flights/gates
     * @return assignment[] where assignment[i] = gate index for flight i
     */
    public static int[] getAssignment(int[][] cost, int n) {
        int states = 1 << n;
        int[] dp   = new int[states];
        int[] from = new int[states]; // which gate was chosen to reach this state
        Arrays.fill(dp,   Integer.MAX_VALUE / 2);
        Arrays.fill(from, -1);
        dp[0] = 0;

        for (int mask = 0; mask < states; mask++) {
            if (dp[mask] >= Integer.MAX_VALUE / 2) continue;
            int i = Integer.bitCount(mask);
            if (i == n) continue;
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    int next    = mask | (1 << j);
                    int newCost = dp[mask] + cost[i][j];
                    if (newCost < dp[next]) {
                        dp[next]   = newCost;
                        from[next] = j;
                    }
                }
            }
        }

        // Back-track to recover assignment
        int[] assignment = new int[n];
        int mask = states - 1;
        for (int i = n - 1; i >= 0; i--) {
            assignment[i] = from[mask];
            mask ^= (1 << from[mask]);
        }
        return assignment;
    }
}
