import com.sun.javafx.application.PlatformImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;

import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javafx.util.StringConverter;
import xobot.script.Manifest;
import xobot.script.methods.NPCs;
import xobot.script.methods.tabs.Prayer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.NPC;
import xobot.script.wrappers.interactive.Player;

import java.io.File;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Optional;


/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/4/2018.
 */
public class GUI extends Application {

    private FastFighter fighter;
    private boolean isFinished = false;

    private final ArrayList<RSTileBox> tiles = new ArrayList<>();
    private final ObservableList<String> loaded = FXCollections.observableArrayList();
    private final ObservableList<Integer> selected = FXCollections.observableArrayList();
    private final ObservableList<Prayer.Prayers> prayers = FXCollections.observableArrayList();

    private ListView prayerList;
    private TableView<RSLootItem> loots;

    private Label lootRadius, fightDelay, eatAt;
    private Slider lootSlider, delaySlider, eatSlider;
    private CheckBox potions, renewals;

    public GUI(FastFighter script) {
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
        final TabPane tabPane = new TabPane(createSelectionTab(), createConfigTab(), createConstraintsTab());
        final BorderPane master = new BorderPane(tabPane);
        final Scene scene = new Scene(master, 500, 450);

        master.setTop(createMenu());
        master.setBottom(createStartBox(stage));

        try {
            scene.getStylesheets().add(new URL("https://pastebin.com/raw/CvjAGgUy").toString());
        } catch (Exception e) {
            System.out.println("Could not load style sheet - using default styling");
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
            fileChooser.setInitialDirectory(new File(FighterProfile.getXobotPath() + "FastFighterProfiles"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized profiles", "*.serialized"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                fighter.profile = FighterProfile.loadProfile(selectedFile);

                if (fighter.profile != null) {
                    fighter.profile.ids.forEach(integer -> {
                        if (!selected.contains(integer)) {
                            selected.add(integer);
                        }
                    });

                    prayerList.refresh();

                    lootSlider.setValue(fighter.profile.radius);
                    lootRadius.textProperty().setValue("Loot radius: " + lootSlider.valueProperty().intValue());

                    delaySlider.setValue(fighter.profile.delay);
                    fightDelay.textProperty().setValue("Fight delay: " + delaySlider.valueProperty().intValue() + " ms");

                    eatSlider.setValue(fighter.profile.eat);
                    eatAt.textProperty().setValue("Eating at: " + eatSlider.valueProperty().intValue() + " hp");

                    potions.setSelected(fighter.profile.potions);
                    renewals.setSelected(fighter.profile.renewals);

                    loots.getItems().clear();
                    loots.getItems().addAll(fighter.profile.lootItems);
                    loots.refresh();
                }
            }
        });

        final MenuItem save = new MenuItem("Save");
        save.setOnAction(action -> {
            TextInputDialog steamIdDialog = new TextInputDialog();
            steamIdDialog.setTitle("Profile Saver");
            steamIdDialog.setHeaderText(null);
            steamIdDialog.setContentText("Enter profile name:");

            final Optional<String> name = steamIdDialog.showAndWait();
            if (name.isPresent() && name.get().length() > 0) {
                updateSafeSpots();
                fighter.profile.lootItems.clear();
                updateLoots();
                FighterProfile.dumpProfile(fighter.profile, name.get());
            }
        });
        final Menu menu = new Menu("Presets", null, load, save);
        return new MenuBar(menu);
    }

    private Tab createSelectionTab() {
        final GridPane selection = new GridPane();
        final Tab selectionTab = new Tab("Selection", selection);
        selection.setPadding(new Insets(20, 20, 20, 20));
        selection.setHgap(20);
        selection.setVgap(20);
        selectionTab.setClosable(false);

        final ListView loadedIds = new ListView(loaded);
        loadedIds.setPrefSize(200, 200);
        loadedIds.setEditable(false);
        loadedIds.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                final String token = (String) loadedIds.getSelectionModel().getSelectedItem();
                final int stripped = Integer.valueOf(token.replaceAll(" \\(.*\\)", ""));
                if (!selected.contains(stripped)) {
                    selected.add(stripped);
                    fighter.profile.ids.add(stripped);
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
                    fighter.profile.ids.remove(index);
                }
            }
        });

        final Button shifter = new Button();
        final Image image = loadResourceImage("https://i.imgur.com/DCDCRm8.png");
        if (image != null) {
            shifter.setGraphic(new ImageView(image));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error loading https://i.imgur.com/DCDCRm8.png");
            alert.showAndWait().filter(response -> response == ButtonType.OK);
        }

        shifter.setOnAction(action -> {
            loaded.forEach(id -> {
                int stripped = Integer.valueOf(id.replaceAll(" \\(.*\\)", ""));
                if (!selected.contains(stripped)) {
                    selected.add(stripped);
                    fighter.profile.ids.add(stripped);
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
        clearer.setOnAction(action -> {
            selected.clear();
            fighter.profile.ids.clear();
        });

        final HBox loadedBox = new HBox(25, new Label("Loaded List"));
        loadedBox.setAlignment(Pos.CENTER);

        final HBox selectedBox = new HBox(25, new Label("Selected List"));
        selectedBox.setAlignment(Pos.CENTER);

        final HBox loaderBox = new HBox(25, loader);
        loaderBox.setAlignment(Pos.CENTER);

        final HBox clearerBox = new HBox(25, clearer);
        clearerBox.setAlignment(Pos.CENTER);

        selection.add(loadedBox, 0, 0);
        selection.add(selectedBox, 2, 0);
        selection.add(loadedIds, 0, 1);
        selection.add(shifter, 1, 1);
        selection.add(selectedIds, 2, 1);
        selection.add(loaderBox, 0, 2);
        selection.add(clearerBox, 2, 2);

        return selectionTab;
    }

    private Tab createConfigTab() {
        final GridPane config = new GridPane();
        final Tab configTab = new Tab("Configuration", config);
        config.setHgap(20);
        config.setVgap(20);
        config.setPadding(new Insets(10, 10, 10, 10));
        configTab.setClosable(false);

        prayerList = new ListView(prayers);
        prayerList.setEditable(false);

        for (Prayer.Prayers prayer : Prayer.Prayers.values()) {
            prayers.add(prayer);
        }

        prayerList.setCellFactory(CheckBoxListCell.forListView(new Callback<Prayer.Prayers, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Prayer.Prayers item) {
                BooleanProperty observable = new SimpleBooleanProperty();

                for (Prayer.Prayers prayer : fighter.profile.prayers) {
                    if (item.equals(prayer)) {
                        observable.set(true);
                    }
                }

                observable.addListener((o, previous, current) -> {
                    if (current) {
                        fighter.profile.prayers.add(item);
                    } else if (previous) {
                        fighter.profile.prayers.remove(item);
                    }
                });

                return observable;
            }
        }));

        potions = new CheckBox("Use boosters");
        potions.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                fighter.profile.potions = newValue;
            }
        });

        renewals = new CheckBox("Use renewals");
        renewals.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                fighter.profile.renewals = newValue;
            }
        });
        loots = new TableView<>();
        loots.getSelectionModel().setCellSelectionEnabled(true);
        loots.setEditable(true);
        loots.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loots.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                final TablePosition pos = loots.getFocusModel().getFocusedCell();
                if (event.getCode() == KeyCode.SHIFT) {
                    if (pos.getRow() == loots.getItems().size() - 1) {
                        loots.getSelectionModel().clearSelection();
                        final RSLootItem data = new RSLootItem(0, false);
                        loots.getItems().add(data);
                    }
                } else if (event.getCode() == KeyCode.DELETE && loots.getItems().size() > 1) {
                    final RSLootItem selected = loots.getSelectionModel().getSelectedItem();
                    loots.getItems().remove(selected);
                }
            }
        });

        final TableColumn<RSLootItem, Number> id = new TableColumn<>("Id");
        id.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        id.setCellFactory(createNumberCellFactory());
        id.setEditable(true);

        final TableColumn<RSLootItem, Boolean> stackable = new TableColumn<>("Stackable");
        stackable.setCellValueFactory(cellData -> cellData.getValue().stackableProperty());
        stackable.setCellFactory(param -> new CheckBoxTableCell<>());
        stackable.setEditable(true);
        loots.getColumns().addAll(id, stackable);

        final ObservableList<RSLootItem> items = FXCollections.observableArrayList(new RSLootItem(0, true));
        loots.setItems(items);

        eatAt = new Label("Eating at: 50 hp");
        eatSlider = new Slider(1, 98, 50);
        eatSlider.setPrefWidth(225);
        eatSlider.setShowTickMarks(true);
        eatSlider.setShowTickLabels(true);
        eatSlider.setMajorTickUnit(20);
        eatSlider.setBlockIncrement(1);
        eatSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                eatAt.textProperty().setValue("Eating at: " + newValue.intValue() + " hp");
                fighter.profile.eat = newValue.intValue();
            }
        });

        final VBox left = new VBox(15, new Label("Selected Prayer(s)"), prayerList, new HBox(10, potions, renewals));
        left.setAlignment(Pos.CENTER);

        final VBox right = new VBox(15, new Label("Loot table"), loots, eatAt, eatSlider);
        right.setAlignment(Pos.CENTER);

        config.add(left, 0, 0);
        config.add(right, 1, 0);

        return configTab;
    }

    private Tab createConstraintsTab() {
        final GridPane constraints = new GridPane();
        final Tab constraintsTab = new Tab("Constraints", constraints);
        final GridPane gridView = new GridPane();

        constraints.setPadding(new Insets(20, 20, 20, 20));
        constraints.setHgap(20);
        constraints.setVgap(20);

        final Tile local = Player.getMyPlayer().getLocation();
        for (int x = -5, realX = 0; x < 6; x++, realX++) {
            for (int y = -5, realY = 0; y < 6; y++, realY++) {
                final RSTileBox tile = new RSTileBox(new Tile(local.getX() + x, local.getY() - y));
                tiles.add(tile);
                gridView.add(tile, realX, realY);
            }
        }
        lootRadius = new Label("Loot radius: 0");
        lootSlider = new Slider(0, 20, 1);
        lootSlider.setPrefWidth(225);
        lootSlider.setShowTickMarks(true);
        lootSlider.setShowTickLabels(true);
        lootSlider.setMajorTickUnit(10);
        lootSlider.setBlockIncrement(1);
        lootSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                lootRadius.textProperty().setValue("Loot radius: " + newValue.intValue());
                fighter.profile.radius = newValue.intValue();
            }
        });

        fightDelay = new Label("Fight delay: 0 ms");
        delaySlider = new Slider(0, 5000, 0);
        delaySlider.setPrefWidth(225);
        delaySlider.setShowTickMarks(true);
        delaySlider.setShowTickLabels(true);
        delaySlider.setMajorTickUnit(1000);
        delaySlider.setBlockIncrement(25);
        delaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fightDelay.textProperty().setValue("Fight delay: " + newValue.intValue() + " ms");
                fighter.profile.delay = newValue.intValue();
            }
        });

        final HBox safeBox = new HBox(25, new Label("Safe spot(s)"));
        safeBox.setAlignment(Pos.CENTER);

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
            updateSafeSpots();
            updateLoots();

            for (RSLootItem lootItem : fighter.profile.lootItems) {
                System.out.println(lootItem.getId());
            }

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
        }
        return null;
    }

    private Callback<TableColumn<RSLootItem, Number>, TableCell<RSLootItem, Number>>  createNumberCellFactory() {
        return TextFieldTableCell.forTableColumn(new StringConverter<Number>() {

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
            @Override
            public String toString(Number object) {
                return object.toString();
            }
        });
    }

    private void updateSafeSpots() {
        tiles.forEach(tile -> {
            if (tile.isSelected()) {
                fighter.profile.safeSpots.add(tile.getRSTile());
            }
        });
    }

    private void updateLoots() {
        fighter.profile.lootItems.clear();
        loots.getItems().forEach(item -> {
            if (item.getId() > 0) {
                fighter.profile.lootItems.add(item);
            }
        });
    }

    public boolean isFinished() {
        return isFinished;
    }

    public ArrayList<RSTileBox> getTiles() {
        return tiles;
    }
}
