import java.util.HashMap;
import java.util.Map;

/**
 * ==========================================================
 * CLASS - RoomInventory
 * ==========================================================
 *
 * Use Case 3: Centralized Room Inventory Management
 *
 * Manages room availability using a HashMap.
 */

public class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    // 🔹 Needed for UC4 search service
    public Map<String, Integer> getRoomAvailability() {
        return inventory;
    }

    public void displayInventory(Room single, Room dbl, Room suite) {

        System.out.println("Hotel Room Inventory Status\n");

        System.out.println("Single Room:");
        single.displayRoomDetails();
        System.out.println("Available Rooms: " + getAvailability("Single"));

        System.out.println();

        System.out.println("Double Room:");
        dbl.displayRoomDetails();
        System.out.println("Available Rooms: " + getAvailability("Double"));

        System.out.println();

        System.out.println("Suite Room:");
        suite.displayRoomDetails();
        System.out.println("Available Rooms: " + getAvailability("Suite"));
    }
}