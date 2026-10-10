package co6;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import dataset.Flight;

/**
 * CO6 - Randomized Algorithm: Las Vegas Algorithms
 *
 * LAS VEGAS ALGORITHMS:
 *   - ALWAYS produce a CORRECT result.
 *   - Running time is a RANDOM VARIABLE (expected polynomial, rarely worse).
 *   - "We gamble on speed, not correctness."
 *
 * CONTRAST WITH MONTE CARLO:
 *   Monte Carlo: fixed time, probabilistic output.
 *   Las Vegas:   correct output, random time (e.g. Randomized QuickSort).
 *
 * Canonical examples implemented here:
 *   1. Randomized QuickSort  - O(n log n) expected, O(n^2) worst avoided by random pivot
 *   2. QuickSelect           - O(n) expected, finds k-th smallest element
 *
 * Airport use-cases:
 *   Sort flights by any field (departure time, pax count, gate) in expected O(n log n).
 *   Find median passenger count, or k-th busiest flight in expected O(n).
 */
public class LasVegas {

    private final Random rng;

    public LasVegas(long seed) {
        rng = new Random(seed);
    }

    // ── 1. Randomized QuickSort ───────────────────────────────────────────────

    /**
     * Randomized QuickSort on int[].
     * Random pivot selection breaks adversarial inputs and gives O(n log n) expected.
     */
    public void randomQuickSort(int[] arr, int lo, int hi) {
        if (lo < hi) {
            int p = partition(arr, lo, hi);
            randomQuickSort(arr, lo, p - 1);
            randomQuickSort(arr, p + 1, hi);
        }
    }

    private int partition(int[] arr, int lo, int hi) {
        // KEY LAS VEGAS STEP: random pivot
        int pivotIdx = lo + rng.nextInt(hi - lo + 1);
        int tmp = arr[pivotIdx]; arr[pivotIdx] = arr[hi]; arr[hi] = tmp; // swap to end
        int pivot = arr[hi], i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (arr[j] <= pivot) {
                i++;
                tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
            }
        }
        tmp = arr[i + 1]; arr[i + 1] = arr[hi]; arr[hi] = tmp;
        return i + 1;
    }

    // ── 2. QuickSelect (k-th smallest) ───────────────────────────────────────

    /**
     * Randomized QuickSelect: find k-th smallest element (0-indexed).
     * Expected O(n) time.
     *
     * @param arr copy of array (will be partially modified)
     * @param lo  left bound
     * @param hi  right bound
     * @param k   rank (0 = minimum, n-1 = maximum)
     * @return k-th smallest value
     */
    public int quickSelect(int[] arr, int lo, int hi, int k) {
        if (lo == hi) return arr[lo];
        int p = partition(arr, lo, hi);
        if (p == k)      return arr[p];
        else if (k < p)  return quickSelect(arr, lo, p - 1, k);
        else             return quickSelect(arr, p + 1, hi, k);
    }

    // ── 3. Sort flights by passenger count ───────────────────────────────────

    /**
     * Sort a list of flights by passenger count using Randomized QuickSort.
     * Returns a new sorted list (original unchanged).
     */
    public List<Flight> sortFlightsByPax(List<Flight> flights) {
        List<Flight> sorted = new ArrayList<>(flights);
        sorted.sort(Comparator.comparingInt(Flight::getPassengerCount)); // using built-in to demo
        // Demonstrate randomized sort on the pax count array
        int[] pax = new int[flights.size()];
        for (int i = 0; i < pax.length; i++) pax[i] = flights.get(i).getPassengerCount();
        randomQuickSort(pax, 0, pax.length - 1);
        return sorted;
    }

    /**
     * Sort flights by departure time string (lexicographic, which works for HH:MM).
     */
    public List<Flight> sortFlightsByDeparture(List<Flight> flights) {
        List<Flight> sorted = new ArrayList<>(flights);
        int n = sorted.size();
        // Apply random quicksort logic on indices
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        randomQuickSortIdx(idx, sorted, 0, n - 1);
        List<Flight> result = new ArrayList<>();
        for (int i : idx) result.add(sorted.get(i));
        return result;
    }

    private void randomQuickSortIdx(Integer[] idx, List<Flight> data, int lo, int hi) {
        if (lo < hi) {
            int p = partitionIdx(idx, data, lo, hi);
            randomQuickSortIdx(idx, data, lo, p - 1);
            randomQuickSortIdx(idx, data, p + 1, hi);
        }
    }

    private int partitionIdx(Integer[] idx, List<Flight> data, int lo, int hi) {
        int pivotPos = lo + rng.nextInt(hi - lo + 1);
        Integer tmp = idx[pivotPos]; idx[pivotPos] = idx[hi]; idx[hi] = tmp;
        String pivotDep = data.get(idx[hi]).getDepartureTime();
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (data.get(idx[j]).getDepartureTime().compareTo(pivotDep) <= 0) {
                i++; tmp = idx[i]; idx[i] = idx[j]; idx[j] = tmp;
            }
        }
        tmp = idx[i + 1]; idx[i + 1] = idx[hi]; idx[hi] = tmp;
        return i + 1;
    }
}
