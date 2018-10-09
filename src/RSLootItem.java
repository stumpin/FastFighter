/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/5/2018.
 */
public class RSLootItem {

    private final int id;
    private final boolean stackable;

    public RSLootItem(int id, boolean stackable) {
        this.id = id;
        this.stackable = stackable;
    }

    public boolean isStackable() {
        return stackable;
    }

    public int getId() {
        return id;
    }
}
