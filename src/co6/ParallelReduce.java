package co6;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import dataset.Flight;

/**
 * CO6 - Parallel Algorithm: Parallel Reduce
 *
 * PROBLEM:
 *   Apply an associative operator (sum, max, min) across an array in parallel.
 *
 * PARALLEL REDUCE (binary tree):
 *   Work W = O(n)       -- visit every element once
 *   Span S = O(log n)   -- tree depth (critical path)
 *   Parallelism = W/S = O(n / log n)
 *
 * WORK-SPAN ANALYSIS (Brent's theorem):
 *   T_p <= W/p + S
 *   With p processors: T_p = O(n/p + log n)
 *   Optimal with p = n/log(n) processors: T_p = O(log n)
 *
 * Airport use-cases:
 *   - Sum:  total passengers across all flights (parallel aggregation)
 *   - Max:  busiest flight (highest passenger count)
 *   - Min:  least loaded flight (lowest passenger count)
 */
public class ParallelReduce {

    private static final int THRESHOLD = 4;

    // ── Parallel Sum ─────────────────────────────────────────────────────────

    public static long parallelSum(int[] arr) {
        if (arr.length == 0) return 0;
        return ForkJoinPool.commonPool().invoke(new SumTask(arr, 0, arr.length - 1));
    }

    private static class SumTask extends RecursiveTask<Long> {
        final int[] arr; final int lo, hi;
        SumTask(int[] arr, int lo, int hi) { this.arr=arr; this.lo=lo; this.hi=hi; }

        @Override protected Long compute() {
            if (hi - lo < THRESHOLD) {
                long s = 0;
                for (int i = lo; i <= hi; i++) s += arr[i];
                return s;
            }
            int mid = (lo + hi) / 2;
            SumTask left  = new SumTask(arr, lo,      mid);
            SumTask right = new SumTask(arr, mid + 1, hi);
            left.fork();
            long r = right.compute();
            return left.join() + r;
        }
    }

    // ── Parallel Max ─────────────────────────────────────────────────────────

    public static int parallelMax(int[] arr) {
        if (arr.length == 0) return Integer.MIN_VALUE;
        return ForkJoinPool.commonPool().invoke(new MaxTask(arr, 0, arr.length - 1));
    }

    private static class MaxTask extends RecursiveTask<Integer> {
        final int[] arr; final int lo, hi;
        MaxTask(int[] arr, int lo, int hi) { this.arr=arr; this.lo=lo; this.hi=hi; }

        @Override protected Integer compute() {
            if (hi - lo < THRESHOLD) {
                int m = arr[lo];
                for (int i = lo+1; i <= hi; i++) m = Math.max(m, arr[i]);
                return m;
            }
            int mid = (lo + hi) / 2;
            MaxTask left  = new MaxTask(arr, lo,      mid);
            MaxTask right = new MaxTask(arr, mid + 1, hi);
            left.fork();
            int r = right.compute();
            return Math.max(left.join(), r);
        }
    }

    // ── Parallel Min ─────────────────────────────────────────────────────────

    public static int parallelMin(int[] arr) {
        if (arr.length == 0) return Integer.MAX_VALUE;
        return ForkJoinPool.commonPool().invoke(new MinTask(arr, 0, arr.length - 1));
    }

    private static class MinTask extends RecursiveTask<Integer> {
        final int[] arr; final int lo, hi;
        MinTask(int[] arr, int lo, int hi) { this.arr=arr; this.lo=lo; this.hi=hi; }

        @Override protected Integer compute() {
            if (hi - lo < THRESHOLD) {
                int m = arr[lo];
                for (int i = lo+1; i <= hi; i++) m = Math.min(m, arr[i]);
                return m;
            }
            int mid = (lo + hi) / 2;
            MinTask left  = new MinTask(arr, lo,      mid);
            MinTask right = new MinTask(arr, mid + 1, hi);
            left.fork();
            int r = right.compute();
            return Math.min(left.join(), r);
        }
    }

    // ── Sequential baselines (for comparison) ─────────────────────────────────

    public static long sequentialSum(int[] arr) {
        long s = 0; for (int v : arr) s += v; return s;
    }
    public static int sequentialMax(int[] arr) {
        int m = Integer.MIN_VALUE; for (int v : arr) m = Math.max(m, v); return m;
    }
    public static int sequentialMin(int[] arr) {
        int m = Integer.MAX_VALUE; for (int v : arr) m = Math.min(m, v); return m;
    }

    // ── Dataset helper ────────────────────────────────────────────────────────

    /** Extract passenger count array from flight list. */
    public static int[] paxArray(List<Flight> flights) {
        int[] arr = new int[flights.size()];
        for (int i = 0; i < flights.size(); i++) arr[i] = flights.get(i).getPassengerCount();
        return arr;
    }
}
