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
        System.out.println("    1.  Load Airport Data");
        System.out.println("    2.  View All Flights");
        System.out.println("    3.  Airport Operations");
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
        System.out.println("       A I R P O R T   O P E R A T I O N S");
        System.out.println("  --------------------------------------------");
        System.out.println("    1.  Search & Match Flights");
        System.out.println("        Search flights, baggage codes and alerts by keyword");
        System.out.println();
        System.out.println("    2.  Index & Browse Flight Records");
        System.out.println("        Index and query flight logs, find repeated phrases");
        System.out.println();
        System.out.println("    3.  Autocorrect & Smart Scheduling");
        System.out.println("        Fix name typos, assign gates, optimize lookup speed");
        System.out.println();
        System.out.println("    4.  Gate & Runway Capacity Management");
        System.out.println("        Maximize simultaneous departures, find bottlenecks");
        System.out.println();
        System.out.println("    5.  Crew & Route Planning");
        System.out.println("        Assign minimum crews, cover all routes, plan gate tours");
        System.out.println();
        System.out.println("    6.  Live Airport Analytics");
        System.out.println("        Estimate delays, sort flights, compute fleet statistics");
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
