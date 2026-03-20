import java.io.*;
import java.util.*;

abstract class Room implements Serializable {
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

class RoomInventory implements Serializable {
    private Map<String, Integer> availability = new HashMap<>();

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

    public Map<String, Integer> getAll() {
        return availability;
    }

    public void setAll(Map<String, Integer> data) {
        availability = data;
    }
}

class Reservation implements Serializable {
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

class BookingHistory implements Serializable {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }
}

class BookingService {
    private RoomInventory inventory;
    private BookingHistory history;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void book(String guest, String roomType) {
        if (inventory.getAvailability(roomType) <= 0) {
            System.out.println("No availability for " + guest);
            return;
        }

        inventory.decrement(roomType);
        Reservation r = new Reservation(guest, roomType);
        history.add(r);

        System.out.println("Booked: " + guest + " -> " + roomType + " | ID: " + r.getId());
    }
}

class PersistenceService {

    private static final String FILE = "system_state.ser";

    public void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("\nState saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving state.");
        }
    }

    public Object[] load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();
            System.out.println("State loaded successfully.");
            return new Object[]{inventory, history};
        } catch (Exception e) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();

        RoomInventory inventory;
        BookingHistory history;

        Object[] data = persistence.load();

        if (data != null) {
            inventory = (RoomInventory) data[0];
            history = (BookingHistory) data[1];
        } else {
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        BookingService booking = new BookingService(inventory, history);

        booking.book("Alice", "Single Room");
        booking.book("Bob", "Double Room");

        persistence.save(inventory, history);
    }
}