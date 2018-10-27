import com.sun.javafx.application.PlatformImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.util.Callback;
import xobot.script.Manifest;
import xobot.script.methods.NPCs;
import xobot.script.methods.tabs.Prayer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.NPC;
import xobot.script.wrappers.interactive.Player;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/4/2018.
 */
public class GUI extends Application {

    private FastFigher fighter;
    private boolean isFinished = false;
    private final ArrayList<RSTileBox> tiles = new ArrayList<>();
    private final ObservableList<String> loaded = FXCollections.observableArrayList(), selected = FXCollections.observableArrayList();
    private final ObservableList<Prayer.Prayers> prayers = FXCollections.observableArrayList();


    public GUI(FastFigher script) {
        this.fighter = script;
        //internally set up javaFX
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            System.setProperty("glass.win.uiScale", "100%");
            System.setProperty("glass.win.renderScale", "100%");
            return null;
        });

        PlatformImpl.startup(() -> { });
        Platform.runLater(() -> {
            start(new Stage());
            Platform.setImplicitExit(false);
        });
    }

    @Override
    public void start(Stage stage) {
        final TabPane tabPane = new TabPane(createGeneralTab(), createConfigTab(), createConstraintsTab());
        final BorderPane master = new BorderPane(tabPane);
        final Scene scene = new Scene(master, 500, 450);

        master.setTop(createMenu());
        master.setBottom(createStartBox(stage));

        try {
            scene.getStylesheets().add(new URL("https://pastebin.com/raw/CvjAGgUy").toString());
        } catch (Exception e) {
            System.out.println("Could not load GUI style sheet - using default styling");
            e.printStackTrace();
        }
        stage.setResizable(false);
        stage.setTitle("Fast Fighter");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenu() {
        final MenuItem load = new MenuItem("Load");
        load.setOnAction(action -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Fighter profile");
            fileChooser.setInitialDirectory(new File(fighter.getXobotPath() + "FastFighterProfiles"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized profiles", "*.serialized"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                //handle profile setting loading
            }
        });

        final MenuItem save = new MenuItem("Save");
        final Menu menu     = new Menu("File", null, load, save);
        return new MenuBar(menu);
    }

    private Tab createGeneralTab() {
        final GridPane general = new GridPane();
        final Tab generalTab   = new Tab("General", general);
        general.setPadding(new Insets(20, 20, 20, 20));
        general.setHgap(20);
        general.setVgap(20);
        generalTab.setClosable(false);

        final ListView loadedIds = new ListView(loaded);
        loadedIds.setPrefSize(200, 200);
        loadedIds.setEditable(false);
        loadedIds.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String token = (String) loadedIds.getSelectionModel().getSelectedItem();
                if (!selected.contains(token)) {
                    selected.add(token);
                }
            }
        });

        final ListView selectedIds = new ListView(selected);
        selectedIds.setPrefSize(200, 200);
        selectedIds.setEditable(false);
        selectedIds.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                int index = selectedIds.getSelectionModel().getSelectedIndex();
                if (index > -1) {
                    selected.remove(index);

                }
            }
        });

        final Button shifter = new Button();
        final Image image = loadResourceImage("https://i.imgur.com/DCDCRm8.png");
        if (image != null) {
            shifter.setGraphic(new ImageView(image));
        }
        shifter.setOnAction(action -> {
            loaded.forEach(id -> {
                if (!selected.contains(id)) {
                    selected.add(id);
                }
            });
        });

        final Button loader = new Button("Load Npcs");
        loader.setOnAction(action -> {
            loaded.clear();
            for (NPC npc : NPCs.getAll()) {
                String token = npc.getId() + " (" + npc.getName() + ")";
                if (!loaded.contains(token)) {
                    loaded.add(token);
                }
            }
        });

        final Button clearer = new Button("Clear Selection");
        clearer.setOnAction(action -> selected.clear());

        final HBox loadedBox = new HBox(25, new Label("Loaded List"));
        loadedBox.setAlignment(Pos.CENTER);

        final HBox selectedBox = new HBox(25, new Label("Selected List"));
        selectedBox.setAlignment(Pos.CENTER);

        final HBox loaderBox = new HBox(25, loader);
        loaderBox.setAlignment(Pos.CENTER);

        final HBox clearerBox = new HBox(25, clearer);
        clearerBox.setAlignment(Pos.CENTER);

        general.add(loadedBox, 0, 0);
        general.add(selectedBox, 2, 0);
        general.add(loadedIds, 0, 1);
        general.add(shifter, 1, 1);
        general.add(selectedIds, 2, 1);
        general.add(loaderBox, 0, 2);
        general.add(clearerBox, 2, 2);

        return generalTab;
    }

    private Tab createConfigTab() {
        final GridPane config = new GridPane();
        final Tab configTab   = new Tab("Configuration", config);
        config.setHgap(20);
        config.setVgap(20);
        config.setPadding(new Insets(20, 20, 20, 20));
        configTab.setClosable(false);

        final ListView prayerList = new ListView(prayers);
        prayerList.setPrefSize(200, 200);
        prayerList.setEditable(false);
        for (Prayer.Prayers prayer : Prayer.Prayers.values()) {
            prayers.add(prayer);
        }

        prayerList.setCellFactory(CheckBoxListCell.forListView(new Callback<Prayer.Prayers, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Prayer.Prayers item) {
                BooleanProperty observable = new SimpleBooleanProperty();

                for (Prayer.Prayers prayer : fighter.profile.getPrayers()) {
                    if (item.equals(prayer)) {
                        observable.set(true);
                    }
                }

                observable.addListener((o, previous, current) -> {
                    if (current) {
                        fighter.profile.getPrayers().add(item);
                    } else if (previous) {
                        fighter.profile.getPrayers().remove(item);
                    }
                });

                return observable;
            }
        }));

        final CheckBox checkBox = new CheckBox("Use potions");
        checkBox.setAlignment(Pos.CENTER_RIGHT);

        final HBox pray = new HBox(25, new Label("Selected Prayer(s)"));
        pray.setAlignment(Pos.CENTER);

        config.add(pray, 0, 0);
        config.add(prayerList, 0, 1);
        config.add(checkBox, 1, 0);

        return configTab;
    }

    private Tab createConstraintsTab() {
        final GridPane constraints = new GridPane();
        final Tab constraintsTab   = new Tab("Constraints", constraints);
        final GridPane gridView    = new GridPane();
        constraints.setPadding(new Insets(20, 20, 20, 20));
        constraints.setHgap(20);
        constraints.setVgap(20);

        Tile local = Player.getMyPlayer().getLocation();
        for (int x = -5, realX = 0; x < 6; x++, realX++) {
            for (int y = -5, realY = 0; y < 6; y++, realY++) {
                RSTileBox tile = new RSTileBox(new Tile(local.getX() + x, local.getY() - y));
                tiles.add(tile);
                gridView.add(tile, realX, realY);
            }
        }

        final Label lootRadius = new Label("Loot radius: 0");
        final Slider lootSlider = new Slider(0, 20, 1);
        lootSlider.setPrefWidth(225);
        lootSlider.setShowTickMarks(true);
        lootSlider.setShowTickLabels(true);
        lootSlider.setMajorTickUnit(10);
        lootSlider.setBlockIncrement(1);
        lootSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                lootRadius.textProperty().setValue("Loot radius: " + newValue.intValue());
                fighter.setRadius(newValue.intValue());
            }
        });


        final Label fightDelay = new Label("Fight delay: 0 ms");
        final Slider delaySlider = new Slider(0, 5000, 0);
        delaySlider.setPrefWidth(225);
        delaySlider.setShowTickMarks(true);
        delaySlider.setShowTickLabels(true);
        delaySlider.setMajorTickUnit(1000);
        delaySlider.setBlockIncrement(25);
        delaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fightDelay.textProperty().setValue("Fight delay: " + newValue.intValue() + " ms");
                fighter.setDelay(newValue.intValue());
            }
        });

        final HBox safeBox = new HBox(25, new Label("Safe spot(s)"));
        safeBox.setAlignment(Pos.CENTER);

        //final VBox other = new VBox(30, new CheckBox("Wait for loot to appear"), lootRadius, new Label("(Leave at 0 to ignore)"), slider);
        final VBox other = new VBox(25, lootRadius, new Label("(Leave at 0 to ignore)"), lootSlider, fightDelay, delaySlider);
        other.setAlignment(Pos.CENTER);

        constraints.add(safeBox, 0, 0);
        constraints.add(gridView, 0, 1);
        constraints.add(other, 1, 1);
        constraintsTab.setClosable(false);

        return constraintsTab;
    }

    private HBox createStartBox(Stage stage) {
        final Button start = new Button("Start Script");
        start.setOnAction(action -> {
            selected.forEach(token -> {
                int id = Integer.valueOf(token.replaceAll(" \\(.*\\)", ""));
                if (!fighter.getIds().contains(id)) {
                    fighter.getIds().add(id);
                }
            });
            tiles.forEach(tile -> {
                if (tile.isSelected()) {
                    fighter.getSafeSpots().add(tile.getRSTile());
                }
            });
            stage.close();
            isFinished = true;
        });
        final Label label = new Label("Version " + fighter.getClass().getAnnotation(Manifest.class).version());
        final HBox box = new HBox(25, label, start);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 5, 10, 0));
        box.setBackground(new Background(new BackgroundFill(Color.rgb(18, 18, 18), CornerRadii.EMPTY, Insets.EMPTY)));
        return box;
    }

    private Image loadResourceImage(String path) {
        try {
            return new Image(path, 20, 20, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Source: " + path + "\nCaused by: " + e.getCause().toString(), "Error loading image", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public ArrayList<RSTileBox> getTiles() {
        return tiles;
    }
}
