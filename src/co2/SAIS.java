package co2;

import java.util.Arrays;

/**
 * CO2 – Suffix Structure: SA-IS (Suffix Array – Induced Sorting)
 *
 * SA-IS constructs a suffix array in true O(n) time using the
 * induced-sorting principle.  This implementation handles arbitrary
 * byte-valued alphabets and is correct for typical ASCII airport text.
 *
 * Airport use-case: Index very large operational text corpora (e.g.
 * concatenated notes from thousands of flights) where the linear
 * construction time of SA-IS matters over the O(n log^2 n) approach.
 *
 * Reference: Nong, Zhang, Chan (2009) – "Linear Suffix Array Construction
 * by Almost Pure Induced-Sorting".
 */
public class SAIS {

    /** Build suffix array using SA-IS (O(n) time, O(n) space). */
    public static int[] build(String s) {
        if (s.isEmpty()) return new int[0];
        // Map characters to integers starting from 1; 0 is the sentinel
        int n = s.length() + 1;
        int[] t = new int[n];
        for (int i = 0; i < s.length(); i++) t[i] = s.charAt(i);
        t[n - 1] = 0; // sentinel (smallest value)

        int sigma = 0;
        for (int v : t) if (v > sigma) sigma = v;
        sigma++;

        int[] sa = new int[n];
        buildSAIS(t, sa, n, sigma);

        // Drop the sentinel entry (sa[0] always points to the sentinel position)
        int[] result = new int[n - 1];
        int pos = 0;
        for (int v : sa)
            if (v < n - 1) result[pos++] = v;
        return result;
    }

    // ── Core SA-IS recursion ─────────────────────────────────────────────────

    private static void buildSAIS(int[] s, int[] sa, int n, int sigma) {
        boolean[] sType = new boolean[n]; // true = S-type, false = L-type
        sType[n - 1] = true;              // sentinel is S-type
        for (int i = n - 2; i >= 0; i--)
            sType[i] = (s[i] < s[i + 1]) || (s[i] == s[i + 1] && sType[i + 1]);

        // Bucket boundaries
        int[] bktHead = new int[sigma];
        int[] bktTail = new int[sigma];
        getBuckets(s, n, sigma, bktHead, bktTail);

        // Place LMS suffixes into their bucket tails
        Arrays.fill(sa, -1);
        int[] tail = Arrays.copyOf(bktTail, sigma);
        for (int i = 1; i < n; i++)
            if (isLMS(sType, i)) sa[tail[s[i]]--] = i;

        // Induced sort: L-types, then S-types
        induceSortL(s, sa, n, sigma, sType, bktHead);
        induceSortS(s, sa, n, sigma, sType, bktTail);

        // Compact LMS suffixes and name them
        int[] lms = new int[n];
        int lmsLen = 0;
        for (int v : sa)
            if (v > 0 && isLMS(sType, v)) lms[lmsLen++] = v;

        // Assign names
        int[] name = new int[n];
        Arrays.fill(name, -1);
        int cur = 0;
        int prev = -1;
        for (int i = 0; i < lmsLen; i++) {
            int pos = lms[i];
            if (prev == -1 || !lmsEqual(s, sType, prev, pos)) cur++;
            name[pos] = cur - 1;
            prev = pos;
        }

        if (cur < lmsLen) {
            // Not all unique – recurse
            int[] s1 = new int[lmsLen];
            int[] lmsIdx = new int[lmsLen];
            int cnt = 0;
            for (int i = 0; i < n; i++)
                if (name[i] != -1) { lmsIdx[cnt] = i; s1[cnt++] = name[i]; }

            int[] sa1 = new int[lmsLen];
            buildSAIS(s1, sa1, lmsLen, cur);

            // Map back
            getBuckets(s, n, sigma, bktHead, bktTail);
            Arrays.fill(sa, -1);
            tail = Arrays.copyOf(bktTail, sigma);
            for (int i = lmsLen - 1; i >= 0; i--)
                sa[tail[s[lmsIdx[sa1[i]]]]--] = lmsIdx[sa1[i]];
        }

        induceSortL(s, sa, n, sigma, sType, bktHead);
        induceSortS(s, sa, n, sigma, sType, bktTail);
    }

    private static boolean isLMS(boolean[] t, int i) {
        return i > 0 && t[i] && !t[i - 1];
    }

    private static boolean lmsEqual(int[] s, boolean[] t, int a, int b) {
        for (int i = 0; ; i++) {
            boolean aLMS = (i > 0) && isLMS(t, a + i);
            boolean bLMS = (i > 0) && isLMS(t, b + i);
            if (i > 0 && (aLMS || bLMS)) return aLMS && bLMS;
            if (s[a + i] != s[b + i] || t[a + i] != t[b + i]) return false;
        }
    }

    private static void getBuckets(int[] s, int n, int sigma,
                                   int[] head, int[] tail) {
        int[] cnt = new int[sigma];
        for (int i = 0; i < n; i++) cnt[s[i]]++;
        int sum = 0;
        for (int i = 0; i < sigma; i++) {
            head[i] = sum;
            sum += cnt[i];
            tail[i] = sum - 1;
        }
    }

    private static void induceSortL(int[] s, int[] sa, int n, int sigma,
                                    boolean[] t, int[] bktHead) {
        int[] head = Arrays.copyOf(bktHead, sigma);
        for (int i = 0; i < n; i++) {
            if (sa[i] <= 0) continue;
            int j = sa[i] - 1;
            if (!t[j]) sa[head[s[j]]++] = j;
        }
    }

    private static void induceSortS(int[] s, int[] sa, int n, int sigma,
                                    boolean[] t, int[] bktTail) {
        int[] tail = Arrays.copyOf(bktTail, sigma);
        for (int i = n - 1; i >= 0; i--) {
            if (sa[i] <= 0) continue;
            int j = sa[i] - 1;
            if (t[j]) sa[tail[s[j]]--] = j;
        }
    }
}
