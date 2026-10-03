package co3;

import java.util.List;

/**
 * CO3 – Advanced DP: Levenshtein Edit Distance
 *
 * Classic O(mn) DP computing the minimum number of single-character
 * insertions, deletions, or substitutions needed to transform string a
 * into string b.
 *
 * Airport use-case: Autocorrect passenger-entered flight numbers or city
 * names (e.g. "Mumabi" → "Mumbai", "AI10" → "AI101").
 */
public class Levenshtein {

    /**
     * Compute the Levenshtein edit distance between a and b.
     *
     * @param a source string
     * @param b target string
     * @return minimum edit distance (insertions + deletions + substitutions)
     */
    public static int distance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        // Base cases: transforming empty string
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // no operation needed
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],  // substitute
                                   Math.min(dp[i - 1][j],       // delete
                                            dp[i][j - 1]));     // insert
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Return the candidate from the list that is closest (smallest edit
     * distance) to the query string.  Ties are broken by first occurrence.
     *
     * @param query      user-supplied (possibly misspelled) string
     * @param candidates list of valid reference strings
     * @return the element of candidates with minimum distance to query
     */
    public static String closestMatch(String query, List<String> candidates) {
        String best    = null;
        int    bestDist = Integer.MAX_VALUE;
        for (String c : candidates) {
            int d = distance(query, c);
            if (d < bestDist) { bestDist = d; best = c; }
        }
        return best;
    }
}
