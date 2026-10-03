package menu;

import co1.AhoCorasick;
import co1.KMP;
import co1.RabinKarp;
import co1.ZFunction;
import dataset.AirportDataset;
import dataset.Flight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * CO1 sub-menu: String Algorithms
 *   1. KMP
 *   2. Z-Function
 *   3. Rabin-Karp
 *   4. Aho-Corasick
 */
public class CO1Menu {

    private final Scanner        scanner;
    private final AirportDataset dataset;

    public CO1Menu(Scanner scanner) {
        this.scanner = scanner;
        this.dataset = AirportDataset.getInstance();
    }

    // ── Menu loop ─────────────────────────────────────────────────────────────

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n  ============================================");
            System.out.println("       CO1 - String Algorithms Module");
            System.out.println("  ============================================");
            System.out.println("    1.  KMP           - Knuth-Morris-Pratt search");
            System.out.println("    2.  Z-Function    - Repeated pattern detection");
            System.out.println("    3.  Rabin-Karp    - Rolling-hash search");
            System.out.println("    4.  Aho-Corasick  - Multi-pattern detection");
            System.out.println("    5.  Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Enter choice: ");

            switch (readInt()) {
                case 1: runKMP();         break;
                case 2: runZFunction();   break;
                case 3: runRabinKarp();   break;
                case 4: runAhoCorasick(); break;
                case 5: running = false;  break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ── 1. KMP ────────────────────────────────────────────────────────────────

    private void runKMP() {
        System.out.println("\n  == KMP - Knuth-Morris-Pratt Search ==");
        System.out.println("  Search field:");
        System.out.println("    1. Alert Messages");
        System.out.println("    2. Operational Notes");
        System.out.println("    3. All Flight Numbers (concatenated)");
        System.out.print("  Choose field: ");
        int field = readInt();

        List<String> texts  = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        switch (field) {
            case 1:
                for (Flight f : dataset.getFlights()) {
                    texts.add(f.getAlertMessage());
                    labels.add("Flight " + f.getFlightNumber());
                }
                break;
            case 2:
                for (Flight f : dataset.getFlights()) {
                    texts.add(f.getOperationalNote());
                    labels.add("Flight " + f.getFlightNumber());
                }
                break;
            default:
                texts.add(String.join(" | ", dataset.getFlightNumbers()));
                labels.add("All Flight Numbers");
        }

        System.out.print("  Enter search pattern: ");
        String pattern = readLine();
        if (pattern.isEmpty()) { System.out.println("  [!] Empty pattern."); return; }

        System.out.println("\n  -- KMP Results (O(n+m) time) --");
        System.out.println("  Pattern: \"" + pattern + "\"");
        System.out.println("  Failure function: " + Arrays.toString(KMP.buildFailure(pattern)));
        System.out.println();

        boolean anyFound = false;
        for (int i = 0; i < texts.size(); i++) {
            List<Integer> pos = KMP.search(texts.get(i), pattern);
            if (!pos.isEmpty()) {
                System.out.printf("  [FOUND]  %-35s -> positions %s%n", labels.get(i), pos);
                anyFound = true;
            }
        }
        if (!anyFound)
            System.out.println("  [NOT FOUND]  Pattern \"" + pattern + "\" not found in any record.");
    }

    // ── 2. Z-Function ─────────────────────────────────────────────────────────

    private void runZFunction() {
        System.out.println("\n  == Z-Function - Repeated Pattern Detection ==");
        System.out.println("  Select a flight to analyse its Operational Note:");
        printFlightList();
        System.out.print("  Flight index (1-" + dataset.getFlights().size() + "): ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= dataset.getFlights().size()) { System.out.println("  [!] Invalid index."); return; }

        Flight f    = dataset.getFlights().get(idx);
        String text = f.getOperationalNote();

        System.out.print("  Enter pattern to search (will be prefixed with '$' separator): ");
        String pattern = readLine();

        String combined = pattern + "$" + text;
        int[] z         = ZFunction.compute(combined);

        System.out.println("\n  -- Z-Function Results --");
        System.out.println("  Flight:  " + f.getFlightNumber() + " - " + f.getOrigin() + " -> " + f.getDestination());
        System.out.println("  Text:    \"" + text + "\"");
        System.out.println("  Pattern: \"" + pattern + "\"");
        System.out.println("  Combined string: pattern + \"$\" + text  (length = " + combined.length() + ")");
        System.out.println();

        int pm = pattern.length();
        List<Integer> matches = new ArrayList<>();
        for (int i = pm + 1; i < combined.length(); i++) {
            if (z[i] >= pm) matches.add(i - pm - 1);
        }

        if (matches.isEmpty())
            System.out.println("  Pattern not found in text.");
        else
            System.out.println("  Pattern occurrences (0-based in text): " + matches);

        int[] zText = ZFunction.compute(text);
        System.out.println("\n  Z-array of text (first 20 values):");
        System.out.println("  " + Arrays.toString(Arrays.copyOf(zText, Math.min(zText.length, 20))));
        System.out.println("  Z[i] = length of longest prefix of text that matches text[i..]");

        System.out.println("\n  Positions where a prefix repeats (Z[i] >= 3):");
        boolean any = false;
        for (int i = 1; i < zText.length; i++) {
            if (zText[i] >= 3) {
                System.out.printf("    i=%-3d  Z=%-3d  substring=\"%s\"%n",
                        i, zText[i], text.substring(i, i + zText[i]));
                any = true;
            }
        }
        if (!any) System.out.println("  None (no prefix of length >= 3 repeats).");
    }

    // ── 3. Rabin-Karp ─────────────────────────────────────────────────────────

    private void runRabinKarp() {
        System.out.println("\n  == Rabin-Karp - Rolling-Hash Search ==");
        System.out.println("  Search in:");
        System.out.println("    1. Baggage Codes");
        System.out.println("    2. Alert Messages");
        System.out.println("    3. Operational Notes");
        System.out.print("  Choose field: ");
        int field = readInt();

        List<String> texts  = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Flight f : dataset.getFlights()) {
            String text = (field == 1) ? f.getBaggageCode()
                        : (field == 2) ? f.getAlertMessage()
                        : f.getOperationalNote();
            texts.add(text);
            labels.add("Flight " + f.getFlightNumber());
        }

        System.out.print("  Enter search pattern: ");
        String pattern = readLine();
        if (pattern.isEmpty()) { System.out.println("  [!] Empty pattern."); return; }

        System.out.println("\n  -- Rabin-Karp Results (BASE=31, MOD=1e9+7) --");
        System.out.println("  Pattern: \"" + pattern + "\"");
        System.out.println();

        boolean anyFound = false;
        for (int i = 0; i < texts.size(); i++) {
            List<Integer> pos = RabinKarp.search(texts.get(i), pattern);
            if (!pos.isEmpty()) {
                System.out.printf("  [FOUND]  %-35s -> positions %s%n", labels.get(i), pos);
                anyFound = true;
            }
        }
        if (!anyFound)
            System.out.println("  [NOT FOUND]  Pattern \"" + pattern + "\" not found.");
    }

    // ── 4. Aho-Corasick ───────────────────────────────────────────────────────

    private void runAhoCorasick() {
        System.out.println("\n  == Aho-Corasick - Multi-Pattern Detection ==");
        System.out.println("  Searching all alert messages simultaneously for status keywords.");
        System.out.println("\n  Default patterns: ON-TIME, DELAYED, CANCELLED, BOARDING, delayed, cancelled, Flight");
        System.out.print("  Add extra pattern? (leave blank to skip): ");
        String extra = readLine();

        List<String> patterns = new ArrayList<>(
                Arrays.asList("ON-TIME", "DELAYED", "CANCELLED", "BOARDING", "delayed", "cancelled", "Flight"));
        if (!extra.isEmpty()) { patterns.add(extra); System.out.println("  [+] Added: \"" + extra + "\""); }

        AhoCorasick ac = new AhoCorasick();
        for (String p : patterns) ac.addPattern(p);
        ac.build();

        System.out.println("\n  Patterns: " + patterns);
        System.out.println("\n  -- Search Results --");
        System.out.println();

        for (Flight f : dataset.getFlights()) {
            Map<String, List<Integer>> hits = ac.search(f.getAlertMessage());
            if (!hits.isEmpty()) {
                System.out.printf("  Flight %-8s  Alert: %s%n",
                        f.getFlightNumber(), truncate(f.getAlertMessage(), 55));
                for (Map.Entry<String, List<Integer>> e : hits.entrySet()) {
                    System.out.printf("    -> %-14s  at positions %s%n",
                            "\"" + e.getKey() + "\"", e.getValue());
                }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void printFlightList() {
        List<Flight> fl = dataset.getFlights();
        for (int i = 0; i < fl.size(); i++)
            System.out.printf("  %2d.  %-8s  %-12s -> %-12s  [%s]%n",
                    i + 1, fl.get(i).getFlightNumber(),
                    fl.get(i).getOrigin(), fl.get(i).getDestination(),
                    fl.get(i).getStatus());
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private String readLine() { return scanner.nextLine().trim(); }
}
