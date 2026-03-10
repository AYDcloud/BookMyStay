import java.util.HashMap;

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

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price per night: $" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 100);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 180);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 350);
    }
}

class RoomInventory {

    private HashMap<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();

        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    public void displayInventory() {
        for (String roomType : availability.keySet()) {
            System.out.println(roomType + " Available: " + availability.get(roomType));
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();

        System.out.println("Room Details\n");

        single.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getType()));
        System.out.println();

        doubleRoom.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleRoom.getType()));
        System.out.println();

        suite.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getType()));
        System.out.println();

        System.out.println("Current Inventory");
        inventory.displayInventory();
    }
}