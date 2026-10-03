package co2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CO2 – Suffix Structure: Suffix Tree (Ukkonen's Online Algorithm)
 *
 * Constructs an implicit suffix tree for string s in O(n) time and space
 * using Ukkonen's online algorithm.  After construction, any substring
 * query is answered in O(m + k) where m is the pattern length and k is
 * the number of occurrences.
 *
 * Airport use-case: Efficient substring queries on concatenated flight
 * operational notes, e.g. "find all notes mentioning 'runway maintenance'".
 */
public class SuffixTree {

    private static final int INF = Integer.MAX_VALUE / 2;

    // ── Ukkonen's internal node ──────────────────────────────────────────────
    private static class Node {
        int  start;          // edge label start index in s
        int  end;            // edge label end index (INF for leaves)
        int  suffixLink;     // suffix link (0 = root)
        int  suffixIndex;    // for leaves: which suffix; -1 for internal nodes
        Map<Character, Integer> children = new HashMap<>();

        Node(int start, int end) {
            this.start       = start;
            this.end         = end;
            this.suffixLink  = 0;
            this.suffixIndex = -1;
        }

        int edgeLen(int phase) {
            return Math.min(end, phase + 1) - start;
        }
    }

    private final String    s;       // text + '$' sentinel
    private List<Node>      nodes;
    private int             activeNode, activeEdge, activeLength;
    private int             remaining;
    private int             leafEnd;
    private int             size;    // number of leaves seen so far

    public SuffixTree(String text) {
        this.s      = text + "$";
        this.nodes  = new ArrayList<>();
        this.nodes.add(new Node(-1, -1)); // root = index 0
        activeNode   = 0;
        activeEdge   = 0;
        activeLength = 0;
        remaining    = 0;
        leafEnd      = -1;

        for (int i = 0; i < s.length(); i++)
            extend(i);

        // Set suffix indices on leaves via DFS
        setSuffixIndices(0, 0);
    }

    // ── Ukkonen extension ────────────────────────────────────────────────────

    private int newNode(int start, int end) {
        nodes.add(new Node(start, end));
        return nodes.size() - 1;
    }

    private void extend(int pos) {
        leafEnd = pos;
        remaining++;
        int lastNewInternal = -1;

        while (remaining > 0) {
            if (activeLength == 0) activeEdge = pos;

            char aChar = s.charAt(activeEdge);
            Node an    = nodes.get(activeNode);

            if (!an.children.containsKey(aChar)) {
                // Rule 2a: create a new leaf
                an.children.put(aChar, newNode(pos, INF));
                if (lastNewInternal != -1) {
                    nodes.get(lastNewInternal).suffixLink = activeNode;
                    lastNewInternal = -1;
                }
            } else {
                int next     = an.children.get(aChar);
                Node nextNode = nodes.get(next);
                int edgeLen  = nextNode.edgeLen(pos);

                // Walk-down (if active length goes past edge)
                if (activeLength >= edgeLen) {
                    activeEdge   += edgeLen;
                    activeLength -= edgeLen;
                    activeNode   = next;
                    continue;
                }

                // Rule 3: character already in tree – do nothing (just extend active point)
                if (s.charAt(nextNode.start + activeLength) == s.charAt(pos)) {
                    activeLength++;
                    if (lastNewInternal != -1)
                        nodes.get(lastNewInternal).suffixLink = activeNode;
                    break;
                }

                // Rule 2b: split edge
                int split = newNode(nextNode.start, nextNode.start + activeLength);
                an.children.put(aChar, split);

                // New leaf for current character
                nodes.get(split).children.put(s.charAt(pos), newNode(pos, INF));

                // Remaining part of old edge
                nextNode.start += activeLength;
                nodes.get(split).children.put(s.charAt(nextNode.start), next);

                if (lastNewInternal != -1)
                    nodes.get(lastNewInternal).suffixLink = split;
                lastNewInternal = split;
            }

            remaining--;

            if (activeNode == 0 && activeLength > 0) {
                activeLength--;
                activeEdge = pos - remaining + 1;
            } else if (nodes.get(activeNode).suffixLink > 0) {
                activeNode = nodes.get(activeNode).suffixLink;
            } else {
                activeNode = 0;
            }
        }
    }

    // ── Post-processing: assign suffix indices ────────────────────────────────

    private int setSuffixIndices(int nodeIdx, int labelHeight) {
        Node node = nodes.get(nodeIdx);
        if (node.children.isEmpty()) {
            // Leaf: suffix index = s.length() - labelHeight
            node.suffixIndex = s.length() - labelHeight;
            return 1;
        }
        for (int child : node.children.values()) {
            Node childNode = nodes.get(child);
            int  elen      = childNode.edgeLen(s.length() - 1);
            setSuffixIndices(child, labelHeight + elen);
        }
        return 0;
    }

    // ── Search ───────────────────────────────────────────────────────────────

    /**
     * Find all starting positions of pattern in the original text (excluding
     * the appended '$' sentinel).
     *
     * @param pattern substring to look for
     * @return sorted list of 0-based start positions
     */
    public List<Integer> search(String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern.isEmpty()) return result;

        int cur = 0; // current node
        int pi  = 0; // index into pattern

        while (pi < pattern.length()) {
            char c    = pattern.charAt(pi);
            Node node = nodes.get(cur);

            if (!node.children.containsKey(c)) return result; // not found

            int  child     = node.children.get(c);
            Node childNode = nodes.get(child);
            int  start     = childNode.start;
            int  end       = Math.min(childNode.end == INF ? s.length() : childNode.end,
                                      s.length());

            // Match along edge
            for (int ei = start; ei < end && pi < pattern.length(); ei++, pi++) {
                if (s.charAt(ei) != pattern.charAt(pi)) return result;
            }
            if (pi < pattern.length()) cur = child;
            else { collectLeaves(child, result); }
        }

        if (pi == pattern.length() && result.isEmpty())
            collectLeaves(cur, result);

        Collections.sort(result);
        return result;
    }

    /** DFS to collect all leaf suffix-indices under a given node. */
    private void collectLeaves(int nodeIdx, List<Integer> result) {
        Node node = nodes.get(nodeIdx);
        if (node.children.isEmpty()) {
            // Leaf: suffixIndex is the start position in s (including '$')
            int idx = node.suffixIndex;
            if (idx >= 0 && idx < s.length() - 1) // exclude sentinel position
                result.add(idx);
            return;
        }
        for (int child : node.children.values())
            collectLeaves(child, result);
    }

    /** @return number of nodes in the suffix tree. */
    public int nodeCount() { return nodes.size(); }
}
