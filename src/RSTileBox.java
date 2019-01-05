import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import xobot.script.wrappers.Tile;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/7/2018.
 */
public class RSTileBox extends HBox {

    private final Tile tile;
    private boolean selected = false;
    private final Color blue = Color.rgb(40, 90, 163);

    public RSTileBox(Tile tile) {
        this.tile = tile;
        setPrefSize(20, 20);
        setBackground(new Background(new BackgroundFill(Color.DIMGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        setOnMouseEntered(event -> {
            if (!selected) {
                setBackground(new Background(new BackgroundFill(blue, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        setOnMouseExited(event -> {
            if (!selected) {
                setBackground(new Background(new BackgroundFill(Color.DIMGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        setOnMouseClicked(event -> {
            setBackground(new Background(new BackgroundFill(Color.DARKBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            selected = !selected;
        });
        setStyle("-fx-border-color: black;");
    }

    public boolean isSelected() {
        return selected;
    }

    public Tile getRSTile() {
        return tile;
    }
}
