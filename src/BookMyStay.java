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
        availability.put("Suite Room", 0);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }
}

class SearchService {

    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchRooms(Room[] rooms) {

        System.out.println("Available Rooms\n");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getType());

            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println();
            }
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Room[] rooms = {single, doubleRoom, suite};

        RoomInventory inventory = new RoomInventory();

        SearchService searchService = new SearchService(inventory);

        searchService.searchRooms(rooms);
    }
}