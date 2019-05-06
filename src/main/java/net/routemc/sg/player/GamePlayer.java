package net.routemc.sg.player;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.trollcoding.requires.hotbar.ClickActionItem;
import me.trollcoding.requires.hotbar.HotbarAdapter;
import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import net.routemc.sg.chest.GameChest;
import net.routemc.sg.database.PlayerData;
import net.routemc.sg.hotbar.PlayerHotbar;
import net.routemc.sg.menu.SponsorMenu;
import net.routemc.sg.task.PlayerFreezingTask;
import net.routemc.sg.team.GameTeam;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.ItemUtility;
import net.routemc.sg.utility.PlayerUtility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GamePlayer {

    private final Player player;
    private PlayerData playerData;
    private boolean watching = false;
    private boolean voted = false;
    private List<ItemStack> sponsorItems;
    private SponsorMenu sponsorMenu;
    private Location respawnLocation;
    private PlayerFreezingTask playerFreezingTask;
    private GameChest openingChest;
    private PlayerHotbar hotbar;
    private boolean alive = true;
    private boolean pvpAlert = false;

    public GamePlayer(Player player) {
        this.player = player;
        this.sponsorItems = Lists.newArrayList();
    }

    public void enableWatching() {
        watching = true;
        //全員から見えないようにする
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            players.hidePlayer(player);
        }
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void disableWatching() {
        watching = false;
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            players.showPlayer(player);
        }
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public void respawn() {
        PlayerUtility.respawn(player);
    }

    public void teleport(Location location) {
        player.teleport(location);
    }

    public void resetPlayer() {
        player.getInventory().clear();
        EntityEquipment equipment = player.getEquipment();
        equipment.setHelmet(null);
        equipment.setChestplate(null);
        equipment.setLeggings(null);
        equipment.setBoots(null);
        player.setFoodLevel(20);
        player.setHealth(20D);
        PlayerUtility.clearEffects(player);
    }

    public void startFreezingTask(Location location){
        if(playerFreezingTask != null){
            playerFreezingTask.cancel();
        }
        playerFreezingTask = new PlayerFreezingTask(player, location);
        playerFreezingTask.runTaskTimer(RouteSG.getInstance(), 0, 3);
    }

    public void stopFreezingTask(){
        if(playerFreezingTask != null){
            playerFreezingTask.cancel();
        }
    }

    public void addItem(ItemStack itemStack){
        player.getInventory().addItem(itemStack);
    }

    public void sendSponsorItem(Material type, GamePlayer from){
        if(type == Material.ENDER_PEARL){
            addItem(new ItemStack(Material.ENDER_PEARL));
            from.getPlayerData().withdrawPoint(getCost(Material.ENDER_PEARL));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Ender Pearl" + ChatColor.WHITE + "!");
            sponsorItems.get(0).setType(Material.AIR);
        }
        if(type == Material.IRON_INGOT){
            addItem(new ItemStack(Material.IRON_INGOT));
            from.getPlayerData().withdrawPoint(getCost(Material.IRON_INGOT));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Iron Ingot" + ChatColor.WHITE + "!");
            sponsorItems.get(1).setType(Material.AIR);
        }
        if(type == Material.ARROW){
            addItem(new ItemStack(Material.ARROW, 5));
            from.getPlayerData().withdrawPoint(getCost(Material.ARROW));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Arrow of 5" + ChatColor.WHITE + "!");
            sponsorItems.get(2).setType(Material.AIR);
        }
        if(type == Material.EXP_BOTTLE){
            addItem(new ItemStack(Material.EXP_BOTTLE, 2));
            from.getPlayerData().withdrawPoint(getCost(Material.EXP_BOTTLE));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Exp bottle of 2" + ChatColor.WHITE + "!");
            sponsorItems.get(3).setType(Material.AIR);
        }
        if(type == Material.PORK){
            addItem(new ItemStack(Material.PORK));
            from.getPlayerData().withdrawPoint(getCost(Material.PORK));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Pork" + ChatColor.WHITE + "!");
            sponsorItems.get(4).setType(Material.AIR);
        }
        if(type == Material.BOW){
            addItem(new ItemStack(Material.BOW));
            from.getPlayerData().withdrawPoint(getCost(Material.BOW));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Bow" + ChatColor.WHITE + "!");
            sponsorItems.get(5).setType(Material.AIR);
        }
        if(type == Material.FLINT_AND_STEEL){
            addItem(ItemUtility.createFlintAndSteel());
            from.getPlayerData().withdrawPoint(getCost(Material.FLINT_AND_STEEL));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Flint and steel" + ChatColor.WHITE + "!");
            sponsorItems.get(6).setType(Material.AIR);
        }
        if(type == Material.MUSHROOM_SOUP){
            addItem(new ItemStack(Material.MUSHROOM_SOUP));
            from.getPlayerData().withdrawPoint(getCost(Material.MUSHROOM_SOUP));
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Sponsor item sent from " + RouteAPI.getColoredName(player) + ChatColor.WHITE + " for " + ChatColor.YELLOW + "Mushroom soup" + ChatColor.WHITE + "!");
            sponsorItems.get(7).setType(Material.AIR);
        }
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        ChatUtility.sendMessage(from,  ChatColor.WHITE + "Sponsor item sent.");
    }

    public void openSponsorMenu(Player player){
        if(sponsorMenu == null){
            sponsorMenu = new SponsorMenu(this);
        }
        sponsorMenu.openMenu(player);
    }

    public void resetSponsorItems(){
        sponsorItems.clear();
        sponsorItems.addAll(ItemUtility.getSponsorItems());
    }

    private int getCost(Material type){
        if(type == Material.ENDER_PEARL){
            return 150;
        }
        if(type == Material.IRON_INGOT){
            return 60;
        }
        if(type == Material.ARROW){
            return 50;
        }
        if(type == Material.EXP_BOTTLE){
            return 70;
        }
        if(type == Material.PORK){
            return 30;
        }
        if(type == Material.BOW){
            return 75;
        }
        if(type == Material.FLINT_AND_STEEL){
            return 75;
        }
        if(type == Material.MUSHROOM_SOUP){
            return 65;
        }
        return 0;
    }

    public void setHotbar(PlayerHotbar hotbar) {
        this.hotbar = hotbar;
        player.getInventory().clear();
        HotbarAdapter adapter = hotbar.getAdapter();
        ClickActionItem[] items = adapter.getItems();
        for(int i = 0; i < items.length; i++){
            if(items[i] != null) {
                player.getInventory().setItem(i, items[i].getItemStack());
            }
        }
        player.updateInventory();
    }

    public boolean isOnline(){
        return player.isOnline();
    }

    public void hidePlayer(GamePlayer other) {
        if (isOnline()) {
            player.hidePlayer(other.getPlayer());
        }
    }

    public void showPlayer(GamePlayer other) {
        if (isOnline()) {
            player.showPlayer(other.getPlayer());
        }
    }

    public void sendMessage(String s) {
        player.sendMessage(s);
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isAlive() {
        return alive;
    }

    public GameTeam getInTeam(){
        for(GameTeam gameTeam : RouteSG.getInstance().getTeamManager().getTeams()){
            if(gameTeam.getTeamPlayer(this) != null){
                return gameTeam;
            }
        }
        return null;
    }

    public boolean isInTeam(){
        return getInTeam() != null;
    }

    public String getSGName(){
        return getInTeam() == null ? player.getDisplayName() : getInTeam().getPrefix() + getName();
    }
}