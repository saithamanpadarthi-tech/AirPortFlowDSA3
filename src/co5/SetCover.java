package co5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CO5 - NP-Hard Approximation: Set Cover (Greedy, H(n)-approximation)
 *
 * PROBLEM (NP-Hard):
 *   Given universe U (|U|=n) and sets S1..Sm, find fewest sets whose union=U.
 *
 * GREEDY ALGORITHM:
 *   Repeat: pick the set covering the most uncovered elements.
 *
 * APPROXIMATION RATIO: H(n) = 1 + 1/2 + 1/3 + ... + 1/n ≈ ln(n) + 1
 *   This ratio is tight (cannot do better unless P=NP).
 *
 * Airport use-case:
 *   Universe U = all flights that need maintenance.
 *   Sets Si = flights that crew-i can service.
 *   Goal = minimum maintenance crews to cover all flights.
 */
public class SetCover {

    private final int universeSize;
    private final List<Set<Integer>> sets;
    private final List<String> setNames;

    public SetCover(int universeSize) {
        this.universeSize = universeSize;
        sets     = new ArrayList<>();
        setNames = new ArrayList<>();
    }

    /** Add a named set of element indices (0-based). */
    public void addSet(String name, Set<Integer> elements) {
        sets.add(new HashSet<>(elements));
        setNames.add(name);
    }

    /**
     * Greedy approximation for Set Cover.
     * Each iteration picks the set covering the most uncovered elements.
     *
     * @return indices of selected sets (in order of selection)
     */
    public List<Integer> greedyCover() {
        Set<Integer> uncovered = new HashSet<>();
        for (int i = 0; i < universeSize; i++) uncovered.add(i);

        boolean[]    used   = new boolean[sets.size()];
        List<Integer> result = new ArrayList<>();

        while (!uncovered.isEmpty()) {
            int best = -1, bestCount = 0;
            for (int i = 0; i < sets.size(); i++) {
                if (!used[i]) {
                    int count = 0;
                    for (int e : sets.get(i))
                        if (uncovered.contains(e)) count++;
                    if (count > bestCount) { bestCount = count; best = i; }
                }
            }
            if (best == -1) break; // remaining elements uncoverable
            used[best] = true;
            result.add(best);
            uncovered.removeAll(sets.get(best));
        }
        return result;
    }

    public String    getSetName(int idx) { return setNames.get(idx); }
    public Set<Integer> getSet(int idx)  { return sets.get(idx); }
    public int       getSetCount()        { return sets.size(); }
    public int       getUniverseSize()    { return universeSize; }
}
