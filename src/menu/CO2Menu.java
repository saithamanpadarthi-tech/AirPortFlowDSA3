package menu;

import co2.LCPKasai;
import co2.SAIS;
import co2.SuffixArray;
import co2.SuffixAutomaton;
import co2.SuffixTree;
import dataset.AirportDataset;
import dataset.Flight;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * CO2 sub-menu: Suffix Structures
 *   1. Suffix Array   (O(n log^2 n))
 *   2. SA-IS          (O(n))
 *   3. LCP / Kasai    (O(n))
 *   4. Suffix Tree    (Ukkonen's, O(n))
 *   5. Suffix Automaton (SAM, O(n))
 */
public class CO2Menu {

    private final Scanner        scanner;
    private final AirportDataset dataset;

    public CO2Menu(Scanner scanner) {
        this.scanner = scanner;
        this.dataset = AirportDataset.getInstance();
    }

    // ── Menu loop ─────────────────────────────────────────────────────────────

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n  ============================================");
            System.out.println("       CO2 - Suffix Structures Module");
            System.out.println("  ============================================");
            System.out.println("    1.  Suffix Array      - Index & binary-search docs");
            System.out.println("    2.  SA-IS             - Linear-time suffix array");
            System.out.println("    3.  LCP / Kasai       - Repeated descriptions");
            System.out.println("    4.  Suffix Tree       - Ukkonen substring queries");
            System.out.println("    5.  Suffix Automaton  - Compact pattern matching");
            System.out.println("    6.  Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Enter choice: ");

            switch (readInt()) {
                case 1: runSuffixArray();    break;
                case 2: runSAIS();           break;
                case 3: runLCPKasai();       break;
                case 4: runSuffixTree();     break;
                case 5: runSuffixAutomaton();break;
                case 6: running = false;     break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ── 1. Suffix Array ───────────────────────────────────────────────────────

    private void runSuffixArray() {
        System.out.println("\n  == Suffix Array (O(n log^2 n) prefix-doubling) ==");
        Flight f = pickFlight("operational note");
        if (f == null) return;

        String text = f.getOperationalNote().toLowerCase();
        System.out.println("  Building SA for: \"" + text + "\"");
        int[] sa = SuffixArray.build(text);

        System.out.println("\n  -- Suffix Array (first 10 entries) --");
        printSA(text, sa, 10);

        System.out.print("\n  Enter pattern to search via SA binary search: ");
        String pattern = readLine().toLowerCase();
        List<Integer> pos = SuffixArray.search(text, sa, pattern);
        printSearchResult(pattern, pos);
    }

    // ── 2. SA-IS ─────────────────────────────────────────────────────────────

    private void runSAIS() {
        System.out.println("\n  == SA-IS - Linear Time O(n) Suffix Array ==");
        Flight f = pickFlight("alert message");
        if (f == null) return;

        String text = f.getAlertMessage().toLowerCase();
        System.out.println("  Building SA-IS for: \"" + text + "\"");
        int[] sa = SAIS.build(text);

        System.out.println("\n  -- SA-IS Result (first 10 entries) --");
        printSA(text, sa, 10);

        System.out.println("\n  SA-IS vs. basic SA:");
        System.out.println("    SA-IS  : O(n)        - induced sorting, no comparisons");
        System.out.println("    Basic  : O(n log^2 n) - comparison sort with doubling");
        System.out.println("  Both produce the same sorted suffix array.");

        System.out.print("\n  Search pattern in SA-IS array: ");
        String pattern = readLine().toLowerCase();
        List<Integer> pos = SuffixArray.search(text, sa, pattern);
        printSearchResult(pattern, pos);
    }

    // ── 3. LCP / Kasai ────────────────────────────────────────────────────────

    private void runLCPKasai() {
        System.out.println("\n  == LCP Array via Kasai's O(n) Algorithm ==");
        Flight f = pickFlight("operational note");
        if (f == null) return;

        String text = f.getOperationalNote().toLowerCase();
        int[]  sa   = SuffixArray.build(text);
        int[]  lcp  = LCPKasai.buildLCP(text, sa);

        System.out.println("  Flight:  " + f.getFlightNumber() + " - " + f.getOrigin() + " -> " + f.getDestination());
        System.out.println("  Text:    \"" + text + "\"");
        System.out.println("\n  Suffix Array: " + Arrays.toString(Arrays.copyOf(sa, Math.min(sa.length, 15))));
        System.out.println("  LCP Array:    " + Arrays.toString(Arrays.copyOf(lcp, Math.min(lcp.length, 15))));
        System.out.println("  LCP[i] = length of longest common prefix between SA[i-1] and SA[i] suffixes.");

        System.out.print("\n  Min repeated-substring length to show (e.g. 3): ");
        int minLen;
        try { minLen = Integer.parseInt(readLine()); } catch (Exception e) { minLen = 3; }

        List<String> repeats = LCPKasai.findRepeatedSubstrings(text, sa, lcp, minLen);
        System.out.println("\n  Repeated substrings (length >= " + minLen + "):");
        if (repeats.isEmpty())
            System.out.println("    None found.");
        else
            for (String r : repeats)
                System.out.println("    \"" + r + "\"  (length " + r.length() + ")");
    }

    // ── 4. Suffix Tree ────────────────────────────────────────────────────────

    private void runSuffixTree() {
        System.out.println("\n  == Suffix Tree - Ukkonen's Online O(n) Algorithm ==");
        Flight f = pickFlight("operational note");
        if (f == null) return;

        String text = f.getOperationalNote().toLowerCase();
        System.out.println("  Building Suffix Tree for: \"" + text + "\"");
        SuffixTree st = new SuffixTree(text);
        System.out.println("  Tree built  ->  " + st.nodeCount() + " internal nodes.");

        System.out.print("\n  Enter pattern to search: ");
        String pattern = readLine().toLowerCase();
        List<Integer> pos = st.search(pattern);
        printSearchResult(pattern, pos);

        System.out.println("\n  Suffix Tree properties:");
        System.out.println("    - Built online character-by-character in O(n) time.");
        System.out.println("    - Any substring query answers in O(m + k) where k = # occurrences.");
        System.out.println("    - Supports LCS, longest repeated substring, and more.");
    }

    // ── 5. Suffix Automaton ───────────────────────────────────────────────────

    private void runSuffixAutomaton() {
        System.out.println("\n  == Suffix Automaton (SAM) - O(n) States ==");
        Flight f = pickFlight("alert message");
        if (f == null) return;

        String text = f.getAlertMessage().toLowerCase();
        System.out.println("  Building SAM for: \"" + text + "\"");
        SuffixAutomaton sam = new SuffixAutomaton(text);
        System.out.println("  SAM states: " + sam.stateCount()
                + "  (<= 2n = " + (2 * text.length()) + ")");

        System.out.print("\n  Enter pattern to check / locate: ");
        String pattern = readLine().toLowerCase();

        boolean found = sam.contains(pattern);
        System.out.println("\n  -- SAM Result --");
        System.out.println("  Pattern: \"" + pattern + "\"");
        System.out.println("  Contained in text: " + (found ? "YES" : "NO"));
        if (found) {
            List<Integer> pos = sam.findOccurrences(text, pattern);
            System.out.println("  Occurrence positions: " + pos);
        }
        System.out.println("\n  SAM properties:");
        System.out.println("    - Minimal DFA accepting all suffixes in O(n) states.");
        System.out.println("    - Membership (contains) query in O(m).");
        System.out.println("    - Most space-efficient structure for multi-pattern suffix queries.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Flight pickFlight(String fieldDesc) {
        System.out.println("  Select a flight to analyse its " + fieldDesc + ":");
        List<Flight> fl = dataset.getFlights();
        int show = Math.min(20, fl.size());
        for (int i = 0; i < show; i++)
            System.out.printf("  %2d.  %-8s  %-11s -> %-11s  [%s]%n",
                    i + 1, fl.get(i).getFlightNumber(),
                    fl.get(i).getOrigin(), fl.get(i).getDestination(),
                    fl.get(i).getStatus());
        if (fl.size() > show) {
            System.out.println("  ... (" + (fl.size() - show) + " more flights available in dataset)");
        }
        System.out.print("  Flight index (1-" + fl.size() + "): ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= fl.size()) { System.out.println("  [!] Invalid index."); return null; }
        return fl.get(idx);
    }

    private void printSA(String text, int[] sa, int maxShow) {
        int n    = sa.length;
        int show = Math.min(maxShow, n);
        for (int i = 0; i < show; i++) {
            String suf = text.substring(sa[i], Math.min(sa[i] + 35, n));
            System.out.printf("  SA[%2d] = %3d  ->  \"%s\"%s%n",
                    i, sa[i], suf, (sa[i] + 35 < n ? "..." : ""));
        }
        if (n > show)
            System.out.println("  ... (" + (n - show) + " more entries)");
    }

    private void printSearchResult(String pattern, List<Integer> pos) {
        if (pos.isEmpty())
            System.out.println("  [NOT FOUND]  Pattern \"" + pattern + "\" not found.");
        else
            System.out.println("  [FOUND]  Pattern \"" + pattern + "\" found at position(s): " + pos);
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private String readLine() { return scanner.nextLine().trim(); }
}
