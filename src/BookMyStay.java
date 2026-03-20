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

class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 2);
        availability.put("Double Room", 1);
        availability.put("Suite Room", 1);
    }

    public synchronized boolean allocate(String type) {
        int count = availability.getOrDefault(type, 0);

        if (count <= 0) return false;

        availability.put(type, count - 1);
        return true;
    }

    public synchronized int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

class Reservation {
    private String guest;
    private String roomType;

    public Reservation(String guest, String roomType) {
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void add(Reservation r) {
        queue.add(r);
    }

    public synchronized Reservation poll() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

class BookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingRequestQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    public void run() {
        while (true) {

            Reservation r;

            synchronized (queue) {
                if (queue.isEmpty()) break;
                r = queue.poll();
            }

            if (r != null) {
                boolean success = inventory.allocate(r.getRoomType());

                if (success) {
                    System.out.println(Thread.currentThread().getName() +
                            " booked " + r.getGuest() +
                            " -> " + r.getRoomType());
                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " failed for " + r.getGuest() +
                            " (No availability)");
                }
            }
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Single Room"));
        queue.add(new Reservation("Charlie", "Single Room"));
        queue.add(new Reservation("David", "Double Room"));
        queue.add(new Reservation("Eve", "Suite Room"));

        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "T1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "T2");

        t1.start();
        t2.start();
    }
}