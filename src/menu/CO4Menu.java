package menu;

import co4.BipartiteMatching;
import co4.Dinic;
import co4.FordFulkerson;
import dataset.AirportDataset;
import dataset.Flight;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * CO4 sub-menu: Network Flow Algorithms
 *   1. Ford-Fulkerson (Edmonds-Karp)
 *   2. Dinic's Algorithm
 *   3. Max-Flow / Min-Cut Duality
 *   4. Bipartite Matching - Flight to Gate
 */
public class CO4Menu {

    private final Scanner        scanner;
    private final AirportDataset dataset;

    // Fixed airport topology used across all CO4 demos
    private static final String[] GATES   = {"A1", "B2", "C3", "D4", "A5"};
    private static final String[] RUNWAYS = {"R1", "R2", "R3"};
    // compat[gate][runway] = true if compatible
    private static final boolean[][] COMPAT = {
        {true,  false, true },   // A1 -> R1, R3
        {false, true,  true },   // B2 -> R2, R3
        {true,  true,  false},   // C3 -> R1, R2
        {false, true,  true },   // D4 -> R2, R3
        {true,  false, false},   // A5 -> R1
    };

    public CO4Menu(Scanner scanner) {
        this.scanner = scanner;
        this.dataset = AirportDataset.getInstance();
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n  ============================================");
            System.out.println("       CO4 - Network Flow Module");
            System.out.println("  ============================================");
            System.out.println("    1.  Ford-Fulkerson (Edmonds-Karp) - Max Flow");
            System.out.println("    2.  Dinic's Algorithm - Max Flow");
            System.out.println("    3.  Max-Flow / Min-Cut Duality");
            System.out.println("    4.  Bipartite Matching - Flight to Gate Assignment");
            System.out.println("    5.  Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Enter choice: ");

            switch (readInt()) {
                case 1: runFordFulkerson();      break;
                case 2: runDinic();              break;
                case 3: runMaxFlowMinCut();      break;
                case 4: runBipartiteMatching();  break;
                case 5: running = false;         break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ── 1. Ford-Fulkerson (Edmonds-Karp) ─────────────────────────────────────

    private void runFordFulkerson() {
        System.out.println("\n  == Ford-Fulkerson with Edmonds-Karp (BFS) ==");
        System.out.println("  Airport flow network:  Source -> Gates -> Runways -> Sink");
        System.out.println("  Each edge: (from -> to, capacity)");
        System.out.println();

        int G = GATES.length, R = RUNWAYS.length;
        int source = 0, sink = G + R + 1;
        FordFulkerson ff = new FordFulkerson(G + R + 2);

        System.out.println("  Edges added:");
        for (int i = 1; i <= G; i++) {
            ff.addEdge(source, i, 1);
            System.out.printf("    Source     -> Gate %-4s  (cap 1)%n", GATES[i - 1]);
        }
        for (int i = 0; i < G; i++)
            for (int j = 0; j < R; j++)
                if (COMPAT[i][j]) {
                    ff.addEdge(i + 1, G + 1 + j, 1);
                    System.out.printf("    Gate %-4s  -> Runway %-3s  (cap 1)%n", GATES[i], RUNWAYS[j]);
                }
        for (int j = 0; j < R; j++) {
            ff.addEdge(G + 1 + j, sink, 2);
            System.out.printf("    Runway %-3s -> Sink        (cap 2)%n", RUNWAYS[j]);
        }

        int maxFlow = ff.maxFlow(source, sink);

        System.out.println("\n  -- Result --");
        System.out.println("  Max flights that can simultaneously depart: " + maxFlow);
        System.out.println("  Each augmenting path found via BFS (shortest path first).");
        System.out.println("  Time complexity: O(V * E^2)");
    }

    // ── 2. Dinic's Algorithm ─────────────────────────────────────────────────

    private void runDinic() {
        System.out.println("\n  == Dinic's Algorithm - Layered Graph + Blocking Flow ==");
        System.out.println("  Phase 1: BFS builds level graph (assigns depth to each node).");
        System.out.println("  Phase 2: DFS pushes blocking flow through level graph.");
        System.out.println("  Repeat until sink unreachable.");
        System.out.println();

        int G = GATES.length, R = RUNWAYS.length;
        int source = 0, sink = G + R + 1;
        Dinic dinic = new Dinic(G + R + 2);

        for (int i = 1; i <= G; i++) dinic.addEdge(source, i, 1);
        for (int i = 0; i < G; i++)
            for (int j = 0; j < R; j++)
                if (COMPAT[i][j]) dinic.addEdge(i + 1, G + 1 + j, 1);
        for (int j = 0; j < R; j++) dinic.addEdge(G + 1 + j, sink, 3);

        int maxFlow = dinic.maxFlow(source, sink);
        int[] levels = dinic.getLevels();

        System.out.println("  Level graph after last BFS (level = -1 means saturated/cut):");
        System.out.printf("  %-20s  Level%n", "Node");
        System.out.println("  " + "-".repeat(28));
        System.out.printf("  %-20s  %d%n", "Source", levels[0]);
        for (int i = 1; i <= G; i++)
            System.out.printf("  Gate %-16s  %d%n", GATES[i - 1], levels[i]);
        for (int j = 0; j < R; j++)
            System.out.printf("  Runway %-14s  %d%n", RUNWAYS[j], levels[G + 1 + j]);
        System.out.printf("  %-20s  %d%n", "Sink", levels[sink]);

        System.out.println("\n  -- Result --");
        System.out.println("  Max Flow (Dinic): " + maxFlow);
        System.out.println("  Complexity: O(V^2 * E) general, O(E * sqrt(V)) unit-cap");
        System.out.println("  Dinic is faster than Edmonds-Karp for dense networks.");
    }

    // ── 3. Max-Flow / Min-Cut Duality ────────────────────────────────────────

    private void runMaxFlowMinCut() {
        System.out.println("\n  == Max-Flow / Min-Cut Duality ==");
        System.out.println("  Theorem (Ford-Fulkerson, 1956):");
        System.out.println("    max_flow(S, T) = min_cut_capacity(S, T)");
        System.out.println("  The minimum cut is the bottleneck of the network.");
        System.out.println();

        int G = 3, R = 2;
        int source = 0, sink = G + R + 1;
        FordFulkerson ff = new FordFulkerson(G + R + 2);

        String[] g = {"A1", "B2", "C3"};
        String[] r = {"R1", "R2"};
        boolean[][] c = {{true, true}, {true, false}, {false, true}};

        for (int i = 1; i <= G; i++) ff.addEdge(source, i, 2);
        for (int i = 0; i < G; i++)
            for (int j = 0; j < R; j++)
                if (c[i][j]) ff.addEdge(i + 1, G + 1 + j, 1);
        for (int j = 0; j < R; j++) ff.addEdge(G + 1 + j, sink, 3);

        int maxFlow = ff.maxFlow(source, sink);
        List<int[]> cuts = ff.minCut(source);

        System.out.println("  Min-Cut edges (saturated bottleneck links):");
        int cutCap = 0;
        for (int[] cut : cuts) {
            String uLabel = cut[0] == 0 ? "Source"
                          : cut[0] <= G ? "Gate " + g[cut[0] - 1]
                          : "Runway " + r[cut[0] - G - 1];
            String vLabel = cut[1] == sink ? "Sink"
                          : cut[1] <= G ? "Gate " + g[cut[1] - 1]
                          : "Runway " + r[cut[1] - G - 1];
            System.out.printf("    %-15s -> %-15s  (cap %d)%n", uLabel, vLabel, cut[2]);
            cutCap += cut[2];
        }

        System.out.println("\n  -- Duality Verification --");
        System.out.println("  Max Flow Value  = " + maxFlow);
        System.out.println("  Min Cut Capacity = " + cutCap);
        System.out.println("  Duality holds: " + (maxFlow == cutCap ? "YES" : "NO"));
        System.out.println("\n  Removing the min-cut edges disconnects source from sink.");
        System.out.println("  These edges are the true capacity bottleneck of the airport network.");
    }

    // ── 4. Bipartite Matching ─────────────────────────────────────────────────

    private void runBipartiteMatching() {
        System.out.println("\n  == Bipartite Matching - Flight to Gate Assignment ==");
        System.out.println("  Left = Flights,  Right = Gates");
        System.out.println("  Edge = flight is compatible with that gate (airline-based rule)");
        System.out.println();

        List<Flight> flights = dataset.getFlights().subList(0, Math.min(8, dataset.getFlights().size()));
        int F = flights.size(), G = GATES.length;

        BipartiteMatching bm = new BipartiteMatching(F, G);

        System.out.printf("  %-10s  %-14s  Compatible Gates%n", "Flight", "Airline");
        System.out.println("  " + "-".repeat(50));
        for (int i = 0; i < F; i++) {
            Flight f      = flights.get(i);
            String airline = f.getAirline();
            StringBuilder compat = new StringBuilder();
            for (int j = 0; j < G; j++) {
                char gp = GATES[j].charAt(0);
                boolean ok = (airline.equals("Air India") && (gp == 'A' || gp == 'B'))
                          || (airline.equals("IndiGo")    && (gp == 'B' || gp == 'C'))
                          || (airline.equals("SpiceJet")  && (gp == 'C' || gp == 'D'))
                          || (airline.equals("Vistara")   && (gp == 'D' || gp == 'A'));
                if (ok) { bm.addEdge(i, j); compat.append(GATES[j]).append(" "); }
            }
            System.out.printf("  %-10s  %-14s  %s%n", f.getFlightNumber(), airline, compat);
        }

        int matched = bm.maxMatching();
        int[] matchL = bm.getMatchL();

        System.out.println("\n  -- Matching Result --");
        System.out.printf("  Flights matched: %d / %d%n", matched, F);
        System.out.println();
        for (int i = 0; i < F; i++) {
            String gate = (matchL[i] == -1) ? "UNASSIGNED" : GATES[matchL[i]];
            System.out.printf("  Flight %-8s -> Gate %s%n",
                    flights.get(i).getFlightNumber(), gate);
        }
        System.out.println("\n  This is equivalent to max-flow on a unit-capacity bipartite graph.");
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
