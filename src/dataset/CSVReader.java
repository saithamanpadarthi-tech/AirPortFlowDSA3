package dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads airport_data.csv and returns a list of Flight objects.
 */
public class CSVReader {

    public static List<Flight> read(String filePath) throws IOException {
        List<Flight> flights = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line = br.readLine(); // skip header row
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Split on comma; limit=-1 keeps trailing empty fields
            String[] p = line.split(",", -1);
            if (p.length < 14) {
                System.err.println("  [!] Skipping malformed CSV line: " + line);
                continue;
            }

            try {
                int    flightId       = Integer.parseInt(p[0].trim());
                String flightNumber   = p[1].trim();
                String origin         = p[2].trim();
                String destination    = p[3].trim();
                String status         = p[4].trim();
                String gate           = p[5].trim();
                String runway         = p[6].trim();
                String departureTime  = p[7].trim();
                String arrivalTime    = p[8].trim();
                String airline        = p[9].trim();
                int    passengerCount = Integer.parseInt(p[10].trim());
                String baggageCode    = p[11].trim();
                String alertMessage   = p[12].trim();
                String operationalNote = p[13].trim();

                flights.add(new Flight(flightId, flightNumber, origin, destination,
                        status, gate, runway, departureTime, arrivalTime, airline,
                        passengerCount, baggageCode, alertMessage, operationalNote));
            } catch (NumberFormatException e) {
                System.err.println("  [!] Parse error on line: " + line + " → " + e.getMessage());
            }
        }
        br.close();
        return flights;
    }
}
