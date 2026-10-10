package menu;

import co5.IndependentSet;
import co5.SetCover;
import co5.TSPApprox;
import co5.VertexCover;
import dataset.AirportDataset;
import dataset.Flight;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * CO5 sub-menu: NP-Completeness & Approximation Algorithms
 *   1. Vertex Cover (2-approximation)
 *   2. Independent Set (complement of VC)
 *   3. Set Cover (greedy, ln(n)-approximation)
 *   4. TSP Nearest-Neighbour Heuristic
 */
public class CO5Menu {

    private final Scanner        scanner;
    private final AirportDataset dataset;

    public CO5Menu(Scanner scanner) {
        this.scanner = scanner;
        this.dataset = AirportDataset.getInstance();
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n  ============================================");
            System.out.println("     CO5 - NP-Completeness & Approximations");
            System.out.println("  ============================================");
            System.out.println("    1.  Vertex Cover      - 2-approximation");
            System.out.println("    2.  Independent Set   - derived from VC");
            System.out.println("    3.  Set Cover         - greedy ln(n)-approx");
            System.out.println("    4.  TSP               - Nearest-Neighbour heuristic");
            System.out.println("    5.  Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Enter choice: ");

            switch (readInt()) {
                case 1: runVertexCover();    break;
                case 2: runIndependentSet(); break;
                case 3: runSetCover();       break;
                case 4: runTSP();            break;
                case 5: running = false;     break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ── 1. Vertex Cover ──────────────────────────────────────────────────────

    private void runVertexCover() {
        System.out.println("\n  == Vertex Cover - 2-Approximation ==");
        System.out.println("  NP-completeness chain:  3-SAT -> Vertex Cover -> Independent Set");
        System.out.println("  Algorithm: Greedy Maximal Matching (pick both endpoints of each uncovered edge)");
        System.out.println("  Approximation ratio: 2  (cover size <= 2 * OPT)");
        System.out.println();

        // Build route graph: airports as nodes, flight routes as edges
        List<Flight> flights = dataset.getFlights();
        // Collect unique cities as node indices
        java.util.Map<String, Integer> cityIdx = new java.util.LinkedHashMap<>();
        for (Flight f : flights) {
            cityIdx.putIfAbsent(f.getOrigin(),      cityIdx.size());
            cityIdx.putIfAbsent(f.getDestination(), cityIdx.size());
        }
        String[] cities = cityIdx.keySet().toArray(new String[0]);
        int n = cities.length;

        VertexCover vc = new VertexCover(n);
        System.out.println("  Graph: Airport route network");
        System.out.printf("  %-4s  %-12s  %-12s%n", "Edge", "Origin", "Dest");
        System.out.println("  " + "-".repeat(32));
        int eCount = 0;
        for (Flight f : flights) {
            int u = cityIdx.get(f.getOrigin()), v = cityIdx.get(f.getDestination());
            vc.addEdge(u, v);
            if (eCount < 10) // show first 10 edges
                System.out.printf("  %-4d  %-12s  %-12s%n", eCount + 1, f.getOrigin(), f.getDestination());
            eCount++;
        }
        if (eCount > 10) System.out.println("  ... (" + (eCount - 10) + " more edges)");

        Set<Integer> cover = vc.approx2();
        System.out.println("\n  -- 2-Approximation Result --");
        System.out.println("  Cover size: " + cover.size() + "  (monitoring airports)");
        System.out.print("  Cover nodes: ");
        for (int idx : cover) System.out.print(cities[idx] + "  ");
        System.out.println();
        System.out.println("  Valid cover: " + vc.verify(cover));
        System.out.println("\n  Every flight route has at least one endpoint airport in the cover.");
        System.out.println("  Placing inspectors at these airports monitors ALL routes.");
    }

    // ── 2. Independent Set ────────────────────────────────────────────────────

    private void runIndependentSet() {
        System.out.println("\n  == Independent Set (from Vertex Cover complement) ==");
        System.out.println("  Relationship: IS = V \\ VC  (every IS complement is a VC)");
        System.out.println("  Max IS is NP-Hard (Karp, 1972) — reduction from 3-SAT via VC.");
        System.out.println();

        java.util.Map<String, Integer> cityIdx = new java.util.LinkedHashMap<>();
        for (Flight f : dataset.getFlights()) {
            cityIdx.putIfAbsent(f.getOrigin(),      cityIdx.size());
            cityIdx.putIfAbsent(f.getDestination(), cityIdx.size());
        }
        String[] cities = cityIdx.keySet().toArray(new String[0]);
        int n = cities.length;

        VertexCover vc = new VertexCover(n);
        for (Flight f : dataset.getFlights())
            vc.addEdge(cityIdx.get(f.getOrigin()), cityIdx.get(f.getDestination()));

        Set<Integer> cover = vc.approx2();
        Set<Integer> is    = IndependentSet.fromVertexCover(n, cover);

        System.out.println("  Vertex Cover size:    " + cover.size());
        System.out.println("  Independent Set size: " + is.size());
        System.out.print("  Independent airports: ");
        for (int idx : is) System.out.print(cities[idx] + "  ");
        System.out.println();
        System.out.println("  Valid independent set: " + IndependentSet.verify(is, vc.getEdges()));
        System.out.println("\n  These airports have NO direct routes between them.");
        System.out.println("  Useful for assigning independent scheduling zones.");
    }

    // ── 3. Set Cover ─────────────────────────────────────────────────────────

    private void runSetCover() {
        System.out.println("\n  == Set Cover - Greedy H(n)-Approximation ==");
        System.out.println("  Approximation ratio: H(n) = 1 + 1/2 + ... + 1/n ≈ ln(n) + 1");
        System.out.println("  This is TIGHT - no polynomial algorithm does better unless P=NP.");
        System.out.println();

        List<Flight> flights = dataset.getFlights();
        int F = flights.size();

        // Universe = flight indices 0..F-1
        // Sets = maintenance crews, each covers flights of one airline
        SetCover sc = new SetCover(F);

        Set<String> uniqueAirlines = new java.util.LinkedHashSet<>();
        for (Flight f : flights) uniqueAirlines.add(f.getAirline());

        System.out.println("  Universe: " + F + " flights need maintenance.");
        System.out.println("  Each maintenance crew covers flights of one airline:");
        System.out.println();

        for (String airline : uniqueAirlines) {
            Set<Integer> covered = new HashSet<>();
            for (int i = 0; i < F; i++)
                if (flights.get(i).getAirline().equals(airline)) covered.add(i);
            sc.addSet("Crew-" + airline.replace(" ", ""), covered);
            System.out.printf("  %-25s covers %d flights%n",
                    "Crew-" + airline, covered.size());
        }
        // Add a cross-airline crew as extra option
        Set<Integer> extras = new HashSet<>();
        for (int i = 0; i < F; i++) if (flights.get(i).getPassengerCount() > 170) extras.add(i);
        sc.addSet("Crew-HighPax", extras);
        System.out.printf("  %-25s covers %d flights (high pax)%n", "Crew-HighPax", extras.size());

        List<Integer> chosen = sc.greedyCover();

        System.out.println("\n  -- Greedy Set Cover Result --");
        System.out.println("  Minimum crews selected (greedy order):");
        int totalCovered = 0;
        Set<Integer> seen = new HashSet<>();
        for (int idx : chosen) {
            Set<Integer> s = sc.getSet(idx);
            int newCovered = 0;
            for (int e : s) if (!seen.contains(e)) { seen.add(e); newCovered++; }
            totalCovered += newCovered;
            System.out.printf("    %d. %-28s covers %d new flights (total now %d)%n",
                    chosen.indexOf(idx) + 1, sc.getSetName(idx), newCovered, totalCovered);
        }
        System.out.println("  Total crews needed: " + chosen.size() + " / " + sc.getSetCount());
    }

    // ── 4. TSP ────────────────────────────────────────────────────────────────

    private void runTSP() {
        System.out.println("\n  == TSP - Nearest-Neighbour Heuristic ==");
        System.out.println("  Ground crew must visit all gates for pre-flight checks.");
        System.out.println("  TSP finds an approximate minimum-distance tour.");
        System.out.println("  Metric TSP best known: Christofides 3/2-approximation.");
        System.out.println();

        // Use gates from first N flights as TSP nodes
        List<Flight> flights = dataset.getFlights();
        int n = Math.min(8, flights.size());
        String[] nodeLabels = new String[n];
        for (int i = 0; i < n; i++)
            nodeLabels[i] = flights.get(i).getGate();

        // Generate Euclidean distance matrix (reproducible seed)
        int[][] dist = TSPApprox.generateDistMatrix(n, 100);

        System.out.println("  Gates (TSP nodes): " + Arrays.toString(nodeLabels));
        System.out.println();
        System.out.println("  Distance matrix (metres between gates):");
        System.out.printf("  %8s", "");
        for (String g : nodeLabels) System.out.printf("  %-5s", g);
        System.out.println();
        System.out.println("  " + "-".repeat(10 + n * 7));
        for (int i = 0; i < n; i++) {
            System.out.printf("  %-8s", nodeLabels[i]);
            for (int j = 0; j < n; j++) System.out.printf("  %-5d", dist[i][j]);
            System.out.println();
        }

        List<Integer> tour = TSPApprox.nearestNeighbour(dist);
        int cost = TSPApprox.tourCost(tour, dist);

        System.out.println("\n  -- Nearest-Neighbour Tour --");
        System.out.print("  Route: ");
        for (int i = 0; i < tour.size(); i++) {
            System.out.print(nodeLabels[tour.get(i)]);
            if (i < tour.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
        System.out.println("  Total distance: " + cost + " metres");
        System.out.println("  (Optimal tour unknown without exhaustive search for n > 20)");
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
