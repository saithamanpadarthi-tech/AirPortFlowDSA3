package co2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * CO2 – Suffix Structure: LCP Array via Kasai's Algorithm
 *
 * Given string s and its suffix array sa, Kasai's algorithm computes
 * the LCP array in O(n) using the rank array and the key observation
 * that lcp(s[i..], s[sa[rank[i]-1]..]) ≥ lcp(s[i-1..], ...) - 1.
 *
 * Airport use-case: Identify repeated operational descriptions, phrases,
 * or prefixes shared across multiple flight records.
 */
public class LCPKasai {

    /**
     * Build the LCP array in O(n) using Kasai's algorithm.
     *
     * @param s  original string
     * @param sa suffix array of s
     * @return lcp[] where lcp[i] = length of the longest common prefix
     *         between suffixes sa[i-1] and sa[i].  lcp[0] = 0 by convention.
     */
    public static int[] buildLCP(String s, int[] sa) {
        int n = s.length();
        int[] lcp  = new int[n];
        int[] rank = new int[n];

        // Invert the suffix array: rank[sa[i]] = i
        for (int i = 0; i < n; i++)
            rank[sa[i]] = i;

        int h = 0; // current LCP extension
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // suffix preceding s[i..] in sorted order
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h))
                    h++;
                lcp[rank[i]] = h;
                if (h > 0) h--; // Kasai's key decrement
            }
        }
        return lcp;
    }

    /**
     * Find all distinct repeated substrings of length ≥ minLen.
     *
     * A substring is "repeated" if its LCP value with the previous suffix
     * in sorted order is ≥ minLen, meaning at least two suffixes share that
     * prefix.
     *
     * @param s      original string
     * @param sa     suffix array
     * @param lcp    LCP array from {@link #buildLCP}
     * @param minLen minimum repeat length
     * @return de-duplicated list of repeated substrings
     */
    public static List<String> findRepeatedSubstrings(String s, int[] sa,
                                                       int[] lcp, int minLen) {
        Set<String> seen = new LinkedHashSet<>();
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] >= minLen) {
                seen.add(s.substring(sa[i], sa[i] + lcp[i]));
            }
        }
        return new ArrayList<>(seen);
    }
}
