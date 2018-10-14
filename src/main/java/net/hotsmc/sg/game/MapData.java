package net.hotsmc.sg.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.utility.PositionInfo;
import net.hotsmc.sg.utility.WorldDataUtility;
import org.bukkit.*;
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

    public void loadWorld(){
        WorldDataUtility.copyWorld(new File(HSG.getInstance().getDataFolder().getAbsolutePath() + "/worlds/" + name), new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/" + name));
        World world = Bukkit.getServer().createWorld(new WorldCreator(name));
        world.setAutoSave(false);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("keepInventory", "false");
        world.setGameRuleValue("doFireTick", "false");
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
        deathmatchSpawns = getDeathmatchLocations();

        new BukkitRunnable() {
            @Override
            public void run() {
                this.cancel();
            }
        }.runTaskLater(HSG.getInstance(), 20*2);
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
    }
}
