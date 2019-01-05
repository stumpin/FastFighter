import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/5/2018.
 */
public class RSLootItem implements Serializable {

    private transient IntegerProperty value = new SimpleIntegerProperty();
    private transient BooleanProperty stackable = new SimpleBooleanProperty();

    public RSLootItem(int id, boolean stackable) {
        valueProperty().set(id);
        stackableProperty().set(stackable);
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public BooleanProperty stackableProperty() {
        return stackable;
    }

    public int getId() {
        return valueProperty().get();
    }

    public boolean isStackable() {
        return stackableProperty().get();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(value.getValue());
        stream.writeBoolean(stackable.getValue());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        value = new SimpleIntegerProperty(stream.readInt());
        stackable = new SimpleBooleanProperty(stream.readBoolean());
    }
}
