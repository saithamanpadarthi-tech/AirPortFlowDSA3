package co2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CO2 – Suffix Structure: Suffix Automaton (SAM)
 *
 * Constructs the suffix automaton of string s in O(n) time and space.
 * The SAM is the minimal deterministic finite automaton that accepts all
 * suffixes of s.  It can answer "does pattern occur in s?" in O(m) and
 * enumerate occurrences efficiently.
 *
 * Airport use-case: Compact representation for multi-query substring
 * matching over flight alert messages; the O(n) state-count means very
 * memory-efficient indexing.
 */
public class SuffixAutomaton {

    // ── SAM state ────────────────────────────────────────────────────────────
    private static class State {
        int              len;       // length of the longest string in this eq. class
        int              link;      // suffix link (-1 for initial state)
        boolean          isClone;
        Map<Character, Integer> next = new HashMap<>();

        State(int len, int link) {
            this.len    = len;
            this.link   = link;
            this.isClone = false;
        }
    }

    private final List<State> st;
    private int last; // index of the last state after full extension

    // ── Construction ─────────────────────────────────────────────────────────

    public SuffixAutomaton(String s) {
        st   = new ArrayList<>();
        st.add(new State(0, -1)); // initial state
        last = 0;
        for (char c : s.toCharArray())
            extend(c);
    }

    private void extend(char c) {
        // Create new state for the new longest suffix
        int cur = st.size();
        st.add(new State(st.get(last).len + 1, -1));

        int p = last;
        // Walk suffix links until we find a state with a c-transition (or root)
        while (p != -1 && !st.get(p).next.containsKey(c)) {
            st.get(p).next.put(c, cur);
            p = st.get(p).link;
        }

        if (p == -1) {
            // No such transition → link to initial state
            st.get(cur).link = 0;
        } else {
            int q = st.get(p).next.get(c);
            if (st.get(p).len + 1 == st.get(q).len) {
                // q is the correct suffix link
                st.get(cur).link = q;
            } else {
                // Clone q into clone, redirect
                int clone = st.size();
                State cloneState = new State(st.get(p).len + 1, st.get(q).link);
                cloneState.next    = new HashMap<>(st.get(q).next);
                cloneState.isClone = true;
                st.add(cloneState);

                while (p != -1 && st.get(p).next.getOrDefault(c, -1) == q) {
                    st.get(p).next.put(c, clone);
                    p = st.get(p).link;
                }
                st.get(q).link  = clone;
                st.get(cur).link = clone;
            }
        }
        last = cur;
    }

    // ── Query ─────────────────────────────────────────────────────────────────

    /**
     * Check whether pattern is a substring of the string used to build this SAM.
     *
     * @param pattern search string
     * @return true iff pattern occurs in the original text
     */
    public boolean contains(String pattern) {
        int cur = 0;
        for (char c : pattern.toCharArray()) {
            if (!st.get(cur).next.containsKey(c)) return false;
            cur = st.get(cur).next.get(c);
        }
        return true;
    }

    /**
     * Find all start positions of pattern in text (the original string).
     * Uses the SAM for fast containment check; positions are gathered by a
     * linear scan for simplicity (the SAM guarantees existence beforehand).
     *
     * @param text    the string passed to the constructor
     * @param pattern substring to locate
     * @return list of 0-based start positions
     */
    public List<Integer> findOccurrences(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (!contains(pattern)) return result;
        int m = pattern.length();
        for (int i = 0; i <= text.length() - m; i++) {
            if (text.regionMatches(i, pattern, 0, m))
                result.add(i);
        }
        return result;
    }

    /** @return total number of states in the SAM (≤ 2n). */
    public int stateCount() { return st.size(); }
}
