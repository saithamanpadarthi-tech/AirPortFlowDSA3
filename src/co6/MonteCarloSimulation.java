package co6;

import java.util.List;
import java.util.Random;
import dataset.Flight;

/**
 * CO6 - Randomized Algorithm: Monte Carlo Simulation
 *
 * MONTE CARLO ALGORITHMS:
 *   - Always finish in bounded time.
 *   - Output is CORRECT WITH HIGH PROBABILITY (may rarely be wrong).
 *   - More trials => higher confidence.
 *   - Error probability: epsilon(trials) -> 0 as trials -> infinity.
 *
 * CONTRAST WITH LAS VEGAS:
 *   Las Vegas: always correct, random runtime.
 *   Monte Carlo: bounded runtime, probabilistic correctness.
 *
 * Airport use-cases:
 *   1. Estimate probability that a flight will be delayed.
 *   2. Estimate expected passenger wait time.
 *   3. Estimate airport throughput (flights per hour).
 */
public class MonteCarloSimulation {

    private final Random rng;

    public MonteCarloSimulation(long seed) {
        rng = new Random(seed);
    }

    // ── 1. Delay Probability Estimation ──────────────────────────────────────

    /**
     * Estimate the probability that a flight is delayed, given external factors.
     * Each trial independently samples three delay sources.
     *
     * @param trials        number of random simulations (more = more accurate)
     * @param weatherFactor [0,1] severity of weather (0=clear, 1=storm)
     * @param trafficFactor [0,1] ATC congestion level
     * @return estimated delay probability in [0,1]
     */
    public double estimateDelayProbability(int trials, double weatherFactor, double trafficFactor) {
        int delayCount = 0;
        for (int i = 0; i < trials; i++) {
            boolean weather = rng.nextDouble() < 0.30 * weatherFactor;
            boolean tech    = rng.nextDouble() < 0.10;           // 10% base technical rate
            boolean traffic = rng.nextDouble() < 0.20 * trafficFactor;
            if (weather || tech || traffic) delayCount++;
        }
        return (double) delayCount / trials;
    }

    // ── 2. Expected Wait Time ─────────────────────────────────────────────────

    /**
     * Estimate expected passenger wait time (minutes) using exponential-distribution
     * model: delay ~ Exp(1/avgDelay).
     *
     * @param trials   simulation count
     * @param avgDelay mean delay in minutes
     * @return expected wait time in minutes
     */
    public double estimateExpectedWait(int trials, double avgDelay) {
        double total = 0;
        for (int i = 0; i < trials; i++) {
            // Inverse transform: X = -mean * ln(U),  U ~ Uniform(0,1)
            double delay = -avgDelay * Math.log(1.0 - rng.nextDouble());
            total += delay;
        }
        return total / trials;
    }

    // ── 3. Airport Throughput ─────────────────────────────────────────────────

    /**
     * Estimate expected flights per hour given runway count and turnaround time.
     * Turnaround modelled as Normal(avgTurnaround, 5 min stddev).
     *
     * @param trials         simulation count
     * @param numRunways     number of active runways
     * @param avgTurnaround  mean turnaround time in minutes
     * @return estimated flights per hour
     */
    public double estimateThroughput(int trials, int numRunways, double avgTurnaround) {
        double totalFlights = 0;
        for (int i = 0; i < trials; i++) {
            double[] runwayTime = new double[numRunways];
            int flights = 0;
            for (int r = 0; r < numRunways; r++) {
                while (runwayTime[r] < 60.0) {
                    double t = avgTurnaround + rng.nextGaussian() * 5.0;
                    t = Math.max(t, 5.0); // at least 5 minutes
                    runwayTime[r] += t;
                    if (runwayTime[r] <= 60.0) flights++;
                }
            }
            totalFlights += flights;
        }
        return totalFlights / trials;
    }

    // ── 4. Dataset-based simulation ───────────────────────────────────────────

    /**
     * Simulate delay outcomes for each flight in the dataset.
     * Returns probability string per flight based on status.
     */
    public double[] simulateFleetDelays(List<Flight> flights, int trials) {
        double[] probs = new double[flights.size()];
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            // Weather factor higher for DELAYED/CANCELLED flights
            double wf = f.getStatus().equals("DELAYED") ? 0.8
                      : f.getStatus().equals("CANCELLED") ? 1.0 : 0.2;
            double tf = (f.getPassengerCount() > 170) ? 0.9 : 0.4;
            probs[i] = estimateDelayProbability(trials, wf, tf);
        }
        return probs;
    }
}
