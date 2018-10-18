package net.hotsmc.sg.game;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.config.FileConfig;
import net.hotsmc.sg.game.MapData;
import net.hotsmc.sg.utility.BlockUtility;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.PositionInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.material.EnderChest;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapManager {

    private JavaPlugin plugin;

    @Getter
    private List<MapData> loadedMapData;

    public MapManager(JavaPlugin plugin){
        this.plugin = plugin;
        this.loadedMapData = Lists.newArrayList();
    }

    /**
     * マップデータロード
     */
    public void load() {
        String dir = plugin.getDataFolder().getPath() +  "/maps/";
        File dir2 = new File(dir);
        String[] list = dir2.list();

        if (list != null) {
            String[] arrayOfString1;
            int j = (arrayOfString1 = list).length;
            for (int i = 0; i < j; i++) {
                String filename = arrayOfString1[i];
                File loadFile = new File(dir + filename);
                FileConfig fileConfig = new FileConfig(loadFile);
                ConfigCursor cursor = new ConfigCursor(fileConfig, "mapdata");
                String mapName = filename.substring(0, filename.indexOf(".yml"));
                loadedMapData.add(new MapData(mapName, cursor));
            }
        }
    }

    /**
     * マップ名からデータを返します
     * @param mapName
     * @return
     */
    public MapData getMapData(String mapName){
        for(MapData mapData : loadedMapData){
            if(mapData.getName().equals(mapName)){
                return mapData;
            }
        }
        return null;
    }

    /**
     * ロビー座標更新
     * @param player
     */
    public void updateLobbyLocation(Player player){
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(HSG.getInstance(), "GameConfig.yml"), "GameConfig");
        configCursor.setLocation("Lobby", new PositionInfo(player));
        configCursor.save();
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully updated the lobby location");
    }

    /**
     * 存在しているか
     * @param mapName
     * @return
     */
    public boolean existsMapData(String mapName){
        return new File(plugin.getDataFolder().getPath() +  "/maps/" + mapName + ".yml").exists();
    }

    /**
     * マップ用のデータファイルを作成
     * @param mapName
     * @param player
     */
    public void createMapData(String mapName, Player player) {
        if (existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data has already existed");
            return;
        }
        File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            ChatUtility.sendMessage(player, "Failed to create file of map data");
        }
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully created the '" + mapName + "'");
    }

    /**
     * 既定のスポーン位置を更新
     * @param mapName
     * @param player
     */
    public void updateDefaultSpawn(String mapName, Player player) {
        if (!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data not found");
            return;
        }
        File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
        PositionInfo positionInfo = new PositionInfo(player);
        configCursor.setLocation("DefaultSpawn", positionInfo);
        configCursor.save();
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully " + mapName + " of default spawn has been updated to " + positionInfo.locationFormat());
    }

    /**
     * 真ん中の座標を更新
     * @param mapName
     * @param player
     */
    public void updateCenterLocation(String mapName, Player player) {
        if (!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data not found");
            return;
        }
        File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
        PositionInfo positionInfo = new PositionInfo(player);
        configCursor.setLocation("CenterLocation", positionInfo);
        configCursor.save();
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully " + mapName + " of center location has been updated to " + positionInfo.locationFormat());
    }

    /**
     * 24箇所のスポーン位置を更新
     * @param mapName
     * @param player
     * @param number
     */
    public void updateSpawn(String mapName, Player player, int number){
        if(!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data not found");
            return;
        }
        if(number > 24){
            ChatUtility.sendMessage(player, ChatColor.RED + "Spawn location of maximum value is 24!");
        }else {
            File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
            ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
            PositionInfo positionInfo = new PositionInfo(player);
            configCursor.setLocation("Spawns.Spawn" + number, positionInfo);
            configCursor.save();
            ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully " + mapName + " of spawn " + number + " has been updated to " + positionInfo.locationFormat());
        }
    }

    public void updateDeathmatchSpawn(String mapName, Player player, int number){
        if(!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data not found");
            return;
        }
        if(number > 3){
            ChatUtility.sendMessage(player, ChatColor.RED + "Deathmatch spawn location of maximum value is 3!");
        }else {
            File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
            ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
            PositionInfo positionInfo = new PositionInfo(player);
            configCursor.setLocation("DeathmatchSpawn." + number, positionInfo);
            configCursor.save();
            ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully " + mapName + " of deathmatch spawn " + number + " has been updated to " + positionInfo.locationFormat());
        }
    }

    /**
     * マップ範囲の最小箇所を更新
     * @param mapName
     * @param player
     */
    public void updateMinLocation(String mapName, Player player){
        if(!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data hasn't found");
            return;
        }
        File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
        PositionInfo positionInfo = new PositionInfo(player);
        configCursor.setLocation("MinLocation", positionInfo);
        configCursor.save();
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully " + mapName + " of min location has been updated to " + positionInfo.locationFormat());
    }

    /**
     * マップ範囲の最大箇所を更新
     * @param mapName
     * @param player
     */
    public void updateMaxLocation(String mapName, Player player){
        if(!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data not found");
            return;
        }
        File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
        PositionInfo positionInfo = new PositionInfo(player);
        configCursor.setLocation("MaxLocation", positionInfo);
        configCursor.save();
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully " + mapName + " of max location has been updated to " + positionInfo.locationFormat());
    }

    /**
     *
     * @param mapName
     * @param player
     */
    public void scanAddAllChest(String mapName, Player player){
        if(!existsMapData(mapName)) {
            ChatUtility.sendMessage(player, ChatColor.RED + "That name of map data not found");
            return;
        }

        ChatUtility.sendMessage(player, ChatColor.YELLOW + "Scanning all chest and enderchest...");

        File file = new File(plugin.getDataFolder().getPath() + "/maps/" + mapName + ".yml");
        ConfigCursor configCursor = new ConfigCursor(new FileConfig(file), "mapdata");
        Location max = configCursor.getLocation("MaxLocation");
        Location min = configCursor.getLocation("MinLocation");
        World world = max.getWorld();

        //チェスト
        List<Block> chests = new ArrayList<>();
        for(Block block : BlockUtility.getChestBlocks(max, min, world)) {
            if (block.getType() == Material.CHEST) {
                chests.add(block);
            }
        }
        List<String> list1 = new ArrayList<>();
        for(Block chest : chests){
            Location location = chest.getLocation();
            BlockState state = chest.getState();
            final BlockFace face = ((org.bukkit.material.Chest)state.getData()).getFacing();
            list1.add(location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + face.name());
        }
        configCursor.set("ChestLocations", list1);

        //エンダーチェスト
        List<Block> enderchests = new ArrayList<>();
        for(Block block : BlockUtility.getEnderChestBlocks(max, min, world)) {
            if (block.getType() == Material.ENDER_CHEST) {
                enderchests.add(block);
            }
        }

        List<String> list2 = new ArrayList<>();
        for(Block ec : enderchests){
            Location location = ec.getLocation();
            BlockState state = ec.getState();
            final BlockFace face = ((org.bukkit.material.EnderChest)state.getData()).getFacing();
            list2.add(location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + face.name());
        }
        configCursor.set("EnderchestLocations", list2);

        configCursor.save();
        ChatUtility.sendMessage(player, ChatColor.GREEN + "Successfully scan with add all chest!!!!ﾏﾝｼﾞｨ卍卍卍卍卍卍卍卍卍卍卍卍！！！！！！！！！！！！！！");
    }
}
