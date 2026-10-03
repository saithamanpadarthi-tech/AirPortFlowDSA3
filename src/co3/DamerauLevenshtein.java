package co3;

import java.util.HashMap;
import java.util.Map;

/**
 * CO3 – Advanced DP: Damerau-Levenshtein Edit Distance
 *
 * Extends the classic Levenshtein distance with one additional operation:
 * transposition of two adjacent characters (e.g. "Dlhei" → "Delhi").
 * Uses the true (unrestricted) Damerau-Levenshtein algorithm which handles
 * non-adjacent transpositions via a "last seen" character map.
 * Time: O(mn),  Space: O(mn + |Σ|).
 *
 * Airport use-case: Passengers often transpose characters when typing city
 * names or flight codes on kiosks; this catches those errors that plain
 * Levenshtein may over-penalise.
 */
public class DamerauLevenshtein {

    /**
     * Compute the full Damerau-Levenshtein distance between a and b.
     * Counts insertions, deletions, substitutions, and transpositions.
     *
     * @param a source string
     * @param b target string
     * @return minimum number of edit operations
     */
    public static int distance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int maxDist = m + n;

        // dp is (m+2) × (n+2) – extra row/column for the sentinel
        int[][] dp = new int[m + 2][n + 2];
        dp[0][0] = maxDist;
        for (int i = 0; i <= m; i++) { dp[i + 1][0] = maxDist; dp[i + 1][1] = i; }
        for (int j = 0; j <= n; j++) { dp[0][j + 1] = maxDist; dp[1][j + 1] = j; }

        // da[c] = last row where character c was seen in a
        Map<Character, Integer> da = new HashMap<>();

        for (int i = 1; i <= m; i++) {
            int db = 0; // last column where a[i] was seen in b (within this row)
            for (int j = 1; j <= n; j++) {
                int i1 = da.getOrDefault(b.charAt(j - 1), 0); // last row of b[j] in a
                int j1 = db;                                    // last col of a[i] in b
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                if (cost == 0) db = j; // a[i] == b[j] → update last-seen column

                dp[i + 1][j + 1] = min4(
                    dp[i][j]   + cost,                                     // substitute
                    dp[i + 1][j] + 1,                                      // insert
                    dp[i][j + 1] + 1,                                      // delete
                    dp[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1)         // transpose
                );
            }
            da.put(a.charAt(i - 1), i); // record last row where a[i] was seen
        }
        return dp[m + 1][n + 1];
    }

    private static int min4(int a, int b, int c, int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }
}
