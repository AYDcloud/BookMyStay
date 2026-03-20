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
    public InvalidBookingException(String msg) {
        super(msg);
    }
}

class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 2);
        availability.put("Double Room", 2);
        availability.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, -1);
    }

    public void decrement(String type) throws InvalidBookingException {
        int count = availability.getOrDefault(type, -1);

        if (count < 0)
            throw new InvalidBookingException("Invalid room type");

        if (count == 0)
            throw new InvalidBookingException("No availability");

        availability.put(type, count - 1);
    }

    public void increment(String type) {
        availability.put(type, availability.getOrDefault(type, 0) + 1);
    }
}

class Reservation {
    private String id;
    private String guest;
    private String roomType;
    private String roomId;

    public Reservation(String guest, String roomType, String roomId) {
        this.id = UUID.randomUUID().toString().substring(0, 6);
        this.guest = guest;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getId() { return id; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
}

class BookingService {

    private RoomInventory inventory;
    private Map<String, Reservation> confirmed = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public Reservation book(String guest, String roomType) {

        try {
            inventory.decrement(roomType);

            String roomId = roomType.replace(" ", "").toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 4);

            Reservation r = new Reservation(guest, roomType, roomId);
            confirmed.put(r.getId(), r);

            System.out.println("Booked: " + guest + " | " + roomType +
                    " | RoomID: " + roomId + " | ResID: " + r.getId());

            return r;

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
            return null;
        }
    }

    public Reservation getReservation(String id) {
        return confirmed.get(id);
    }

    public void removeReservation(String id) {
        confirmed.remove(id);
    }
}

class CancellationService {

    private RoomInventory inventory;
    private BookingService bookingService;
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(RoomInventory inventory, BookingService bookingService) {
        this.inventory = inventory;
        this.bookingService = bookingService;
    }

    public void cancel(String reservationId) {

        Reservation r = bookingService.getReservation(reservationId);

        if (r == null) {
            System.out.println("Cancellation Failed: Reservation not found");
            return;
        }

        rollbackStack.push(r.getRoomId());

        inventory.increment(r.getRoomType());

        bookingService.removeReservation(reservationId);

        System.out.println("Cancelled Reservation: " + reservationId +
                " | Room Released: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack");
        for (String id : rollbackStack) {
            System.out.println(id);
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService booking = new BookingService(inventory);
        CancellationService cancelService = new CancellationService(inventory, booking);

        Reservation r1 = booking.book("Alice", "Single Room");
        Reservation r2 = booking.book("Bob", "Double Room");

        cancelService.cancel(r1.getId());
        cancelService.cancel("INVALID"); // should fail

        cancelService.showRollbackStack();
    }
}