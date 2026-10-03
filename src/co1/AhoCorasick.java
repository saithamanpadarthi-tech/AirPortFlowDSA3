package co1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * CO1 – String Algorithm: Aho-Corasick Automaton
 *
 * Builds a trie of all patterns, computes failure links (like KMP on a
 * trie), and scans text once in O(n + total_matches).  Total build time
 * O(sum of pattern lengths).
 *
 * Airport use-case: Simultaneously detect multiple flight-status keywords
 * ("DELAYED", "CANCELLED", "BOARDING", etc.) in a single pass over an
 * alert message.
 */
public class AhoCorasick {

    private static final int ALPHA = 128; // printable ASCII

    private int[][] go;          // trie transitions
    private int[]   fail;        // failure links
    private int[]   output;      // bitmask: which patterns end at this state
    private List<String> patterns;
    private int size;            // number of trie nodes created

    public AhoCorasick() {
        patterns = new ArrayList<>();
        // Pre-allocate for up to 2000 nodes; resize dynamically if needed
        go     = new int[2000][ALPHA];
        fail   = new int[2000];
        output = new int[2000];
        size   = 1; // root = node 0
        Arrays.fill(go[0], -1);
    }

    /** Add a pattern to the trie (call before build()). */
    public void addPattern(String pattern) {
        int cur = 0;
        for (char c : pattern.toCharArray()) {
            int ch = (c < ALPHA) ? c : 0;
            if (go[cur][ch] == -1) {
                Arrays.fill(go[size], -1);
                output[size] = 0;
                go[cur][ch] = size++;
            }
            cur = go[cur][ch];
        }
        // Mark end of this pattern with its bit
        output[cur] |= (1 << patterns.size());
        patterns.add(pattern);
    }

    /**
     * Build failure links via BFS.  Must be called after all addPattern() calls
     * and before any search().
     */
    public void build() {
        Queue<Integer> queue = new LinkedList<>();
        fail[0] = 0;

        // Initialise root's children
        for (int c = 0; c < ALPHA; c++) {
            if (go[0][c] == -1) {
                go[0][c] = 0; // missing → loop back to root
            } else {
                fail[go[0][c]] = 0;
                queue.add(go[0][c]);
            }
        }

        // BFS
        while (!queue.isEmpty()) {
            int u = queue.poll();
            output[u] |= output[fail[u]]; // propagate output via suffix link
            for (int c = 0; c < ALPHA; c++) {
                if (go[u][c] == -1) {
                    go[u][c] = go[fail[u]][c]; // shortcut
                } else {
                    fail[go[u][c]] = go[fail[u]][c];
                    queue.add(go[u][c]);
                }
            }
        }
    }

    /**
     * Search text for all added patterns simultaneously.
     *
     * @return map from matched-pattern-string → list of start positions (0-based).
     *         Patterns with zero matches are excluded from the result.
     */
    public Map<String, List<Integer>> search(String text) {
        Map<String, List<Integer>> result = new LinkedHashMap<>();
        for (String p : patterns)
            result.put(p, new ArrayList<>());

        int cur = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int ch = (c < ALPHA) ? c : 0;
            cur = go[cur][ch];

            if (output[cur] != 0) {
                for (int j = 0; j < patterns.size(); j++) {
                    if ((output[cur] & (1 << j)) != 0) {
                        int start = i - patterns.get(j).length() + 1;
                        result.get(patterns.get(j)).add(start);
                    }
                }
            }
        }
        // Remove empty entries
        result.entrySet().removeIf(e -> e.getValue().isEmpty());
        return result;
    }

    /** Returns the list of patterns in insertion order. */
    public List<String> getPatterns() { return patterns; }
}
