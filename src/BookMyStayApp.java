/**
 * ==========================================================
 * MAIN CLASS - UseCase5BookingRequestQueue
 * ==========================================================
 *
 * Use Case 5: Booking Request (First-Come-First-Served)
 *
 * Demonstrates how booking requests are accepted
 * and queued in a fair and predictable order.
 *
 * @version 5.0
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        // Display application header
        System.out.println("Booking Request Queue");

        // Initialize booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Create booking requests
        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Subha", "Double");
        Reservation r3 = new Reservation("Vanmathi", "Suite");

        // Add requests to queue
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Process requests in FIFO order
        while (bookingQueue.hasPendingRequests()) {

            Reservation current = bookingQueue.getNextRequest();

            System.out.println(
                    "Processing booking for Guest: "
                            + current.getGuestName()
                            + ", Room Type: "
                            + current.getRoomType()
            );
        }
    }
}