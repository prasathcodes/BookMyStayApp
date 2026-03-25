import java.util.*;

/**
 * Book My Stay App
 *
 * UC9: Error Handling & Validation
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Book My Stay App - UC9 Error Handling & Validation\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        BookingHistory bookingHistory = new BookingHistory();
        RoomAllocationService allocationService = new RoomAllocationService(bookingHistory);

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Double"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));
        bookingQueue.addRequest(new Reservation("Rahul", "Deluxe")); // invalid room type

        while (bookingQueue.hasPendingRequests()) {
            Reservation reservation = bookingQueue.getNextRequest();

            try {
                allocationService.allocateRoom(reservation, inventory);
            } catch (InvalidBookingException e) {
                System.out.println("Booking failed for Guest: "
                        + reservation.getGuestName()
                        + " -> " + e.getMessage());
            }
        }

        System.out.println();

        BookingReportService reportService = new BookingReportService();
        reportService.displayBookingHistory(bookingHistory);

        System.out.println();
        reportService.displaySummaryReport(bookingHistory);
    }
}

/**
 * Custom exception for invalid booking scenarios
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * Abstract Room
 */
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

/**
 * Centralized inventory
 */
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, -1);
    }

    public void updateAvailability(String roomType, int count) throws InvalidBookingException {
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

/**
 * Reservation request / confirmed booking data
 */
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

/**
 * FIFO booking queue
 */
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

/**
 * Stores confirmed bookings in insertion order
 */
class BookingHistory {
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addBooking(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getConfirmedBookings() {
        return confirmedBookings;
    }
}

/**
 * Allocation service with validation
 */
class RoomAllocationService {
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;
    private BookingHistory bookingHistory;

    public RoomAllocationService(BookingHistory bookingHistory) {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
        this.bookingHistory = bookingHistory;
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) throws InvalidBookingException {
        String roomType = reservation.getRoomType();

        validateReservation(reservation, inventory);

        int available = inventory.getAvailability(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for room type: " + roomType);
        }

        String roomId = generateRoomId(roomType);

        if (allocatedRoomIds.contains(roomId)) {
            throw new InvalidBookingException("Duplicate room allocation detected for room ID: " + roomId);
        }

        allocatedRoomIds.add(roomId);

        assignedRoomsByType
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        inventory.updateAvailability(roomType, available - 1);

        reservation.setRoomId(roomId);
        bookingHistory.addBooking(reservation);

        System.out.println("Booking confirmed for Guest: "
                + reservation.getGuestName()
                + ", Room ID: "
                + roomId);
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

/**
 * Generates booking reports from stored history
 */
class BookingReportService {

    public void displayBookingHistory(BookingHistory bookingHistory) {
        System.out.println("Booking History");

        List<Reservation> bookings = bookingHistory.getConfirmedBookings();

        if (bookings.isEmpty()) {
            System.out.println("No confirmed bookings found.");
            return;
        }

        for (Reservation reservation : bookings) {
            System.out.println("Guest: " + reservation.getGuestName()
                    + ", Room Type: " + reservation.getRoomType()
                    + ", Room ID: " + reservation.getRoomId());
        }
    }

    public void displaySummaryReport(BookingHistory bookingHistory) {
        System.out.println("Booking Summary Report");

        List<Reservation> bookings = bookingHistory.getConfirmedBookings();
        System.out.println("Total Confirmed Bookings: " + bookings.size());

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation reservation : bookings) {
            String roomType = reservation.getRoomType();
            roomTypeCount.put(roomType, roomTypeCount.getOrDefault(roomType, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Booked: " + entry.getValue());
        }
    }
}