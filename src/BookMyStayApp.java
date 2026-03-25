import java.util.*;

/**
 * Book My Stay App
 *
 * UC11: Concurrent Booking Simulation (Thread Safety)
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=== UC11 Concurrent Booking Simulation ===\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingHistory history = new BookingHistory();
        RoomAllocationService allocationService = new RoomAllocationService(history);

        queue.addRequest(new Reservation("User1", "Single"));
        queue.addRequest(new Reservation("User2", "Single"));
        queue.addRequest(new Reservation("User3", "Single"));
        queue.addRequest(new Reservation("User4", "Double"));
        queue.addRequest(new Reservation("User5", "Double"));
        queue.addRequest(new Reservation("User6", "Suite"));
        queue.addRequest(new Reservation("User7", "Suite"));
        queue.addRequest(new Reservation("User8", "Suite"));

        BookingWorker t1 = new BookingWorker("Thread-1", queue, allocationService, inventory);
        BookingWorker t2 = new BookingWorker("Thread-2", queue, allocationService, inventory);
        BookingWorker t3 = new BookingWorker("Thread-3", queue, allocationService, inventory);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        System.out.println("\nBooking History");
        for (Reservation reservation : history.getConfirmedBookings()) {
            System.out.println("Guest: " + reservation.getGuestName()
                    + ", Room Type: " + reservation.getRoomType()
                    + ", Room ID: " + reservation.getRoomId());
        }

        System.out.println("\nFinal Inventory");
        for (Map.Entry<String, Integer> entry : inventory.getRoomAvailability().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 400, 2500.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 750, 5000.0);
    }
}

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public synchronized int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, -1);
    }

    public synchronized void updateAvailability(String roomType, int count) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        if (count < 0) {
            throw new InvalidBookingException("Inventory cannot become negative for room type: " + roomType);
        }

        inventory.put(roomType, count);
    }

    public Map<String, Integer> getRoomAvailability() {
        return inventory;
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }
}

class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public synchronized void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public synchronized Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public synchronized boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

class BookingHistory {
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public synchronized void addBooking(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getConfirmedBookings() {
        return confirmedBookings;
    }
}

class RoomAllocationService {
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;
    private BookingHistory bookingHistory;

    public RoomAllocationService(BookingHistory bookingHistory) {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
        this.bookingHistory = bookingHistory;
    }

    public synchronized void allocateRoomThreadSafe(Reservation reservation, RoomInventory inventory)
            throws InvalidBookingException {

        String roomType = reservation.getRoomType();

        validateReservation(reservation, inventory);

        int available = inventory.getAvailability(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }

        String roomId = generateRoomId(roomType);

        if (allocatedRoomIds.contains(roomId)) {
            throw new InvalidBookingException("Duplicate room allocation: " + roomId);
        }

        allocatedRoomIds.add(roomId);

        assignedRoomsByType
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        inventory.updateAvailability(roomType, available - 1);

        reservation.setRoomId(roomId);
        bookingHistory.addBooking(reservation);

        System.out.println(Thread.currentThread().getName()
                + " -> Allocated " + roomId
                + " to " + reservation.getGuestName());
    }

    private void validateReservation(Reservation reservation, RoomInventory inventory) throws InvalidBookingException {
        if (reservation.getGuestName() == null || reservation.getGuestName().trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (reservation.getRoomType() == null || reservation.getRoomType().trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        if (!inventory.isValidRoomType(reservation.getRoomType())) {
            throw new InvalidBookingException("Room type does not exist: " + reservation.getRoomType());
        }
    }

    private String generateRoomId(String roomType) {
        Set<String> assigned = assignedRoomsByType.getOrDefault(roomType, new HashSet<>());
        int nextId = assigned.size() + 1;
        return roomType + "-" + nextId;
    }
}

class BookingWorker extends Thread {

    private BookingRequestQueue queue;
    private RoomAllocationService allocationService;
    private RoomInventory inventory;

    public BookingWorker(String name,
                         BookingRequestQueue queue,
                         RoomAllocationService allocationService,
                         RoomInventory inventory) {
        super(name);
        this.queue = queue;
        this.allocationService = allocationService;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation;

            synchronized (queue) {
                if (!queue.hasPendingRequests()) {
                    break;
                }
                reservation = queue.getNextRequest();
            }

            try {
                allocationService.allocateRoomThreadSafe(reservation, inventory);
            } catch (InvalidBookingException e) {
                System.out.println(getName() + " -> Booking failed: " + e.getMessage());
            }
        }
    }
}