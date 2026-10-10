# ✈ AirportFlow – Airport Operations & Resource Management Platform

> **DSA Project** | KLU | Semester 4  
> Modules: **CO1** (String Algorithms) · **CO2** (Suffix Structures) · **CO3** (Advanced DP) · **CO4** (Network Flow) · **CO5** (NP-Completeness & Approximations) · **CO6** (Randomized & Parallel Algorithms)

---

## Project Structure

```
AirportFlow/
├── data/
│   └── airport_data.csv          ← 20-flight realistic dataset
├── src/
│   ├── Main.java                  ← Entry point
│   ├── dataset/
│   │   ├── Flight.java            ← POJO for flight record (14 attributes)
│   │   ├── CSVReader.java         ← Buffered CSV parser
│   │   └── AirportDataset.java   ← Singleton dataset holder
│   ├── co1/                       ← String Algorithms
│   │   ├── KMP.java               ← Knuth-Morris-Pratt O(n+m)
│   │   ├── ZFunction.java         ← Z-Function O(n)
│   │   ├── RabinKarp.java         ← Rolling-hash search
│   │   └── AhoCorasick.java       ← Multi-pattern automaton
│   ├── co2/                       ← Suffix Structures
│   │   ├── SuffixArray.java       ← O(n log² n) suffix array
│   │   ├── SAIS.java              ← SA-IS linear O(n)
│   │   ├── LCPKasai.java          ← Kasai LCP array O(n)
│   │   ├── SuffixTree.java        ← Ukkonen's O(n)
│   │   └── SuffixAutomaton.java   ← SAM O(n)
│   ├── co3/                       ← Advanced DP
│   │   ├── Levenshtein.java       ← Edit distance O(mn)
│   │   ├── DamerauLevenshtein.java← + transpositions O(mn)
│   │   ├── BitmaskDP.java         ← Gate assignment O(n²·2^n)
│   │   ├── MatrixChain.java       ← Matrix-chain O(n³)
│   │   └── OptimalBST.java        ← OBST O(n²)
│   ├── co4/                       ← Network Flow
│   │   ├── FordFulkerson.java     ← Edmonds-Karp O(VE²) + Min-Cut
│   │   ├── Dinic.java             ← Level graph + blocking flow O(V²E)
│   │   └── BipartiteMatching.java ← Flight-to-gate matching O(VE)
│   ├── co5/                       ← NP-Completeness & Approximations
│   │   ├── VertexCover.java       ← 2-approximation (maximal matching)
│   │   ├── IndependentSet.java    ← Derived via VC complement
│   │   ├── SetCover.java          ← Greedy H(n) ≈ ln(n) approximation
│   │   └── TSPApprox.java         ← Nearest-neighbour heuristic
│   ├── co6/                       ← Randomized & Parallel Algorithms
│   │   ├── MonteCarloSimulation.java ← Probabilistic delay & throughput
│   │   ├── LasVegas.java          ← Randomized QuickSort & QuickSelect
│   │   ├── ParallelPrefixSum.java ← ForkJoin parallel scan (W=O(n), S=O(log n))
│   │   └── ParallelReduce.java    ← ForkJoin parallel sum/max/min
│   └── menu/
│       ├── MainMenu.java          ← Top-level menu
│       ├── CO1Menu.java           ← CO1 algorithm menus
│       ├── CO2Menu.java           ← CO2 algorithm menus
│       ├── CO3Menu.java           ← CO3 algorithm menus
│       ├── CO4Menu.java           ← CO4 algorithm menus
│       ├── CO5Menu.java           ← CO5 algorithm menus
│       └── CO6Menu.java           ← CO6 algorithm menus
├── compile.bat                    ← Windows compile script (JDK 21)
├── run.bat                        ← Windows run script (JDK 21)
└── README.md
```

---

## How to Build & Run

### Windows (Quick Start)
```
1. Double-click compile.bat   (or run it in CMD from project root)
2. Double-click run.bat
```

### Manual (Command Prompt from project root)
```cmd
javac -d out -sourcepath src src\Main.java src\dataset\*.java src\co1\*.java src\co2\*.java src\co3\*.java src\co4\*.java src\co5\*.java src\co6\*.java src\menu\*.java
java -cp out Main
```

---

## Menu Flow

```
AIRPORTFLOW
    │
    ├── 1. Load Dataset         ← Reads data/airport_data.csv
    ├── 2. View Dataset         ← Prints formatted flight table
    ├── 3. Select Module
    │       ├── CO1 – String Algorithms
    │       │     ├── KMP              (search alert messages / flight numbers)
    │       │     ├── Z-Function       (detect repeated operational messages)
    │       │     ├── Rabin-Karp       (search baggage / booking codes)
    │       │     └── Aho-Corasick     (detect multiple status patterns)
    │       │
    │       ├── CO2 – Suffix Structures
    │       │     ├── Suffix Array     (index & binary-search notes)
    │       │     ├── SA-IS            (linear-time suffix array)
    │       │     ├── LCP / Kasai      (find repeated descriptions)
    │       │     ├── Suffix Tree      (Ukkonen substring queries)
    │       │     └── Suffix Automaton (compact pattern matching)
    │       │
    │       ├── CO3 – Advanced DP
    │       │     ├── Levenshtein          (autocorrect flight/city names)
    │       │     ├── Damerau-Levenshtein  (+ transposition errors)
    │       │     ├── Bitmask DP           (optimal gate assignment)
    │       │     ├── Matrix-Chain Mult.   (optimal data pipeline order)
    │       │     └── Optimal BST          (min expected flight lookup)
    │       │
    │       ├── CO4 – Network Flow
    │       │     ├── Ford-Fulkerson       (Edmonds-Karp max flow)
    │       │     ├── Dinic's Algorithm    (layered graph blocking flow)
    │       │     ├── Max-Flow / Min-Cut   (duality bottleneck verification)
    │       │     └── Bipartite Matching   (flight-to-gate assignment)
    │       │
    │       ├── CO5 – NP-Completeness & Approximations
    │       │     ├── Vertex Cover         (2-approximation route monitoring)
    │       │     ├── Independent Set      (complement of VC scheduling)
    │       │     ├── Set Cover            (greedy ln(n) crew assignment)
    │       │     └── TSP                  (nearest neighbour gate tour)
    │       │
    │       └── CO6 – Randomized & Parallel Algorithms
    │             ├── Monte Carlo          (delay prob & throughput simulation)
    │             ├── Las Vegas            (Randomized QuickSort & QuickSelect)
    │             ├── Parallel Prefix Sum  (cumulative passengers scan)
    │             └── Parallel Reduce      (parallel sum/max/min)
    │
    └── 4. Exit
```

---

## Algorithm Complexity Summary

| Module | Algorithm | Time Complexity | Space Complexity / Ratio |
|---|---|---|---|
| **CO1** | KMP | $O(n + m)$ | $O(m)$ |
| **CO1** | Z-Function | $O(n)$ | $O(n)$ |
| **CO1** | Rabin-Karp | $O(n + m)$ avg | $O(1)$ |
| **CO1** | Aho-Corasick | $O(n + \sum |p_i| + k)$ | $O(\sum |p_i| \cdot \sigma)$ |
| **CO2** | Suffix Array (basic) | $O(n \log^2 n)$ | $O(n)$ |
| **CO2** | SA-IS | $O(n)$ | $O(n)$ |
| **CO2** | Kasai LCP | $O(n)$ | $O(n)$ |
| **CO2** | Ukkonen Suffix Tree | $O(n)$ | $O(n)$ |
| **CO2** | Suffix Automaton | $O(n)$ | $O(n)$ |
| **CO3** | Levenshtein | $O(mn)$ | $O(mn)$ |
| **CO3** | Damerau-Levenshtein | $O(mn)$ | $O(mn)$ |
| **CO3** | Bitmask DP | $O(n^2 \cdot 2^n)$ | $O(2^n)$ |
| **CO3** | Matrix-Chain Mult. | $O(n^3)$ | $O(n^2)$ |
| **CO3** | Optimal BST | $O(n^2)$ | $O(n^2)$ |
| **CO4** | Ford-Fulkerson (Edmonds-Karp) | $O(V E^2)$ | $O(V^2)$ |
| **CO4** | Dinic's Algorithm | $O(V^2 E)$ / $O(E \sqrt{V})$ | $O(V^2)$ |
| **CO4** | Bipartite Matching | $O(V E)$ | $O(V + E)$ |
| **CO5** | Vertex Cover | $O(V + E)$ | 2-Approximation |
| **CO5** | Independent Set | $O(V + E)$ | Derived from VC complement |
| **CO5** | Set Cover (Greedy) | $O(m \cdot n)$ | $H(n) \approx \ln(n) + 1$ |
| **CO5** | TSP (Nearest Neighbour) | $O(n^2)$ | Metric heuristic |
| **CO6** | Monte Carlo Simulation | $O(\text{trials})$ | $O(1)$ |
| **CO6** | Las Vegas QuickSort / Select | $O(n \log n)$ / $O(n)$ exp | $O(\log n)$ |
| **CO6** | Parallel Prefix Sum | $W = O(n), S = O(\log n)$ | Parallel ForkJoin |
| **CO6** | Parallel Reduce (Sum/Max/Min) | $W = O(n), S = O(\log n)$ | Parallel ForkJoin |
