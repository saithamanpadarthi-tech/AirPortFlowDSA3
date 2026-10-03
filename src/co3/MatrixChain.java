package co3;

import dataset.Flight;

import java.util.List;

/**
 * CO3 – Advanced DP: Matrix-Chain Multiplication
 *
 * Given a chain of n matrices A₁, A₂, … , Aₙ with dimensions
 * dims[0]×dims[1], dims[1]×dims[2], … , dims[n-1]×dims[n], finds the
 * parenthesisation that minimises the total number of scalar multiplications
 * using O(n³) DP and O(n²) space.
 *
 * Airport use-case: Operational data pipelines multiply transformation
 * matrices (e.g. passenger-flow × routing × cost matrices).  The order of
 * multiplication dramatically affects the number of floating-point ops;
 * this DP finds the optimal order.
 */
public class MatrixChain {

    /**
     * Compute the minimum number of scalar multiplications for a chain of
     * matrices whose dimensions are given by dims[].
     * Matrix Mᵢ has dimensions dims[i] × dims[i+1].
     *
     * @param dims dimension array of length n+1 (n matrices)
     * @return minimum scalar multiplications
     */
    public static long minMultiplications(int[] dims) {
        int n = dims.length - 1; // number of matrices
        if (n <= 1) return 0;

        long[][] dp    = new long[n][n];
        int[][]  split = new int[n][n];

        // dp[i][j] = min cost to multiply matrices i … j (0-indexed)
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE / 2;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j]
                              + (long) dims[i] * dims[k + 1] * dims[j + 1];
                    if (cost < dp[i][j]) {
                        dp[i][j]    = cost;
                        split[i][j] = k;
                    }
                }
            }
        }
        return dp[0][n - 1];
    }

    /**
     * Return the optimal parenthesisation string using flight-number labels.
     *
     * @param dims    dimension array
     * @param flights list of flights (first n used as matrix labels)
     * @return human-readable parenthesisation, e.g. "((AI101 × 6E202) × SG303)"
     */
    public static String getOptimalOrder(int[] dims, List<Flight> flights) {
        int n = dims.length - 1;
        if (n == 0) return "";
        if (n == 1) return flights.get(0).getFlightNumber();

        long[][]   dp    = new long[n][n];
        int[][]    split = new int[n][n];
        String[]   label = new String[n];
        for (int i = 0; i < n && i < flights.size(); i++)
            label[i] = flights.get(i).getFlightNumber();

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE / 2;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j]
                              + (long) dims[i] * dims[k + 1] * dims[j + 1];
                    if (cost < dp[i][j]) { dp[i][j] = cost; split[i][j] = k; }
                }
            }
        }
        return buildParens(split, label, 0, n - 1);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String buildParens(int[][] split, String[] label, int i, int j) {
        if (i == j) return label[i];
        return "(" + buildParens(split, label, i, split[i][j])
             + " \u00D7 "                                       // × character
             + buildParens(split, label, split[i][j] + 1, j) + ")";
    }
}
