package net.hotsmc.sg.game;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.config.FileConfig;
import net.hotsmc.sg.utility.BlockUtility;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

@Getter
public class ChestManager implements Listener {

    private int maxItemSlot;
    private List<ItemStack> tier1Items;
    private List<ItemStack> tier2Items;
    private List<Location> openedChestLocations;
    private List<Location> tier2ChestLocations;

    public ChestManager() {
        maxItemSlot = new ConfigCursor(new FileConfig(HSG.getInstance(), "ChestConfig.yml"), "ChestConfig").getInt("MaxItemSlot");
        tier1Items = Lists.newArrayList();
        tier2Items = Lists.newArrayList();
        openedChestLocations = Lists.newArrayList();
        tier2ChestLocations = Lists.newArrayList();
        Bukkit.getPluginManager().registerEvents(this, HSG.getInstance());
    }

    public void fillChest(TierType type, Chest chest) {

        Inventory inventory = chest.getInventory();
        inventory.clear();
        Random r = new Random();
        int next;

        if (type == TierType.TIER1) {
            next = maxItemSlot;

            for (int i = 0; i < next; i++) {
                ItemStack item = tier1Items.get(r.nextInt(tier1Items.size()));
                int slot = r.nextInt(chest.getInventory().getSize());
                if (!inventory.contains(item)) {
                    inventory.setItem(slot, item);
                }
            }
            return;
        }


        if (type == TierType.TIER2) {

            next = maxItemSlot;

            for (int i = 0; i < next; i++) {
                ItemStack item = tier2Items.get(r.nextInt(tier2Items.size()));
                int slot = r.nextInt(chest.getInventory().getSize());
                if (!inventory.contains(item)) {
                    inventory.setItem(slot, item);
                }
            }
        }
    }


    public void fillDoubleChest(TierType type, DoubleChest doubleChest) {

        Random r = new Random();
        Inventory inventory = doubleChest.getInventory();
        int next;

        if (inventory instanceof DoubleChestInventory) {
            if (type == TierType.TIER1) {
                next = maxItemSlot * 2;

                for (int i = 0; i < next; i++) {
                    ItemStack item = tier1Items.get(r.nextInt(tier1Items.size()));
                    int slot = r.nextInt(doubleChest.getInventory().getSize());
                    if (!inventory.contains(item)) {
                        inventory.setItem(slot, item);
                    }
                }
            }
            return;
        }

        if (type == TierType.TIER2) {
            next = maxItemSlot * 2;

            for (int i = 0; i < next; i++) {
                ItemStack item = tier1Items.get(r.nextInt(tier2Items.size()));
                int slot = r.nextInt(doubleChest.getInventory().getSize());
                if (!inventory.contains(item)) {
                    inventory.setItem(slot, item);
                }
            }
        }
    }

    public void loadTierItems() {
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(HSG.getInstance(), "ChestItems.yml"), "ChestItems");
        tier1Items = configCursor.getItemStackList("Tier1");
        tier2Items = configCursor.getItemStackList("Tier2");
    }

    public void addTierItem(TierType type, Player player) {
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(HSG.getInstance(), "ChestItems.yml"), "ChestItems");
        if (type == TierType.TIER1) {
            List<ItemStack> itemStacks = configCursor.getItemStackList("Tier1");
            itemStacks.add(player.getItemInHand());
            configCursor.setItemStackList(itemStacks, "Tier1");
            configCursor.save();
            ChatUtility.sendMessage(player, ChatColor.GREEN + "You now have item has been saved to Tier1");
            return;
        }
        if (type == TierType.TIER2) {
            List<ItemStack> itemStacks = configCursor.getItemStackList("Tier2");
            itemStacks.add(player.getItemInHand());
            configCursor.setItemStackList(itemStacks, "Tier2");
            configCursor.save();
            ChatUtility.sendMessage(player, ChatColor.GREEN + "You now have item has been saved to Tier2");
        }
    }

    public void loadAllMapTier2Chest(MapData mapData) {
        for (Block b : BlockUtility.getEnderChests(mapData.getMin(), mapData.getMax(), Bukkit.getWorld(mapData.getName()))) {
            tier2ChestLocations.add(b.getLocation());
            b.setType(Material.CHEST);
        }
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        if (HSG.getGameTask().getState() == GameState.LiveGame || HSG.getGameTask().getState() == GameState.PreDeathMatch || HSG.getGameTask().getState() == GameState.DeathMatch) {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType() == Material.CHEST)) {
                BlockState blockState = event.getClickedBlock().getState();
                if(blockState instanceof DoubleChest){
                    DoubleChest doubleChest = (DoubleChest) blockState;
                    Location left = ((Chest) doubleChest.getLeftSide()).getLocation();
                    Location right = ((Chest) doubleChest.getRightSide()).getLocation();
                    if (!openedChestLocations.contains(left) &&!openedChestLocations.contains(right) ) {
                        openedChestLocations.add(left);
                        openedChestLocations.add(right);
                        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(event.getPlayer());
                        if(gamePlayer == null)return;
                        if (tier2ChestLocations.contains(left) && tier2ChestLocations.contains(right)) {
                            fillDoubleChest(TierType.TIER2, doubleChest);
                        } else {
                            fillDoubleChest(TierType.TIER1, doubleChest);
                        }
                        gamePlayer.getPlayerData().updateChests(1);
                    }
                    return;
                }
                if (blockState instanceof Chest) {
                    Chest chest = (Chest) blockState;
                    Location location = chest.getLocation();
                    if (!openedChestLocations.contains(location)) {
                        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(event.getPlayer());
                        if(gamePlayer == null)return;
                        if (tier2ChestLocations.contains(location)) {
                            fillChest(TierType.TIER2, chest);
                        } else {
                            fillChest(TierType.TIER1, chest);
                        }
                        gamePlayer.getPlayerData().updateChests(1);
                        openedChestLocations.add(location);
                    }
                }
            }
        }
    }
}

