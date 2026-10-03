package dataset;

/**
 * POJO representing one flight record from airport_data.csv.
 */
public class Flight {
    private int    flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private String status;
    private String gate;
    private String runway;
    private String departureTime;
    private String arrivalTime;
    private String airline;
    private int    passengerCount;
    private String baggageCode;
    private String alertMessage;
    private String operationalNote;

    public Flight(int flightId, String flightNumber, String origin, String destination,
                  String status, String gate, String runway, String departureTime,
                  String arrivalTime, String airline, int passengerCount,
                  String baggageCode, String alertMessage, String operationalNote) {
        this.flightId       = flightId;
        this.flightNumber   = flightNumber;
        this.origin         = origin;
        this.destination    = destination;
        this.status         = status;
        this.gate           = gate;
        this.runway         = runway;
        this.departureTime  = departureTime;
        this.arrivalTime    = arrivalTime;
        this.airline        = airline;
        this.passengerCount = passengerCount;
        this.baggageCode    = baggageCode;
        this.alertMessage   = alertMessage;
        this.operationalNote = operationalNote;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getFlightId()       { return flightId; }
    public String getFlightNumber()   { return flightNumber; }
    public String getOrigin()         { return origin; }
    public String getDestination()    { return destination; }
    public String getStatus()         { return status; }
    public String getGate()           { return gate; }
    public String getRunway()         { return runway; }
    public String getDepartureTime()  { return departureTime; }
    public String getArrivalTime()    { return arrivalTime; }
    public String getAirline()        { return airline; }
    public int    getPassengerCount() { return passengerCount; }
    public String getBaggageCode()    { return baggageCode; }
    public String getAlertMessage()   { return alertMessage; }
    public String getOperationalNote(){ return operationalNote; }

    @Override
    public String toString() {
        return String.format("Flight{ID=%-2d  %-6s  %-10s -> %-10s  %-10s  Gate=%-4s  %s}",
                flightId, flightNumber, origin, destination, status, gate, airline);
    }
}
