package dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that holds the loaded flight dataset and exposes
 * field-specific helper collections used by algorithm menus.
 */
public class AirportDataset {

    private static AirportDataset instance;
    private List<Flight> flights = new ArrayList<>();
    private boolean loaded = false;

    private AirportDataset() {}

    public static AirportDataset getInstance() {
        if (instance == null) instance = new AirportDataset();
        return instance;
    }

    /** Load flights into the singleton. */
    public void load(List<Flight> data) {
        flights = data;
        loaded  = true;
    }

    public boolean isLoaded()        { return loaded; }
    public List<Flight> getFlights() { return flights; }

    // ── Field extractors ─────────────────────────────────────────────────────

    public List<String> getFlightNumbers() {
        List<String> list = new ArrayList<>();
        for (Flight f : flights) list.add(f.getFlightNumber());
        return list;
    }

    public List<String> getAlertMessages() {
        List<String> list = new ArrayList<>();
        for (Flight f : flights) list.add(f.getAlertMessage());
        return list;
    }

    public List<String> getBaggageCodes() {
        List<String> list = new ArrayList<>();
        for (Flight f : flights) list.add(f.getBaggageCode());
        return list;
    }

    public List<String> getOperationalNotes() {
        List<String> list = new ArrayList<>();
        for (Flight f : flights) list.add(f.getOperationalNote());
        return list;
    }

    // ── Display ──────────────────────────────────────────────────────────────

    public void printTable() {
        String sep = "-".repeat(132);
        System.out.println("\n  " + sep);
        System.out.printf("  %-4s %-8s %-12s %-12s %-10s %-5s %-4s %-6s %-6s %-12s %-5s %-18s%n",
                "ID", "Flight", "Origin", "Dest", "Status", "Gate", "RWY",
                "Dep", "Arr", "Airline", "Pax", "BaggageCode");
        System.out.println("  " + sep);
        for (Flight f : flights) {
            System.out.printf("  %-4d %-8s %-12s %-12s %-10s %-5s %-4s %-6s %-6s %-12s %-5d %-18s%n",
                    f.getFlightId(), f.getFlightNumber(), f.getOrigin(), f.getDestination(),
                    f.getStatus(), f.getGate(), f.getRunway(),
                    f.getDepartureTime(), f.getArrivalTime(),
                    f.getAirline(), f.getPassengerCount(), f.getBaggageCode());
        }
        System.out.println("  " + sep);
        System.out.println("  Total flights loaded: " + flights.size());
    }
}
