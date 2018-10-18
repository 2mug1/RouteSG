package net.hotsmc.sg.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.config.FileConfig;
import net.hotsmc.sg.utility.BlockUtility;
import net.hotsmc.sg.utility.ChatUtility;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockAction;
import net.minecraft.server.v1_7_R4.TileEntityChest;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Getter
public class ChestManager implements Listener {

    private int maxItemSlot;
    private List<ItemStack> tier1Items;
    private List<ItemStack> tier2Items;

    private List<GameChest> chests;

    public ChestManager() {
        maxItemSlot = new ConfigCursor(new FileConfig(HSG.getInstance(), "ChestConfig.yml"), "ChestConfig").getInt("MaxItemSlot");
        tier1Items = Lists.newArrayList();
        tier2Items = Lists.newArrayList();
        chests = Lists.newArrayList();
        Bukkit.getPluginManager().registerEvents(this, HSG.getInstance());
    }

    public void refillChest(){
        for(GameChest chest : chests){
            chest.setOpened(false);
        }
    }

    public void loadAllChest(MapData mapData) {
        chests.clear();

        World world = mapData.getDefaultSpawn().getWorld();
        Location max = mapData.getMax();
        Location min = mapData.getMin();

        //Tier1読み込み
        for(Block block : BlockUtility.getChestBlocks(max, min, world)) {
            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block.getState();
                chests.add(new GameChest(false, chest.getLocation()));
            }
        }

        //Tier2読み込み
        for(Block block : BlockUtility.getEnderChestBlocks(max, min, world)) {
            if (block.getType() == Material.ENDER_CHEST) {
                block.setType(Material.CHEST);
                Chest chest = (Chest) block.getState();
                chests.add(new GameChest(true, chest.getLocation()));
            }
        }
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        if (gamePlayer.isWatching()) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR ||
                    event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                event.setCancelled(true);
                return;
            }
        }
        if (HSG.getGameTask().getState() == GameState.LiveGame || HSG.getGameTask().getState() == GameState.PreDeathmatch || HSG.getGameTask().getState() == GameState.DeathMatch) {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType() == Material.CHEST)) {
                Block block = event.getClickedBlock();
                event.setCancelled(true);
                for (GameChest gameChest : chests) {
                    if (gameChest.getLocation().equals(block.getLocation())) {
                        if (!gameChest.isOpened()) {
                            gameChest.setOpened(true);
                            if (gameChest.isTier2()) {
                                fillChest(TierType.TIER2, gameChest.getInventory());
                            } else {
                                fillChest(TierType.TIER1, gameChest.getInventory());
                            }
                        }
                        Location location = player.getLocation();
                        player.openInventory(gameChest.getInventory());
                        player.getWorld().playSound(location, Sound.CHEST_OPEN, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();
        if(event.getPlayer() instanceof Player) {
            for (GameChest gameChest : chests) {
                if (gameChest.getInventory().equals(inventory)) {
                    Player player = (Player) event.getPlayer();
                    player.getWorld().playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
                }
            }
        }
    }


    /**
     * チェストの処理
     *
     * @param type
     * @param inventory
     */
    public void fillChest(TierType type, Inventory inventory) {

        inventory.clear();
        Random r = new Random();
        int next;

        if (type == TierType.TIER1) {
            next = maxItemSlot;

            for (int i = 0; i < next; i++) {
                ItemStack item = tier1Items.get(r.nextInt(tier1Items.size()));
                int slot = r.nextInt(inventory.getSize());
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
                int slot = r.nextInt(inventory.getSize());
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
}