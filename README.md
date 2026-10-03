# ✈ AirportFlow – Airport Operations & Resource Management Platform

> **DSA Project** | KLU | Semester 4  
> Modules: **CO1** (String Algorithms) · **CO2** (Suffix Structures) · **CO3** (Advanced DP)

---

## Project Structure

```
AirportFlow/
├── data/
│   └── airport_data.csv          ← 20-flight realistic dataset
├── src/
│   ├── Main.java                  ← Entry point
│   ├── dataset/
│   │   ├── Flight.java            ← POJO for one flight record
│   │   ├── CSVReader.java         ← CSV parser
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
│   └── menu/
│       ├── MainMenu.java          ← Top-level menu
│       ├── CO1Menu.java           ← CO1 algorithm menus
│       ├── CO2Menu.java           ← CO2 algorithm menus
│       └── CO3Menu.java           ← CO3 algorithm menus
├── compile.bat                    ← Windows compile script
├── run.bat                        ← Windows run script
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
javac -d out -sourcepath src src\Main.java src\dataset\*.java src\co1\*.java src\co2\*.java src\co3\*.java src\menu\*.java
java -cp out Main
```

> Requires **Java 8+**. No external libraries.

---

## Menu Flow

```
AIRPORTFLOW
    │
    ├── 1. Load Dataset         ← Reads data/airport_data.csv
    ├── 2. View Dataset         ← Prints full flight table
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
    │       └── CO3 – Advanced DP
    │             ├── Levenshtein          (autocorrect flight/city names)
    │             ├── Damerau-Levenshtein  (+ transposition errors)
    │             ├── Bitmask DP           (optimal gate assignment)
    │             ├── Matrix-Chain Mult.   (optimal data pipeline order)
    │             └── Optimal BST          (min expected flight lookup)
    └── 4. Exit
```

---

## Dataset (`airport_data.csv`)

| Column | Description |
|---|---|
| FlightID | Unique numeric ID |
| FlightNumber | IATA-style code (AI101, 6E202, …) |
| Origin / Destination | Indian cities |
| Status | ON-TIME / DELAYED / CANCELLED / BOARDING |
| Gate / Runway | Resource identifiers |
| DepartureTime / ArrivalTime | HH:MM strings |
| Airline | Air India / IndiGo / SpiceJet / Vistara |
| PassengerCount | Used as access frequency for OBST & matrix dims |
| BaggageCode | Searched by Rabin-Karp |
| AlertMessage | Multi-pattern searched by Aho-Corasick / KMP |
| OperationalNote | Indexed by suffix structures; Levenshtein autocorrect |

---

## Algorithm Complexity Summary

| Algorithm | Time | Space |
|---|---|---|
| KMP | O(n + m) | O(m) |
| Z-Function | O(n) | O(n) |
| Rabin-Karp | O(n + m) avg | O(1) |
| Aho-Corasick | O(n + Σ\|pᵢ\| + k) | O(Σ\|pᵢ\| · σ) |
| Suffix Array (basic) | O(n log² n) | O(n) |
| SA-IS | O(n) | O(n) |
| Kasai LCP | O(n) | O(n) |
| Ukkonen Suffix Tree | O(n) | O(n) |
| Suffix Automaton | O(n) | O(n) |
| Levenshtein | O(mn) | O(mn) |
| Damerau-Levenshtein | O(mn) | O(mn) |
| Bitmask DP | O(n² · 2^n) | O(2^n) |
| Matrix-Chain Mult. | O(n³) | O(n²) |
| Optimal BST | O(n²) | O(n²) |
