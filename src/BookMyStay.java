import java.util.*;

abstract class Room {
    protected String type;
    protected int beds;
    protected double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() { return type; }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 100); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 180); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 350); }
}

class RoomInventory {
    private HashMap<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 2);
        availability.put("Double Room", 2);
        availability.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availability.put(type, availability.get(type) - 1);
    }
}

class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = UUID.randomUUID().toString().substring(0, 6);
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }
}

class BookingReportService {

    public void displayAll(List<Reservation> history) {
        System.out.println("\nBooking History\n");
        for (Reservation r : history) {
            System.out.println(r.getReservationId() + " | " +
                    r.getGuestName() + " | " + r.getRoomType());
        }
    }

    public void summary(List<Reservation> history) {
        Map<String, Integer> count = new HashMap<>();

        for (Reservation r : history) {
            count.put(r.getRoomType(),
                    count.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("\nSummary Report\n");
        for (String type : count.keySet()) {
            System.out.println(type + " Bookings: " + count.get(type));
        }
    }
}

class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public Reservation book(String guest, String roomType) {

        if (inventory.getAvailability(roomType) <= 0) {
            System.out.println("No rooms available for " + guest);
            return null;
        }

        Reservation r = new Reservation(guest, roomType);

        inventory.decrement(roomType);
        history.add(r);

        System.out.println("Booked: " + guest + " -> " + roomType +
                " | ResID: " + r.getReservationId());

        return r;
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingService booking = new BookingService(inventory, history);
        BookingReportService report = new BookingReportService();

        booking.book("Alice", "Single Room");
        booking.book("Bob", "Double Room");
        booking.book("Charlie", "Single Room");

        report.displayAll(history.getAll());
        report.summary(history.getAll());
    }
}