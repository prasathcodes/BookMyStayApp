/**
 * ==========================================================
 * MAIN CLASS - UseCase6RoomAllocation
 * ==========================================================
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 *
 * Demonstrates how booking requests are confirmed
 * and rooms are allocated safely.
 *
 * @version 6.0
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Room Allocation Processing");

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        while (bookingQueue.hasPendingRequests()) {

            Reservation reservation = bookingQueue.getNextRequest();

            allocationService.allocateRoom(reservation, inventory);
        }
    }
}