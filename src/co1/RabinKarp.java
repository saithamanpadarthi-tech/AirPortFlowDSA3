package co1;

import java.util.ArrayList;
import java.util.List;

/**
 * CO1 – String Algorithm: Rabin-Karp Rolling Hash
 *
 * Uses a polynomial rolling hash (base=31, mod=1e9+7) to search for
 * all occurrences of a pattern in a text.  Average O(n + m); worst-case
 * O(nm) due to hash collisions (mitigated by character-level verification).
 *
 * Airport use-case: Search baggage codes and booking references which
 * follow structured formats (e.g. "BAG-AI101-001").
 */
public class RabinKarp {

    private static final long BASE = 31L;
    private static final long MOD  = 1_000_000_007L;

    /**
     * Search all occurrences of pattern inside text using Rabin-Karp.
     *
     * @return list of 0-based start positions of every match.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || m > n) return result;

        // Pre-compute BASE^(m-1) mod MOD
        long power = 1;
        for (int i = 0; i < m - 1; i++)
            power = power * BASE % MOD;

        // Initial hash values
        long patHash  = 0;
        long winHash  = 0;
        for (int i = 0; i < m; i++) {
            patHash = (patHash * BASE + charVal(pattern.charAt(i))) % MOD;
            winHash = (winHash  * BASE + charVal(text.charAt(i)))   % MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            if (winHash == patHash) {
                // Verify character by character to avoid spurious matches
                if (text.substring(i, i + m).equals(pattern))
                    result.add(i);
            }
            // Slide the window
            if (i < n - m) {
                winHash = (winHash - charVal(text.charAt(i)) * power % MOD + MOD) % MOD;
                winHash = (winHash * BASE + charVal(text.charAt(i + m))) % MOD;
            }
        }
        return result;
    }

    /** Map a character to its numeric value for hashing. */
    private static long charVal(char c) {
        return c; // use raw ASCII value
    }
}
