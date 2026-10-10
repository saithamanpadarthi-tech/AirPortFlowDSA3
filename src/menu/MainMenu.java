package menu;

import dataset.AirportDataset;
import dataset.CSVReader;
import dataset.Flight;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main interactive menu for AirportFlow.
 */
public class MainMenu {

    private static final String DATA_FILE = "data/airport_data.csv";

    private final Scanner        scanner;
    private final AirportDataset dataset;

    public MainMenu() {
        scanner = new Scanner(System.in);
        dataset = AirportDataset.getInstance();
    }

    // ── Entry point ──────────────────────────────────────────────────────────

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1: loadDataset();  break;
                case 2: viewDataset();  break;
                case 3: selectModule(); break;
                case 4:
                    System.out.println("\n  Thank you for using AirportFlow. Safe travels!\n");
                    running = false;
                    break;
                default:
                    System.out.println("  [!] Invalid option. Please enter 1-4.");
            }
        }
        scanner.close();
    }

    // ── Banner & menus ───────────────────────────────────────────────────────

    private void printBanner() {
        System.out.println();
        System.out.println("  ============================================");
        System.out.println("        A I R P O R T F L O W");
        System.out.println("    Airport Operations & Resource Management");
        System.out.println("    DSA Project  |  CO1 - CO6");
        System.out.println("  ============================================");
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println("  --------------------------------------------");
        System.out.println("               M A I N   M E N U");
        System.out.println("  --------------------------------------------");
        System.out.println("    1.  Load Dataset");
        System.out.println("    2.  View Dataset");
        System.out.println("    3.  Select Module  (CO1 - CO6)");
        System.out.println("    4.  Exit");
        System.out.println("  --------------------------------------------");
        System.out.print("  Enter choice: ");
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    private void loadDataset() {
        System.out.println("\n  [*] Loading dataset from: " + DATA_FILE + " ...");
        try {
            List<Flight> flights = CSVReader.read(DATA_FILE);
            dataset.load(flights);
            System.out.println("  [OK] Dataset loaded! " + flights.size() + " flights available.");
        } catch (IOException e) {
            System.out.println("  [ERR] Failed to load dataset: " + e.getMessage());
            System.out.println("  [!]  Ensure 'data/airport_data.csv' exists in the project root.");
        }
    }

    private void viewDataset() {
        if (!checkLoaded()) return;
        dataset.printTable();
    }

    private void selectModule() {
        if (!checkLoaded()) return;

        System.out.println("\n  --------------------------------------------");
        System.out.println("           S E L E C T   M O D U L E");
        System.out.println("  --------------------------------------------");
        System.out.println("    1.  CO1 - String Algorithms");
        System.out.println("        KMP | Z-Function | Rabin-Karp | Aho-Corasick");
        System.out.println();
        System.out.println("    2.  CO2 - Suffix Structures");
        System.out.println("        Suffix Array | SA-IS | LCP/Kasai");
        System.out.println("        Suffix Tree | Suffix Automaton");
        System.out.println();
        System.out.println("    3.  CO3 - Advanced Dynamic Programming");
        System.out.println("        Levenshtein | Damerau-Levenshtein");
        System.out.println("        Bitmask DP | Matrix-Chain | Optimal BST");
        System.out.println();
        System.out.println("    4.  CO4 - Network Flow");
        System.out.println("        Ford-Fulkerson | Dinic | Max-Flow/Min-Cut | Bipartite Matching");
        System.out.println();
        System.out.println("    5.  CO5 - NP-Completeness & Approximations");
        System.out.println("        Vertex Cover | Independent Set | Set Cover | TSP");
        System.out.println();
        System.out.println("    6.  CO6 - Randomized & Parallel Algorithms");
        System.out.println("        Monte Carlo | Las Vegas | Parallel Prefix | Parallel Reduce");
        System.out.println();
        System.out.println("    7.  Back");
        System.out.println("  --------------------------------------------");
        System.out.print("  Enter choice: ");

        int choice = readInt();
        switch (choice) {
            case 1: new CO1Menu(scanner).start(); break;
            case 2: new CO2Menu(scanner).start(); break;
            case 3: new CO3Menu(scanner).start(); break;
            case 4: new CO4Menu(scanner).start(); break;
            case 5: new CO5Menu(scanner).start(); break;
            case 6: new CO6Menu(scanner).start(); break;
            case 7: break;
            default: System.out.println("  [!] Invalid option.");
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private boolean checkLoaded() {
        if (!dataset.isLoaded()) {
            System.out.println("\n  [!] Dataset not loaded. Please choose option 1 first.\n");
            return false;
        }
        return true;
    }

    int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    String readLine() {
        return scanner.nextLine().trim();
    }
}
