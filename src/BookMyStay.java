import java.util.*;

abstract class Room {
    protected String type;

    public Room(String type) {
        this.type = type;
    }

    public String getType() { return type; }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room"); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room"); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room"); }
}

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 1);
        availability.put("Double Room", 1);
        availability.put("Suite Room", 0);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, -1);
    }

    public void decrement(String type) throws InvalidBookingException {
        int count = availability.getOrDefault(type, -1);

        if (count < 0)
            throw new InvalidBookingException("Invalid room type: " + type);

        if (count == 0)
            throw new InvalidBookingException("No availability for " + type);

        availability.put(type, count - 1);
    }
}

class Reservation {
    private String id;
    private String guest;
    private String roomType;

    public Reservation(String guest, String roomType) {
        this.id = UUID.randomUUID().toString().substring(0, 6);
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getId() { return id; }
    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
}

class BookingService {

    private RoomInventory inventory;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void book(String guest, String roomType) {

        try {
            if (roomType == null || roomType.isEmpty())
                throw new InvalidBookingException("Room type cannot be empty");

            inventory.decrement(roomType);

            Reservation r = new Reservation(guest, roomType);

            System.out.println("Booking Confirmed: " + guest +
                    " -> " + roomType + " | ID: " + r.getId());

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService booking = new BookingService(inventory);

        booking.book("Alice", "Single Room");   // valid
        booking.book("Bob", "Suite Room");      // no availability
        booking.book("Charlie", "Penthouse");   // invalid type
        booking.book("David", "");              // invalid input
    }
}