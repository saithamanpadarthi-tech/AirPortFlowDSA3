package co6;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import dataset.Flight;

/**
 * CO6 - Parallel Algorithm: Parallel Prefix Sum (Scan)
 *
 * PROBLEM:
 *   Given array A[0..n-1], compute P[i] = A[0] + A[1] + ... + A[i] for all i.
 *
 * SEQUENTIAL: O(n) time, O(1) space.
 *
 * PARALLEL (binary-tree up-sweep + down-sweep):
 *   Work W = O(n)       -- same total operations as sequential
 *   Span S = O(log n)   -- critical path length (depth of binary tree)
 *   Parallelism = W/S = O(n / log n)
 *
 * WORK-SPAN ANALYSIS:
 *   By Brent's theorem: T_p <= W/p + S  (time on p processors)
 *   With p = n/log(n) processors: T_p = O(log n)  (optimal speedup)
 *
 * Airport use-case:
 *   Compute cumulative passenger counts across all flights in parallel
 *   (running total, e.g., P[i] = total passengers on flights 0..i).
 */
public class ParallelPrefixSum {

    // ── Sequential (baseline) ─────────────────────────────────────────────────

    /**
     * Sequential prefix sum for comparison.
     * @return prefix[] where prefix[i] = sum of arr[0..i]
     */
    public static long[] sequential(int[] arr) {
        long[] prefix = new long[arr.length];
        if (arr.length == 0) return prefix;
        prefix[0] = arr[0];
        for (int i = 1; i < arr.length; i++)
            prefix[i] = prefix[i - 1] + arr[i];
        return prefix;
    }

    // ── Parallel (ForkJoin divide-and-conquer) ────────────────────────────────

    /**
     * Parallel prefix sum using ForkJoinPool.
     * Splits array recursively; right half offset by left-half sum.
     *
     * @param arr input array
     * @return prefix sum array (parallel computation)
     */
    public static long[] parallel(int[] arr) {
        int n = arr.length;
        long[] result = new long[n];
        if (n == 0) return result;
        long[] input  = new long[n];
        for (int i = 0; i < n; i++) input[i] = arr[i];

        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new PrefixTask(input, result, 0, n - 1, 0L));
        return result;
    }

    // ── ForkJoin task ─────────────────────────────────────────────────────────

    private static class PrefixTask extends RecursiveAction {
        private static final int THRESHOLD = 4; // sequential below this size

        final long[] src, dst;
        final int lo, hi;
        final long offset; // sum of all elements before lo

        PrefixTask(long[] src, long[] dst, int lo, int hi, long offset) {
            this.src = src; this.dst = dst;
            this.lo = lo; this.hi = hi; this.offset = offset;
        }

        @Override
        protected void compute() {
            if (hi - lo < THRESHOLD) {
                // Base case: sequential scan
                long sum = offset;
                for (int i = lo; i <= hi; i++) {
                    sum += src[i];
                    dst[i] = sum;
                }
            } else {
                int mid = (lo + hi) / 2;

                // Compute sum of left half sequentially (for offset)
                long leftSum = 0;
                for (int i = lo; i <= mid; i++) leftSum += src[i];

                // Spawn left and right sub-tasks in parallel
                PrefixTask leftTask  = new PrefixTask(src, dst, lo,      mid, offset);
                PrefixTask rightTask = new PrefixTask(src, dst, mid + 1, hi,  offset + leftSum);
                leftTask.fork();
                rightTask.compute(); // run right in current thread
                leftTask.join();     // wait for left
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Extract passenger count array from flight list. */
    public static int[] fromFlights(List<Flight> flights) {
        int[] arr = new int[flights.size()];
        for (int i = 0; i < flights.size(); i++)
            arr[i] = flights.get(i).getPassengerCount();
        return arr;
    }
}
