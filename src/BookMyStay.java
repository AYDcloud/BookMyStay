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

    public String getType() {
        return type;
    }
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

class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocated = new HashMap<>();
    private Map<String, Reservation> confirmed = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public Reservation book(String guest, String roomType) {

        if (inventory.getAvailability(roomType) <= 0) {
            System.out.println("No rooms available for " + guest);
            return null;
        }

        Reservation r = new Reservation(guest, roomType);

        String roomId = roomType.replace(" ", "").toUpperCase() + "-" +
                UUID.randomUUID().toString().substring(0, 4);

        allocated.putIfAbsent(roomType, new HashSet<>());
        allocated.get(roomType).add(roomId);

        inventory.decrement(roomType);
        confirmed.put(r.getReservationId(), r);

        System.out.println("Booked: " + guest + " | " + roomType +
                " | RoomID: " + roomId +
                " | ResID: " + r.getReservationId());

        return r;
    }
}

class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public double getCost() { return cost; }
    public String getName() { return name; }
}

class AddOnServiceManager {

    private Map<String, List<AddOnService>> servicesMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        servicesMap.putIfAbsent(reservationId, new ArrayList<>());
        servicesMap.get(reservationId).add(service);
    }

    public double calculateTotal(String reservationId) {
        double total = 0;
        List<AddOnService> list = servicesMap.getOrDefault(reservationId, new ArrayList<>());
        for (AddOnService s : list) {
            total += s.getCost();
        }
        return total;
    }

    public void displayServices(String reservationId) {
        List<AddOnService> list = servicesMap.getOrDefault(reservationId, new ArrayList<>());
        System.out.println("\nAdd-ons for " + reservationId);
        for (AddOnService s : list) {
            System.out.println(s.getName() + " - $" + s.getCost());
        }
        System.out.println("Total Add-on Cost: $" + calculateTotal(reservationId));
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService booking = new BookingService(inventory);
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        Reservation r1 = booking.book("Alice", "Single Room");
        Reservation r2 = booking.book("Bob", "Double Room");

        AddOnService wifi = new AddOnService("WiFi", 10);
        AddOnService breakfast = new AddOnService("Breakfast", 20);
        AddOnService spa = new AddOnService("Spa", 50);

        addOnManager.addService(r1.getReservationId(), wifi);
        addOnManager.addService(r1.getReservationId(), breakfast);

        addOnManager.addService(r2.getReservationId(), spa);

        addOnManager.displayServices(r1.getReservationId());
        addOnManager.displayServices(r2.getReservationId());
    }
}