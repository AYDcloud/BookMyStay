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
        availability.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        int current = availability.getOrDefault(roomType, 0);
        if (current > 0) {
            availability.put(roomType, current - 1);
        }
    }
}

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        queue.add(reservation);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class BookingService {

    private RoomInventory inventory;
    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processRequests(BookingRequestQueue requestQueue) {

        while (!requestQueue.isEmpty()) {

            Reservation request = requestQueue.getNextRequest();
            String roomType = request.getRoomType();

            if (inventory.getAvailability(roomType) > 0) {

                String roomId = roomType.replace(" ", "").toUpperCase() + "-" + UUID.randomUUID().toString().substring(0,5);

                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                inventory.decrement(roomType);

                System.out.println("Reservation Confirmed: " + request.getGuestName() +
                        " -> " + roomType + " | Room ID: " + roomId);

            } else {

                System.out.println("Reservation Failed: " + request.getGuestName() +
                        " -> No " + roomType + " available");
            }
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue requestQueue = new BookingRequestQueue();

        requestQueue.addRequest(new Reservation("Alice", single.getType()));
        requestQueue.addRequest(new Reservation("Bob", doubleRoom.getType()));
        requestQueue.addRequest(new Reservation("Charlie", single.getType()));
        requestQueue.addRequest(new Reservation("David", suite.getType()));

        BookingService bookingService = new BookingService(inventory);

        bookingService.processRequests(requestQueue);
    }
}