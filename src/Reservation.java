/**
 * ==========================================================
 * CLASS - Reservation
 * ==========================================================
 *
 * Use Case 5: Booking Request (FIFO)
 *
 * Represents a booking request made by a guest.
 *
 * @version 5.0
 */

public class Reservation {

    /** Name of the guest making the booking. */
    private String guestName;

    /** Requested room type. */
    private String roomType;

    /**
     * Creates a new booking request.
     *
     * @param guestName name of the guest
     * @param roomType requested room type
     */
    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    /** @return guest name */
    public String getGuestName() {
        return guestName;
    }

    /** @return requested room type */
    public String getRoomType() {
        return roomType;
    }
}