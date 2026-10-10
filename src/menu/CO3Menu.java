package menu;

import co3.BitmaskDP;
import co3.DamerauLevenshtein;
import co3.Levenshtein;
import co3.MatrixChain;
import co3.OptimalBST;
import dataset.AirportDataset;
import dataset.Flight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * CO3 sub-menu: Advanced Dynamic Programming
 *   1. Levenshtein
 *   2. Damerau-Levenshtein
 *   3. Bitmask DP  (gate assignment)
 *   4. Matrix-Chain Multiplication
 *   5. Optimal BST
 */
public class CO3Menu {

    private final Scanner        scanner;
    private final AirportDataset dataset;

    public CO3Menu(Scanner scanner) {
        this.scanner = scanner;
        this.dataset = AirportDataset.getInstance();
    }

    // ── Menu loop ─────────────────────────────────────────────────────────────

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n  ============================================");
            System.out.println("     CO3 - Advanced Dynamic Programming");
            System.out.println("  ============================================");
            System.out.println("    1.  Levenshtein           - Edit-distance autocorrect");
            System.out.println("    2.  Damerau-Levenshtein   - + transposition support");
            System.out.println("    3.  Bitmask DP            - Optimal gate assignment");
            System.out.println("    4.  Matrix-Chain Mult.    - Optimal pipeline order");
            System.out.println("    5.  Optimal BST           - Min expected flight lookup");
            System.out.println("    6.  Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Enter choice: ");

            switch (readInt()) {
                case 1: runLevenshtein();        break;
                case 2: runDamerauLevenshtein(); break;
                case 3: runBitmaskDP();          break;
                case 4: runMatrixChain();        break;
                case 5: runOptimalBST();         break;
                case 6: running = false;         break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ── 1. Levenshtein ────────────────────────────────────────────────────────

    private void runLevenshtein() {
        System.out.println("\n  == Levenshtein Edit Distance - Autocorrect ==");
        System.out.println("  Match against:");
        System.out.println("    1. Flight Numbers");
        System.out.println("    2. City Names (Origins & Destinations)");
        System.out.println("    3. Airline Names");
        System.out.print("  Choose field: ");
        int field = readInt();

        List<String> candidates = extractCandidates(field);
        if (candidates.size() <= 15) {
            System.out.println("  Candidates: " + candidates);
        } else {
            System.out.println("  Candidates loaded: " + candidates.size() + " records (e.g. "
                    + String.join(", ", candidates.subList(0, Math.min(8, candidates.size()))) + ", ...)");
        }

        System.out.print("\n  Enter your query (possibly with typo): ");
        String query = readLine();
        if (query.isEmpty()) { System.out.println("  [!] Empty query."); return; }

        class Match implements Comparable<Match> {
            String name; int dist;
            Match(String n, int d) { name = n; dist = d; }
            public int compareTo(Match o) { return Integer.compare(this.dist, o.dist); }
        }
        List<Match> matches = new ArrayList<>();
        for (String c : candidates) {
            matches.add(new Match(c, Levenshtein.distance(query, c)));
        }
        Collections.sort(matches);

        System.out.println("\n  -- Levenshtein Distances (Top Matches) --");
        System.out.printf("  %-22s  Distance%n", "Candidate");
        System.out.println("  " + "-".repeat(32));

        int limit = Math.min(10, matches.size());
        for (int i = 0; i < limit; i++) {
            System.out.printf("  %-22s  %d%n", matches.get(i).name, matches.get(i).dist);
        }
        System.out.println("  " + "-".repeat(32));
        System.out.printf("  Best match: \"%s\"  (edit distance = %d)%n", matches.get(0).name, matches.get(0).dist);
        System.out.println("  (Levenshtein counts: insertions + deletions + substitutions)");
    }

    // ── 2. Damerau-Levenshtein ────────────────────────────────────────────────

    private void runDamerauLevenshtein() {
        System.out.println("\n  == Damerau-Levenshtein - Edit Distance + Transpositions ==");
        System.out.println("  Also handles swapped adjacent characters, e.g. \"Dlhei\" -> \"Delhi\".");
        System.out.println("  Match against:");
        System.out.println("    1. City Names");
        System.out.println("    2. Airline Names");
        System.out.print("  Choose field: ");
        int field = readInt();

        List<String> candidates = extractCandidates(field == 2 ? 3 : 2);
        if (candidates.size() <= 15) {
            System.out.println("  Candidates: " + candidates);
        } else {
            System.out.println("  Candidates loaded: " + candidates.size() + " records (e.g. "
                    + String.join(", ", candidates.subList(0, Math.min(8, candidates.size()))) + ", ...)");
        }

        System.out.print("\n  Enter query (try a transposition typo, e.g. \"Hyderabd\"): ");
        String query = readLine();
        if (query.isEmpty()) { System.out.println("  [!] Empty query."); return; }

        class DamMatch implements Comparable<DamMatch> {
            String name; int lev, dam;
            DamMatch(String n, int l, int d) { name = n; lev = l; dam = d; }
            public int compareTo(DamMatch o) { return Integer.compare(this.dam, o.dam); }
        }
        List<DamMatch> damMatches = new ArrayList<>();
        for (String c : candidates) {
            damMatches.add(new DamMatch(c, Levenshtein.distance(query, c), DamerauLevenshtein.distance(query, c)));
        }
        Collections.sort(damMatches);

        System.out.println("\n  -- Comparison: Levenshtein vs Damerau-Levenshtein (Top Matches) --");
        System.out.printf("  %-22s  %-12s  %-12s%n", "Candidate", "Levenshtein", "Damerau-Lev");
        System.out.println("  " + "-".repeat(50));

        int limit = Math.min(10, damMatches.size());
        for (int i = 0; i < limit; i++) {
            DamMatch m = damMatches.get(i);
            System.out.printf("  %-22s  %-12d  %-12d%n", m.name, m.lev, m.dam);
        }
        System.out.println("  " + "-".repeat(50));
        System.out.printf("  Best match: \"%s\"  (Damerau distance = %d)%n", damMatches.get(0).name, damMatches.get(0).dam);
    }

    // ── 3. Bitmask DP ─────────────────────────────────────────────────────────

    private void runBitmaskDP() {
        System.out.println("\n  == Bitmask DP - Optimal Gate Assignment ==");

        int n = Math.min(5, dataset.getFlights().size());
        List<Flight> flights = dataset.getFlights().subList(0, n);

        System.out.println("  Assigning " + n + " flights to " + n + " gates (one-to-one).");
        System.out.println("  Cost = runway-gate compatibility (lower = cheaper turnaround).");
        System.out.println();

        System.out.printf("  %-2s  %-8s  %-6s  %-4s%n", "#", "Flight", "Gate", "Runway");
        System.out.println("  " + "-".repeat(26));
        for (int i = 0; i < n; i++)
            System.out.printf("  %-2d  %-8s  %-6s  %-4s%n",
                    i + 1, flights.get(i).getFlightNumber(),
                    flights.get(i).getGate(), flights.get(i).getRunway());

        int[][] cost = BitmaskDP.generateCostMatrix(flights);
        String[] gateLabels = new String[n];
        for (int i = 0; i < n; i++) gateLabels[i] = flights.get(i).getGate();

        System.out.println("\n  Cost Matrix  (row = flight, col = gate):");
        System.out.printf("  %8s", "");
        for (String g : gateLabels) System.out.printf("  %-7s", g);
        System.out.println();
        System.out.println("  " + "-".repeat(10 + n * 9));
        for (int i = 0; i < n; i++) {
            System.out.printf("  %-8s", flights.get(i).getFlightNumber());
            for (int j = 0; j < n; j++) System.out.printf("  %-7d", cost[i][j]);
            System.out.println();
        }

        int   minCost    = BitmaskDP.minCostAssignment(cost, n);
        int[] assignment = BitmaskDP.getAssignment(cost, n);

        System.out.println("\n  -- Optimal Assignment (Bitmask DP over 2^" + n + " = " + (1 << n) + " states) --");
        int total = 0;
        for (int i = 0; i < n; i++) {
            int g = assignment[i];
            System.out.printf("  Flight %-8s -> Gate %-6s  (cost %d)%n",
                    flights.get(i).getFlightNumber(), gateLabels[g], cost[i][g]);
            total += cost[i][g];
        }
        System.out.println("  " + "-".repeat(45));
        System.out.println("  Minimum Total Cost: " + minCost + "  (verified: " + total + ")");
    }

    // ── 4. Matrix-Chain ───────────────────────────────────────────────────────

    private void runMatrixChain() {
        System.out.println("\n  == Matrix-Chain Multiplication - Optimal Pipeline Order ==");
        System.out.println("  Each flight's data is represented as a matrix.");
        System.out.println("  We find the multiplication order that minimises scalar ops.");
        System.out.println();

        int n = Math.min(6, dataset.getFlights().size());
        List<Flight> flights = dataset.getFlights().subList(0, n);

        int[] dims = new int[n + 1];
        dims[0] = 10;
        for (int i = 0; i < n; i++)
            dims[i + 1] = (flights.get(i).getPassengerCount() % 15) + 5;

        System.out.println("  Matrices and their dimensions:");
        for (int i = 0; i < n; i++)
            System.out.printf("  M%-2d  (Flight %-8s)  %3d x %3d%n",
                    i + 1, flights.get(i).getFlightNumber(), dims[i], dims[i + 1]);

        long   minOps = MatrixChain.minMultiplications(dims);
        String order  = MatrixChain.getOptimalOrder(dims, flights);

        System.out.println("\n  -- Result --");
        System.out.println("  Minimum scalar multiplications: " + minOps);
        System.out.println("  Optimal parenthesisation:");
        System.out.println("    " + order);
        System.out.println("\n  (A naive left-to-right order may require far more operations.)");
    }

    // ── 5. Optimal BST ────────────────────────────────────────────────────────

    private void runOptimalBST() {
        System.out.println("\n  == Optimal Binary Search Tree (OBST) ==");
        System.out.println("  Flight records are accessed proportionally to passenger counts.");
        System.out.println("  OBST minimises the expected number of comparisons per lookup.");
        System.out.println();

        int n = Math.min(8, dataset.getFlights().size());
        List<Flight> flights = dataset.getFlights().subList(0, n);

        double totalPax = 0;
        for (Flight f : flights) totalPax += f.getPassengerCount();

        double[] freq = new double[n];
        System.out.printf("  %-8s  %-6s  %-10s%n", "Flight", "Pax", "Frequency");
        System.out.println("  " + "-".repeat(28));
        for (int i = 0; i < n; i++) {
            freq[i] = flights.get(i).getPassengerCount() / totalPax;
            System.out.printf("  %-8s  %-6d  %.4f%n",
                    flights.get(i).getFlightNumber(),
                    flights.get(i).getPassengerCount(), freq[i]);
        }

        double minCost = OptimalBST.minCost(freq, n);
        int[][] roots  = OptimalBST.getRootTable(freq, n);

        System.out.println("\n  -- OBST Result --");
        System.out.printf("  Minimum expected search cost: %.4f%n", minCost);
        System.out.println("  Overall root of the optimal BST: "
                + flights.get(roots[0][n - 1]).getFlightNumber()
                + "  (most central key for this frequency distribution)");
        System.out.println("\n  Root sub-table (row i, col j = root index of sub-tree [i..j]):");
        System.out.print("      ");
        for (int j = 0; j < n; j++) System.out.printf("%-3d", j);
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.printf("  %-4d", i);
            for (int j = 0; j < n; j++) {
                if (j >= i) System.out.printf("%-3d", roots[i][j]);
                else        System.out.printf("%-3s", ".");
            }
            System.out.println();
        }
        System.out.println("  (value = index into flight list above)");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<String> extractCandidates(int field) {
        Set<String> set = new LinkedHashSet<>();
        for (Flight f : dataset.getFlights()) {
            switch (field) {
                case 1: set.add(f.getFlightNumber()); break;
                case 2: set.add(f.getOrigin()); set.add(f.getDestination()); break;
                case 3: set.add(f.getAirline()); break;
                default: set.add(f.getFlightNumber());
            }
        }
        return new ArrayList<>(set);
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private String readLine() { return scanner.nextLine().trim(); }
}
