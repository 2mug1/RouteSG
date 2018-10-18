package net.hotsmc.sg.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.badlion.gspigot.FakeMultiBlockChange;
import net.hotsmc.core.ServerInfo;
import net.hotsmc.core.ServerType;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.utility.PositionInfo;
import net.hotsmc.sg.utility.WorldDataUtility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public class MapData {

    private String name;

    private ConfigCursor cursor;

    private Location defaultSpawn;
    private Location centerLocation;
    private Location min;
    private Location max;
    private List<Location> spawns;
    private List<Location> deathmatchSpawns;
    private List<Block> chests;
    private List<Block> enderchests;

    public MapData(String name, ConfigCursor cursor){
        this.name = name;
        this.cursor = cursor;
    }

    private List<Location> getSpawnLocations() {
        List<Location> locations = new ArrayList<>();
        int n = 1;
        for (int i = 0; i < 24; i++) {
            Location location = cursor.getLocation("Spawns.Spawn" + n);
            locations.add(location);
            n++;
        }
        return locations;
    }

    private List<Location> getDeathmatchLocations() {
        List<Location> locations = new ArrayList<>();
        int n = 1;
        for (int i = 0; i < 3; i++) {
            Location location = cursor.getLocation("DeathmatchSpawn." + n);
            locations.add(location);
            n++;
        }
        return locations;
    }

    private List<Block> getChestBlockData(){
        List<Block> blocks = new ArrayList<>();
        for(String string : cursor.getStringList("ChestLocations")){
            String[] data = string.split(":");
            blocks.add(Bukkit.getWorld(data[0]).getBlockAt(
                    new Location(Bukkit.getWorld(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]))));
        }
        return blocks;
    }

    private List<Block> getEnderChestBlockData(){
        List<Block> blocks = new ArrayList<>();
        for(String string : cursor.getStringList("EnderchestLocations")){
            String[] data = string.split(":");
            blocks.add(Bukkit.getWorld(data[0]).getBlockAt(
                    new Location(Bukkit.getWorld(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]))));
        }
        return blocks;
    }


    public void loadWorld(){
        WorldDataUtility.copyWorld(new File(HSG.getInstance().getDataFolder().getAbsolutePath() + "/worlds/" + name), new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/" + name));
        World world = Bukkit.getServer().createWorld(new WorldCreator(name));
        world.setAutoSave(false);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("keepInventory", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setTime(12000L);
        world.setThundering(false);
        world.setStorm(false);
        world.setDifficulty(Difficulty.EASY);

        for (Entity entity : world.getEntities()) {
            entity.remove();
        }

        defaultSpawn = cursor.getLocation("DefaultSpawn");
        centerLocation = cursor.getLocation("CenterLocation");
        min = cursor.getLocation("MinLocation");
        max = cursor.getLocation("MaxLocation");
        spawns = getSpawnLocations();
        chests = getChestBlockData();
        enderchests = getEnderChestBlockData();
        deathmatchSpawns = getDeathmatchLocations();
    }

    public void unloadWorld(){
        Bukkit.getServer().unloadWorld(name, false);
        WorldDataUtility.deleteWorld(new File(HSG.getInstance().getServer().getWorldContainer().getAbsolutePath() + "/" + name));
        defaultSpawn = null;
        centerLocation = null;
        min = null;
        max = null;
        spawns = null;
        deathmatchSpawns = null;
        chests = null;
        enderchests = null;
    }
}
