package co3;

/**
 * CO3 – Advanced DP: Optimal Binary Search Tree (OBST)
 *
 * Given n keys with access frequencies freq[0..n-1], computes the BST
 * structure that minimises the expected search cost using O(n²) DP.
 * The cost of a BST is Σ freq[i] × (depth(i) + 1).
 *
 * Airport use-case: Flight records are accessed with different frequencies
 * (popular routes have higher access rates).  An optimal BST over sorted
 * flight keys minimises average lookup time across millions of queries.
 */
public class OptimalBST {

    /**
     * Compute the minimum expected search cost for n keys with given
     * access frequencies.
     *
     * @param freq normalised or raw access frequencies (freq[i] ≥ 0)
     * @param n    number of keys
     * @return minimum expected search cost
     */
    public static double minCost(double[] freq, int n) {
        // cost[i][j]   = min expected cost for keys i..j
        // weight[i][j] = sum of freq[i..j]
        double[][] cost   = new double[n + 1][n + 1];
        double[][] weight = new double[n + 1][n + 1];

        // Single keys
        for (int i = 0; i < n; i++) {
            cost[i][i]   = freq[i];
            weight[i][i] = freq[i];
        }

        // Chains of increasing length
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                weight[i][j] = weight[i][j - 1] + freq[j];
                cost[i][j]   = Double.MAX_VALUE;
                // Try each key r as root of the sub-tree [i..j]
                for (int r = i; r <= j; r++) {
                    double leftCost  = (r > i) ? cost[i][r - 1] : 0.0;
                    double rightCost = (r < j) ? cost[r + 1][j] : 0.0;
                    double c = leftCost + rightCost + weight[i][j];
                    if (c < cost[i][j]) cost[i][j] = c;
                }
            }
        }
        return cost[0][n - 1];
    }

    /**
     * Compute the root index of each sub-tree in the optimal BST.
     *
     * @param freq access frequencies
     * @param n    number of keys
     * @return root[0][n-1] is the overall root index (0-based into freq[])
     */
    public static int[][] getRootTable(double[] freq, int n) {
        double[][] cost   = new double[n][n];
        double[][] weight = new double[n][n];
        int[][]    root   = new int[n][n];

        for (int i = 0; i < n; i++) {
            cost[i][i]   = freq[i];
            weight[i][i] = freq[i];
            root[i][i]   = i;
        }

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                weight[i][j] = weight[i][j - 1] + freq[j];
                cost[i][j]   = Double.MAX_VALUE;
                for (int r = i; r <= j; r++) {
                    double leftCost  = (r > i) ? cost[i][r - 1] : 0.0;
                    double rightCost = (r < j) ? cost[r + 1][j] : 0.0;
                    double c = leftCost + rightCost + weight[i][j];
                    if (c < cost[i][j]) { cost[i][j] = c; root[i][j] = r; }
                }
            }
        }
        return root;
    }
}
