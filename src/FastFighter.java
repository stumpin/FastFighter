import xobot.bot.Context;
import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.*;
import xobot.script.methods.tabs.Inventory;
import xobot.script.methods.tabs.Prayer;
import xobot.script.methods.tabs.Skills;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.Character;
import xobot.script.wrappers.interactive.*;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/3/2018.
 */

@Manifest(authors = { "Jake" }, name = "Fast Fighter", description = "Fights anything, loots anything", version = 1.4)

public class FastFighter extends ActiveScript implements PaintListener {

    private GUI gui;
    private NPC target = null;
    protected FighterProfile profile;
    private Timer scriptTimer, renewalTimer;
    private final Color black = new Color(0, 0, 0, 127);
    private final Color blue  = new Color(40, 90, 163);
    private Color highlight = Color.BLACK;
    private final double version = this.getClass().getAnnotation(Manifest.class).version();
    private String status = "Setting up";

    @Override
    public boolean onStart() {
        final File profiles = new File(FighterProfile.getXobotPath() + "FastFighterProfiles");

        if (!profiles.exists() && profiles.mkdir()) {
            System.out.println("Created profile folder");
        }

        profile = new FighterProfile();
        gui = new GUI(this);
        scriptTimer = new Timer();
        renewalTimer = new Timer(1);

        return true;
    }

    @Override
    public int loop() {
        if (gui.isFinished() && Game.isLoggedIn()) {
            if (Skills.CONSTITUTION.getCurrentLevel() < profile.eat) {
                final Item food = Arrays.stream(Inventory.getItems()).filter(item -> item.hasAction("eat")).findAny().orElse(null);
                if (food != null) {
                    status = "eating food";
                    final Character interactor = Players.getMyPlayer().getInteractingCharacter();
                    food.interact("eat");
                    Time.sleep(1000);
                    if (interactor != null) {
                        interactor.interact("attack");
                        Time.sleep(() -> Players.getMyPlayer().getInteractingIndex() != -1, 3000);
                    }
                } else {
                    return terminate("Hp is lower than " + profile.eat + ", and no food found");
                }
            } else if (profile.renewals && !renewalTimer.isRunning()) {
                final Item potion = Inventory.getItem(21636, 21634, 21632, 21630);
                if (potion != null) {
                    status = "drinking renewal";
                    final Character interactor = Players.getMyPlayer().getInteractingCharacter();
                    potion.interact("drink");
                    renewalTimer.setEndIn(300000);
                    Time.sleep(1000);
                    if (interactor != null) {
                        interactor.interact("attack");
                        Time.sleep(() -> Players.getMyPlayer().getInteractingIndex() != -1, 3000);
                    }
                }
            } else if (Prayer.getPointPercentage() < 15) {
                final Item potion = Inventory.getItem(3024, 3026, 3028, 3030, 2434, 139, 141, 143);
                if (potion != null) {
                    status = "restoring prayer";
                    final Character interactor = Players.getMyPlayer().getInteractingCharacter();
                    potion.interact("drink");
                    Time.sleep(1000);
                    if (interactor != null) {
                        interactor.interact("attack");
                        Time.sleep(() -> Players.getMyPlayer().getInteractingIndex() != -1, 3000);
                    }
                } else {
                    return terminate("Prayer is lower than 15%, and no prayer/restore pots found");
                }
            } else if (!profile.safeSpots.isEmpty() && !containsTile(Players.getMyPlayer().getLocation(), profile.safeSpots)) {//!safeSpots.contains(Players.getMyPlayer().getLocation())) {
                profile.safeSpots.sort(Comparator.comparingInt(Tile::getDistance));
                final Tile safe = profile.safeSpots.get(0);
                final Character interactor = Players.getMyPlayer().getInteractingCharacter();
                safe.walk();
                if (Time.sleep(() -> Players.getMyPlayer().getLocation().equals(safe), 8000) && interactor != null) {
                    interactor.interact("attack");
                    Time.sleep(() -> Players.getMyPlayer().getInteractingIndex() != -1, 3000);
                }
            } else {
                final Character interacting = Players.getMyPlayer().getInteractingCharacter();

                if (interacting == null || (interacting.isDead() && Time.sleep(profile.delay))) {
                    if (profile.potions) {
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
                    for (GroundItem groundItem : GroundItems.getAll()) {
                        for (RSLootItem loot : profile.lootItems) {
                            if (groundItem.getItem().getID() == loot.getId() && groundItem.isReachable() && (profile.radius == 0 || groundItem.getDistance() <= profile.radius)) {
                                boolean room = false;
                                if (!Inventory.isFull() || (loot.isStackable() && Inventory.Contains(loot.getId()))) {
                                    room = true;
                                } else {
                                    Item item = Arrays.stream(Inventory.getItems()).filter(drop -> drop.getID() == 229 || drop.getDefinition().getName().matches(".*?\\(([1-4])\\)$")).findAny().orElse(null);
                                    //Item item = Arrays.stream(Inventory.getItems()).filter(vial -> vial.getID() == 229).findAny().orElse(Inventory.getItem(item -> item.getDefinition().getName().matches(".*?\\(([1-4])\\)$")));
                                    if (item != null) {
                                        status = "Making room";
                                        item.interact("drop");
                                        if (Time.sleep(() -> !Inventory.isFull(), 1000)) {
                                            room = true;
                                        }
                                    }
                                }
                                if (room) {
                                    int count = Inventory.getRealCount();
                                    status = "looting";
                                    groundItem.getItem().interact("take");
                                    Time.sleep(() -> Inventory.getRealCount() > count, 7000);
                                } else {
                                    System.out.println("Could not make room to loot " + groundItem.getItem().getDefinition().getName());
                                }
                            }
                        }
                    }

                    final NPC target = NPCs.getNearest(npc -> profile.ids.contains(npc.getId()) && npc.isReachable() && !npc.isDead() && (!npc.isInCombat() || npc.getInteractingIndex() - 32768 == Context.client.getInteractingIndex()));
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
                            //edge case, someone else interacted with it before us and it retaliated
                            if (target.isInCombat() && target.getInteractingIndex() - 32768 != Context.client.getInteractingIndex()) {
                                status = "Too slow";
                                highlight = Color.red;
                                return 100;
                            }
                            Time.sleep(75);
                        }
                        status = "Timed out";
                        highlight = Color.RED;
                    }
                } else {
                    status = "Sleeping";
                }
            }
            profile.prayers.forEach(prayer -> {
                if (!prayer.isActivated()) {
                    status = "Activating " + prayer.getName();
                    prayer.Activate();
                    Time.sleep(250);
                }
            });
        }

        return 100;
    }

    private boolean containsTile(Tile search, java.util.List<Tile> from) {
        for (Tile tile : from) {
            if (tile.equals(search)) {
                return true;
            }
        }
        return false;
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
            if (!profile.safeSpots.isEmpty()) {
                profile.safeSpots.forEach(tile -> {
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
            if (profile.radius > 0) {
                g.setColor(Color.PINK);
                Tile local = Players.getMyPlayer().getLocation();
                drawDerived(g, local.derive(-profile.radius, -profile.radius));
                drawDerived(g, local.derive(-profile.radius, profile.radius));
                drawDerived(g, local.derive(profile.radius, -profile.radius));
                drawDerived(g, local.derive(profile.radius, profile.radius));
            }
        }
    }


    private void drawDerived(Graphics g, Tile tile) {
        Point d2 = Calculations.tileToMinimap(tile);
        g.drawRect(d2.x, d2.y, 5, 5);
        tile.draw(g, Color.PINK);
    }

    private int terminate(String reason) {
        System.out.println("TERMINATING: Reason - " + reason);
        Game.teleport("edgeville");
        return -1;
    }
}
