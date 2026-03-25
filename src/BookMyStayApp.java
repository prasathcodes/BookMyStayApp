import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Book My Stay App
 *
 * UC12: Data Persistence & System Recovery
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("System Recovery");

        PersistenceService persistenceService = new PersistenceService();
        RoomInventory inventory = persistenceService.loadInventory();

        System.out.println();
        System.out.println("Current Inventory:");
        inventory.displayInventory();

        persistenceService.saveInventory(inventory);
    }
}

/**
 * Centralized inventory
 */
class RoomInventory implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public Map<String, Integer> getRoomAvailability() {
        return inventory;
    }

    public void displayInventory() {
        System.out.println("Single: " + inventory.getOrDefault("Single", 0));
        System.out.println("Double: " + inventory.getOrDefault("Double", 0));
        System.out.println("Suite: " + inventory.getOrDefault("Suite", 0));
    }
}

/**
 * Handles persistence and recovery of system state
 */
class PersistenceService {

    private static final String FILE_NAME = "inventory.dat";

    public void saveInventory(RoomInventory inventory) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(inventory);
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save inventory: " + e.getMessage());
        }
    }

    public RoomInventory loadInventory() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return new RoomInventory();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Object obj = in.readObject();

            if (obj instanceof RoomInventory) {
                System.out.println("Inventory restored successfully.");
                return (RoomInventory) obj;
            } else {
                System.out.println("No valid inventory data found. Starting fresh.");
                return new RoomInventory();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return new RoomInventory();
        }
    }
}