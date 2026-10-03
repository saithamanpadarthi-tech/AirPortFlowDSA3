package co2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * CO2 – Suffix Structure: Suffix Array
 *
 * Builds a suffix array for string s in O(n log^2 n) using the
 * doubling / prefix-doubling technique (Manber & Myers style).
 * Provides binary-search-based pattern lookup in O(m log n).
 *
 * Airport use-case: Index all suffixes of an operational-note or alert
 * message and answer arbitrary substring queries efficiently.
 */
public class SuffixArray {

    /**
     * Build the suffix array for s.
     *
     * @param s input string (should not contain '\0')
     * @return sa[] where sa[i] is the starting index of the i-th
     *         lexicographically smallest suffix.
     */
    public static int[] build(String s) {
        int n = s.length();
        if (n == 0) return new int[0];

        Integer[] sa   = new Integer[n];
        int[]     rank = new int[n];
        int[]     tmp  = new int[n];

        for (int i = 0; i < n; i++) {
            sa[i]   = i;
            rank[i] = s.charAt(i);
        }

        for (int k = 1; k < n; k <<= 1) {
            final int   kk = k;
            final int[] r  = Arrays.copyOf(rank, n);

            Comparator<Integer> cmp = (a, b) -> {
                if (r[a] != r[b]) return Integer.compare(r[a], r[b]);
                int ra = (a + kk < n) ? r[a + kk] : -1;
                int rb = (b + kk < n) ? r[b + kk] : -1;
                return Integer.compare(ra, rb);
            };

            Arrays.sort(sa, cmp);

            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++)
                tmp[sa[i]] = tmp[sa[i - 1]] + (cmp.compare(sa[i - 1], sa[i]) < 0 ? 1 : 0);

            System.arraycopy(tmp, 0, rank, 0, n);
            if (rank[sa[n - 1]] == n - 1) break; // all ranks unique – done
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    /**
     * Binary-search the suffix array to find all positions where
     * pattern occurs in text.
     *
     * @param text    original string used to build sa
     * @param sa      suffix array of text
     * @param pattern search pattern
     * @return sorted list of start positions (0-based)
     */
    public static List<Integer> search(String text, int[] sa, String pattern) {
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return result;

        // Find leftmost match (lower bound)
        int lo = 0, hi = n - 1, left = n;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int end = Math.min(sa[mid] + m, n);
            String suf = text.substring(sa[mid], end);
            if (suf.compareTo(pattern) < 0) lo = mid + 1;
            else { left = mid; hi = mid - 1; }
        }

        // Find rightmost match (upper bound)
        lo = 0; hi = n - 1;
        int right = -1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int end = Math.min(sa[mid] + m, n);
            String suf = text.substring(sa[mid], end);
            if (suf.compareTo(pattern) > 0) hi = mid - 1;
            else { right = mid; lo = mid + 1; }
        }

        for (int i = left; i <= right; i++)
            result.add(sa[i]);
        Collections.sort(result);
        return result;
    }
}
