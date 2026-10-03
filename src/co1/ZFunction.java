package co1;

/**
 * CO1 – String Algorithm: Z-Function
 *
 * For string s of length n, Z[i] is the length of the longest substring
 * starting at s[i] that is also a prefix of s.  Computed in O(n).
 *
 * Airport use-case: Detect repeated sub-phrases in operational messages
 * and find a pattern inside a text via the classic P + "$" + T trick.
 */
public class ZFunction {

    /**
     * Compute the Z-array for string s in O(n).
     *
     * @param s input string
     * @return z[] where z[0] = s.length() by convention
     */
    public static int[] compute(String s) {
        int n = s.length();
        int[] z = new int[n];
        z[0] = n;           // whole string matches itself

        int l = 0, r = 0;   // current Z-box window [l, r)
        for (int i = 1; i < n; i++) {
            if (i < r)
                z[i] = Math.min(r - i, z[i - l]);

            // Extend naively from z[i]
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i]))
                z[i]++;

            // Expand Z-box if needed
            if (i + z[i] > r) {
                l = i;
                r = i + z[i];
            }
        }
        return z;
    }
}
