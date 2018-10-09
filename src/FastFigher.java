import xobot.bot.Context;
import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.*;
import xobot.script.methods.tabs.Inventory;
import xobot.script.methods.tabs.Skills;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.Character;
import xobot.script.wrappers.interactive.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/3/2018.
 */

@Manifest(authors = { "Kumalo" }, name = "Fast Fighter", description = "Fights anything, loots anything", version = 1.4)

public class FastFigher extends ActiveScript implements PaintListener {

    private GUI gui;

    private final ArrayList<Tile> safeSpots = new ArrayList<>();
    private final ArrayList<Integer> ids = new ArrayList<>();

    private NPC target = null;
    private Timer scriptTimer;

    private final Color black = new Color(0, 0, 0, 127);
    private final Color blue  = new Color(40, 90, 163);
    private Color highlight   = Color.BLACK;

    private int eat = 75, radius = 0;

    private String status = "Setting up";

    private final RSLootItem[] items = {
            new RSLootItem(526, false),
            new RSLootItem(995, true)
    };

    private boolean boosters = true;
    private double version = this.getClass().getAnnotation(Manifest.class).version();

    @Override
    public boolean onStart() {
        final File profiles = new File(getXobotPath() + "FastFighterProfiles");

        if (!profiles.exists() && profiles.mkdir()) {
            System.out.println("Created profile folder");
        }

        gui = new GUI(this);
        scriptTimer = new Timer();

        //test
        return true;
    }

    @Override
    public int loop() {
        if (gui.isFinished() && Game.isLoggedIn()) {
            if (Skills.CONSTITUTION.getCurrentLevel() < eat) {
                Item food = Arrays.stream(Inventory.getItems()).filter(item -> item.hasAction("eat")).findAny().orElse(null);
                if (food != null) {
                    food.interact("eat");
                    Time.sleep(750);
                } else {
                    return terminate("Hp is lower than " + eat + ", and no food found");
                }
            } else if (Skills.PRAYER.getCurrentLevel() < 5) {
                System.out.println("praying");
            } else if (!safeSpots.isEmpty() && !safeSpots.contains(Players.getMyPlayer().getLocation())) {
                safeSpots.forEach(spot -> {
                    System.out.println(spot.toString());
                });
                safeSpots.sort(Comparator.comparingInt(Tile::getDistance));
                Tile safe = safeSpots.get(0);
                safe.walk();
                Time.sleep(() -> Players.getMyPlayer().getLocation().equals(safe), 8000);
            } else {
                final Character interacting = Players.getMyPlayer().getInteractingCharacter();
                if (interacting == null || interacting.isDead()) {
                    for (GroundItem groundItem : GroundItems.getAll()) {
                        for (RSLootItem loot : items) {
                            if (groundItem.getItem().getID() == loot.getId() && groundItem.isReachable()) {
                                boolean room = false;
                                if (!Inventory.isFull() || (loot.isStackable() && Inventory.Contains(loot.getId()))) {
                                    room = true;
                                } else {
                                    Item item = Arrays.stream(Inventory.getItems()).filter(vial -> vial.getID() == 229).findAny().orElse(Inventory.getItem());
                                    if (item != null) {
                                        status = "Making room";
                                        if (item.hasAction("Eat")) {
                                            item.interact("eat");
                                        } else {
                                            item.interact("drop");
                                        }
                                        if (Time.sleep(() -> !Inventory.isFull(), 1000)) room = true;
                                    }
                                }
                                int count = Inventory.getRealCount();
                                if (room) {
                                    groundItem.getItem().interact("take");
                                    Time.sleep(() -> Inventory.getRealCount() > count, 10000);
                                } else {
                                    System.out.println("Could not make room to loot " + groundItem.getItem().getDefinition().getName());
                                }
                            }
                        }
                    }
                    final NPC target = NPCs.getNearest(npc -> ids.contains(npc.getId()) && npc.isReachable() && !npc.isDead() && (!npc.isInCombat() || npc.getInteractingIndex() - 32768 == Context.client.getInteractingIndex()));
                    if (target != null) {
                        highlight = Color.ORANGE;
                        this.target = target;
                        target.interact("attack");
                        status = "Trying to interact";
                        Timer timer = new Timer(3000);
                        while (timer.isRunning()) {
                            Player local = Player.getMyPlayer();
                            if (local.getInteractingIndex() == target.getIndex() && local.getAnimation() != -1) {
                                status = "Interacted!";
                                highlight = Color.PINK;
                                return 100;
                            }
                            //edge case, someone else interacted with it before us
                            if (target.isInCombat() && target.getInteractingIndex() - 32768 != Context.client.getInteractingIndex()) {
                                status = "Too slow";
                                highlight = Color.red;
                                return 100;
                            }
                            Time.sleep(75);
                        }
                        status = "Failed to interact";
                        highlight = Color.RED;
                    }
                } else {
                    status = "Sleeping";
                }

                if (boosters) {
                    if (Skills.ATTACK.getCurrentLevel() < Math.ceil(Skills.ATTACK.getRealLevel() * 1.07)) {
                        Item potion = Inventory.getItem(9739, 9741, 9743, 9745, 2436, 145, 147, 149);
                        if (potion != null) {
                            status = "Potting attack";
                            potion.interact("drink");
                            Time.sleep(1200);
                        }
                    }

                    if (Skills.STRENGTH.getCurrentLevel() < Math.ceil(Skills.STRENGTH.getRealLevel() * 1.07)) {
                        Item potion = Inventory.getItem(9739, 9741, 9743, 9745, 2440, 157, 159, 161);
                        if (potion != null) {
                            status = "Potting strength";
                            potion.interact("drink");
                            Time.sleep(1200);
                        }
                    }

                    if (Skills.RANGE.getCurrentLevel() < Math.ceil(Skills.RANGE.getRealLevel() * 1.06)) {
                        Item potion = Inventory.getItem(2444, 169, 171, 173);
                        if (potion != null) {
                            status = "Potting range";
                            potion.interact("drink");
                            Time.sleep(1200);
                        }
                    }

                    if (Skills.MAGIC.getCurrentLevel() < Math.ceil(Skills.MAGIC.getRealLevel() * 1.02)) {
                        Item potion = Inventory.getItem(3040, 3042, 3044, 3046);
                        if (potion != null) {
                            status = "Potting magic";
                            potion.interact("drink");
                            Time.sleep(1200);
                        }
                    }
                }
            }
        }

        return 100;
    }

    @Override
    public void repaint(Graphics g) {
        if (gui.isFinished()) {
            g.setColor(black);
            g.fillRect(0, 305, 516, 33);
            g.setColor(Color.WHITE);
            g.drawString("Run time: " + scriptTimer.toElapsedString(), 20, 327);
            g.drawString("Fast Fighter V" + version, 200, 327);
            g.drawString("Status: " + status, 380, 327);
            if (target != null) target.getLocation().draw(g, target.isDead() ? Color.black : target.isInCombat() ? Color.GREEN : highlight);
            if (!safeSpots.isEmpty()) {
                safeSpots.forEach(tile -> {
                    if (tile.isOnScreen()) {
                        tile.draw(g, Color.blue);
                    }
                });
            }
        } else {
            gui.getTiles().forEach(tile -> {
                if (tile.isSelected()) {
                    tile.getRSTile().draw(g, Color.blue);
                } else if (tile.isHover()) {
                    tile.getRSTile().draw(g, blue);
                }
            });
            if (radius > 0) {
                g.setColor(Color.PINK);
                Tile local = Players.getMyPlayer().getLocation();

                drawDerived(g, local.derive(-radius, -radius));
                drawDerived(g, local.derive(-radius, radius));
                drawDerived(g, local.derive(radius, -radius));
                drawDerived(g, local.derive(radius, radius));
            }
        }
    }

    public void drawDerived(Graphics g, Tile tile) {
        Point d2 = Calculations.tileToMinimap(tile);
        tile.draw(g, Color.PINK);
        g.drawRect(d2.x, d2.y, 5, 5);
    }

    public String getXobotPath() {
        StringBuilder builder = new StringBuilder();
        String separator = System.getProperty("file.separator");
        builder.append(System.getProperty("user.home")).append(separator).append("Documents").append(separator).append("XoBot").append(separator);
        return builder.toString();
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public ArrayList<Tile> getSafeSpots() {
        return safeSpots;
    }

    public void setRadius(int lootRadius) {
        radius = lootRadius;
    }

    public int terminate(String reason) {
        System.out.println("TERMINATING: Cause - " + reason);
        Game.teleport("edgeville");
        return -1;
    }
}
