package co1;

import java.util.ArrayList;
import java.util.List;

/**
 * CO1 – String Algorithm: Knuth-Morris-Pratt (KMP)
 *
 * Builds the failure (partial-match) table in O(m) then scans the
 * text in O(n), giving overall O(n + m) time complexity.
 *
 * Airport use-case: Search a pattern inside flight-number strings,
 * alert messages, or operational notes.
 */
public class KMP {

    /**
     * Build the failure / prefix function for the given pattern.
     *
     * @param pattern search pattern
     * @return fail[] where fail[i] = length of the longest proper prefix
     *         of pattern[0..i] that is also a suffix.
     */
    public static int[] buildFailure(String pattern) {
        int m = pattern.length();
        int[] fail = new int[m];
        fail[0] = 0;
        int k = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(i))
                k = fail[k - 1];
            if (pattern.charAt(k) == pattern.charAt(i))
                k++;
            fail[i] = k;
        }
        return fail;
    }

    /**
     * Search for all (possibly overlapping) occurrences of pattern in text.
     *
     * @return list of 0-based start indices where pattern occurs in text.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern.isEmpty() || text.isEmpty()) return result;

        int n = text.length();
        int m = pattern.length();
        int[] fail = buildFailure(pattern);
        int q = 0; // number of characters matched so far

        for (int i = 0; i < n; i++) {
            while (q > 0 && pattern.charAt(q) != text.charAt(i))
                q = fail[q - 1];
            if (pattern.charAt(q) == text.charAt(i))
                q++;
            if (q == m) {
                result.add(i - m + 1); // match starts here
                q = fail[q - 1];       // allow overlapping matches
            }
        }
        return result;
    }
}
