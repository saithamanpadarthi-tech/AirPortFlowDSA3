package menu;

import co6.LasVegas;
import co6.MonteCarloSimulation;
import co6.ParallelPrefixSum;
import co6.ParallelReduce;
import dataset.AirportDataset;
import dataset.Flight;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * CO6 sub-menu: Randomized & Parallel Algorithms
 *   1. Monte Carlo Simulation
 *   2. Las Vegas (Randomized QuickSort / QuickSelect)
 *   3. Parallel Prefix Sum
 *   4. Parallel Reduce (Sum / Max / Min)
 */
public class CO6Menu {

    private final Scanner        scanner;
    private final AirportDataset dataset;

    public CO6Menu(Scanner scanner) {
        this.scanner = scanner;
        this.dataset = AirportDataset.getInstance();
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n  ============================================");
            System.out.println("     CO6 - Randomized & Parallel Algorithms");
            System.out.println("  ============================================");
            System.out.println("    1.  Monte Carlo Simulation  - delay/throughput estimation");
            System.out.println("    2.  Las Vegas               - Randomized QuickSort / QuickSelect");
            System.out.println("    3.  Parallel Prefix Sum     - cumulative passenger count");
            System.out.println("    4.  Parallel Reduce         - sum / max / min in parallel");
            System.out.println("    5.  Back to Main Menu");
            System.out.println("  --------------------------------------------");
            System.out.print("  Enter choice: ");

            switch (readInt()) {
                case 1: runMonteCarlo();        break;
                case 2: runLasVegas();          break;
                case 3: runParallelPrefixSum(); break;
                case 4: runParallelReduce();    break;
                case 5: running = false;        break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ── 1. Monte Carlo ────────────────────────────────────────────────────────

    private void runMonteCarlo() {
        System.out.println("\n  == Monte Carlo Simulation ==");
        System.out.println("  Type: always terminates, correct WITH HIGH PROBABILITY.");
        System.out.println("  More trials => lower error probability.");
        System.out.println();
        System.out.println("  Select simulation:");
        System.out.println("    1. Delay probability per flight (from dataset)");
        System.out.println("    2. Expected passenger wait time");
        System.out.println("    3. Airport throughput estimation");
        System.out.print("  Choose: ");
        int sub = readInt();

        MonteCarloSimulation mc = new MonteCarloSimulation(42);

        if (sub == 1) {
            System.out.print("  Number of trials (e.g. 10000): ");
            int trials = readIntDefault(10000);
            List<Flight> flights = dataset.getFlights();
            double[] probs = mc.simulateFleetDelays(flights, trials);

            System.out.println("\n  -- Delay Probability per Flight (" + trials + " trials each) --");
            System.out.printf("  %-8s  %-10s  %-10s  %s%n", "Flight", "Status", "DelayProb", "Bar");
            System.out.println("  " + "-".repeat(55));
            int show = Math.min(20, flights.size());
            double totalProb = 0;
            int highRiskCount = 0;
            for (int i = 0; i < flights.size(); i++) {
                totalProb += probs[i];
                if (probs[i] >= 0.5) highRiskCount++;
                if (i < show) {
                    Flight f = flights.get(i);
                    int bar = (int)(probs[i] * 20);
                    System.out.printf("  %-8s  %-10s  %-10.3f  %s%n",
                            f.getFlightNumber(), f.getStatus(), probs[i],
                            "#".repeat(bar));
                }
            }
            if (flights.size() > show) {
                System.out.println("  ... (" + (flights.size() - show) + " more flights simulated in fleet; showing first " + show + ")");
            }
            System.out.println("  " + "-".repeat(55));
            System.out.printf("  Fleet-wide average delay probability: %.2f%%%n", (totalProb / flights.size()) * 100);
            System.out.printf("  High-delay risk flights (>= 50%%):    %d of %d flights%n", highRiskCount, flights.size());

        } else if (sub == 2) {
            System.out.print("  Average delay in minutes (e.g. 25): ");
            double avg = readDoubleDefault(25.0);
            System.out.print("  Number of trials (e.g. 100000): ");
            int trials = readIntDefault(100000);
            double expected = mc.estimateExpectedWait(trials, avg);
            System.out.println("\n  -- Expected Wait Time --");
            System.out.printf("  Input mean delay:          %.1f min%n", avg);
            System.out.printf("  Monte Carlo estimate:      %.2f min%n", expected);
            System.out.printf("  Theoretical (Exp dist):    %.2f min%n", avg);
            System.out.printf("  Error:                     %.4f min%n", Math.abs(expected - avg));
            System.out.println("  (Error shrinks as 1/sqrt(trials))");

        } else {
            System.out.print("  Number of runways (e.g. 3): ");
            int runways = readIntDefault(3);
            System.out.print("  Average turnaround time in minutes (e.g. 20): ");
            double avg = readDoubleDefault(20.0);
            System.out.print("  Trials (e.g. 5000): ");
            int trials = readIntDefault(5000);
            double throughput = mc.estimateThroughput(trials, runways, avg);
            System.out.println("\n  -- Throughput Estimation --");
            System.out.printf("  Runways:            %d%n", runways);
            System.out.printf("  Avg turnaround:     %.1f min%n", avg);
            System.out.printf("  Estimated flights/hour: %.2f%n", throughput);
        }
    }

    // ── 2. Las Vegas ─────────────────────────────────────────────────────────

    private void runLasVegas() {
        System.out.println("\n  == Las Vegas - Randomized QuickSort / QuickSelect ==");
        System.out.println("  Type: ALWAYS correct, runtime is a random variable.");
        System.out.println("  Expected O(n log n) for QuickSort, O(n) for QuickSelect.");
        System.out.println("  Random pivot avoids worst-case O(n^2) on adversarial input.");
        System.out.println();
        System.out.println("  Select operation:");
        System.out.println("    1. Sort flights by passenger count (QuickSort)");
        System.out.println("    2. Sort flights by departure time (QuickSort)");
        System.out.println("    3. Find k-th smallest passenger count (QuickSelect)");
        System.out.print("  Choose: ");
        int sub = readInt();

        LasVegas lv = new LasVegas(42);
        List<Flight> flights = dataset.getFlights();

        if (sub == 1) {
            int[] pax = new int[flights.size()];
            for (int i = 0; i < pax.length; i++) pax[i] = flights.get(i).getPassengerCount();
            System.out.println("\n  Before sort (passenger counts): " + Arrays.toString(pax));
            lv.randomQuickSort(pax, 0, pax.length - 1);
            System.out.println("  After sort  (ascending):        " + Arrays.toString(pax));
            System.out.println("  Random pivot ensures O(n log n) expected, avoids sorted-input pitfall.");

        } else if (sub == 2) {
            List<Flight> sorted = lv.sortFlightsByDeparture(flights);
            System.out.println("\n  Flights sorted by departure time (Randomized QuickSort):");
            System.out.printf("  %-8s  %-8s  %-10s%n", "Flight", "Departs", "Status");
            System.out.println("  " + "-".repeat(32));
            for (Flight f : sorted)
                System.out.printf("  %-8s  %-8s  %-10s%n",
                        f.getFlightNumber(), f.getDepartureTime(), f.getStatus());

        } else {
            System.out.print("  Enter k (1-based rank, e.g. 1=minimum, " + flights.size() + "=maximum): ");
            int k = readIntDefault(1) - 1;
            k = Math.max(0, Math.min(k, flights.size() - 1));
            int[] pax = new int[flights.size()];
            for (int i = 0; i < pax.length; i++) pax[i] = flights.get(i).getPassengerCount();
            int result = lv.quickSelect(pax, 0, pax.length - 1, k);
            System.out.println("\n  All passenger counts: " + Arrays.toString(
                    flights.stream().mapToInt(Flight::getPassengerCount).toArray()));
            System.out.println("  Rank " + (k + 1) + " (k=" + k + ") smallest pax count: " + result);
            System.out.println("  QuickSelect finds this in O(n) expected time using random pivot.");
        }
    }

    // ── 3. Parallel Prefix Sum ────────────────────────────────────────────────

    private void runParallelPrefixSum() {
        System.out.println("\n  == Parallel Prefix Sum (Scan) ==");
        System.out.println("  Sequential: O(n) time,   O(1) extra space");
        System.out.println("  Parallel:   O(log n) span, O(n) work  (ForkJoin binary tree)");
        System.out.println("  Parallelism: W/S = O(n / log n)");
        System.out.println("  Brent's theorem: T_p <= W/p + S");
        System.out.println();

        List<Flight> flights = dataset.getFlights();
        int[] pax = ParallelPrefixSum.fromFlights(flights);

        System.out.println("  Input (passenger counts per flight):");
        System.out.println("  " + Arrays.toString(pax));

        long[] seqResult = ParallelPrefixSum.sequential(pax);
        long t1 = System.nanoTime();
        long[] seqResult2 = ParallelPrefixSum.sequential(pax);
        long seqTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        long[] parResult = ParallelPrefixSum.parallel(pax);
        long parTime = System.nanoTime() - t2;

        System.out.println("\n  -- Results --");
        System.out.printf("  %-8s  %-10s  %-12s  %-12s%n", "Flight", "Pax", "SeqPrefix", "ParPrefix");
        System.out.println("  " + "-".repeat(48));
        int limit = Math.min(15, flights.size());
        for (int i = 0; i < limit; i++)
            System.out.printf("  %-8s  %-10d  %-12d  %-12d%n",
                    flights.get(i).getFlightNumber(), pax[i], seqResult[i], parResult[i]);
        if (flights.size() > limit + 5) {
            System.out.println("  ... (" + (flights.size() - limit - 5) + " intermediate flights calculated in parallel) ...");
            for (int i = flights.size() - 5; i < flights.size(); i++)
                System.out.printf("  %-8s  %-10d  %-12d  %-12d%n",
                        flights.get(i).getFlightNumber(), pax[i], seqResult[i], parResult[i]);
        }
        System.out.println("  " + "-".repeat(48));
        System.out.printf("  Cumulative Fleet Total Passengers: %,d%n", parResult[flights.size() - 1]);

        System.out.println("\n  Sequential time:  " + seqTime + " ns");
        System.out.println("  Parallel time:    " + parTime + " ns");
        System.out.println("  Results match:    " + Arrays.equals(seqResult, parResult));
        System.out.println("  (Parallel overhead dominates for small n; gains appear at n >> 1000)");
    }

    // ── 4. Parallel Reduce ────────────────────────────────────────────────────

    private void runParallelReduce() {
        System.out.println("\n  == Parallel Reduce ==");
        System.out.println("  Work W = O(n),  Span S = O(log n),  Parallelism = O(n / log n)");
        System.out.println("  Binary-tree reduction: each level halves the problem.");
        System.out.println();

        List<Flight> flights = dataset.getFlights();
        int[] pax = ParallelReduce.paxArray(flights);

        System.out.println("  Passenger counts: " + Arrays.toString(pax));
        System.out.println();

        try {
            long seqSum = ParallelReduce.sequentialSum(pax);
            int  seqMax = ParallelReduce.sequentialMax(pax);
            int  seqMin = ParallelReduce.sequentialMin(pax);

            long parSum = ParallelReduce.parallelSum(pax);
            int  parMax = ParallelReduce.parallelMax(pax);
            int  parMin = ParallelReduce.parallelMin(pax);

            System.out.printf("  %-20s  %-12s  %-12s%n", "Reduction", "Sequential", "Parallel");
            System.out.println("  " + "-".repeat(48));
            System.out.printf("  %-20s  %-12d  %-12d%n", "Sum (total pax)",  seqSum, parSum);
            System.out.printf("  %-20s  %-12d  %-12d%n", "Max (busiest flt)", seqMax, parMax);
            System.out.printf("  %-20s  %-12d  %-12d%n", "Min (lightest flt)", seqMin, parMin);

            System.out.println("\n  All results match: " +
                    (seqSum == parSum && seqMax == parMax && seqMin == parMin));

            // Find which flights are max/min
            for (Flight f : flights)
                if (f.getPassengerCount() == parMax)
                    System.out.println("  Busiest flight: " + f.getFlightNumber() +
                            " (" + f.getOrigin() + " -> " + f.getDestination() + ", " + parMax + " pax)");
            for (Flight f : flights)
                if (f.getPassengerCount() == parMin)
                    System.out.println("  Lightest flight: " + f.getFlightNumber() +
                            " (" + f.getOrigin() + " -> " + f.getDestination() + ", " + parMin + " pax)");

        } catch (Exception e) {
            System.out.println("  [!] Parallel execution error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private int readIntDefault(int def) {
        try {
            String s = scanner.nextLine().trim();
            return s.isEmpty() ? def : Integer.parseInt(s);
        } catch (Exception e) { return def; }
    }

    private double readDoubleDefault(double def) {
        try {
            String s = scanner.nextLine().trim();
            return s.isEmpty() ? def : Double.parseDouble(s);
        } catch (Exception e) { return def; }
    }
}
