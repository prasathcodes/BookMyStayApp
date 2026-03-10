/**
 * ==========================================================
 * MAIN CLASS - UseCase4RoomSearch
 * ==========================================================
 *
 * Use Case 4: Room Search & Availability Check
 *
 * Demonstrates how guests can view available rooms
 * without modifying inventory data.
 *
 * @version 4.0
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();

        RoomSearchService searchService = new RoomSearchService();

        searchService.searchAvailableRooms(
                inventory,
                single,
                dbl,
                suite
        );
    }
}