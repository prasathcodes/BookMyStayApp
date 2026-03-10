/**
 * ==========================================================
 * MAIN CLASS - UseCase3InventorySetup
 * ==========================================================
 *
 * Initializes centralized room inventory.
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();

        inventory.displayInventory(single, dbl, suite);
    }
}