package net.routemc.sg.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.routemc.sg.utility.PositionInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Config書き込みクラス
 */
@AllArgsConstructor
@Getter
public class ConfigCursor {

    private final FileConfig fileConfig;
    private String path;

    public boolean exists() {
        return this.exists(null);
    }

    public boolean exists(String path) {
        return this.fileConfig.getConfig().contains(this.path + (path == null ? "" : "." + path));
    }

    public Set<String> getKeys() {
        return this.getKeys(null);
    }

    public Set<String> getKeys(String path) {
        return this.fileConfig.getConfig().getConfigurationSection(this.path + (path == null ? "" : "." + path)).getKeys(false);
    }

    public String getString(String path) {
        return this.fileConfig.getConfig().getString((this.path == null ? "" : this.path + ".") + path);
    }

    public boolean getBoolean(String path) {
        return this.fileConfig.getConfig().getBoolean((this.path == null ? "" : this.path + ".") + "." + path);
    }

    public int getInt(String path) {
        return this.fileConfig.getConfig().getInt((this.path == null ? "" : this.path + ".") + "." + path);
    }

    public long getLong(String path) {
        return this.fileConfig.getConfig().getLong((this.path == null ? "" : this.path + ".") + "." + path);
    }

    public List<String> getStringList(String path) {
        return this.fileConfig.getConfig().getStringList((this.path == null ? "" : this.path + ".") + "." + path);
    }

    public UUID getUuid(String path) {
        return UUID.fromString(this.fileConfig.getConfig().getString(this.path + "." + path));
    }

    public World getWorld(String path) {
        return Bukkit.getWorld(this.fileConfig.getConfig().getString(this.path + "." + path));
    }

    public void set(Object value) {
        this.set(null, value);
    }

    public void set(String path, Object value) {
        this.fileConfig.getConfig().set(this.path + (path == null ? "" : "." + path), value);
    }

    public void save() {
        this.fileConfig.save();
    }

    public void setItemStackList(List<ItemStack> itemStackList, String key) {
        this.fileConfig.getConfig().set(this.path + "." + key, itemStackList);
    }

    public List<ItemStack> getItemStackList(String key) {
        return (List<ItemStack>) this.fileConfig.getConfig().getList(path + "." + key);
    }

    public double getDouble(String path) {
        return this.fileConfig.getConfig().getDouble(this.path + "." + path);
    }

    public void setLocation(String path, PositionInfo pos) {
        set(path + ".world", pos.getWorldName());
        set(path + ".x", pos.getX());
        set(path + ".y", pos.getY());
        set(path + ".z", pos.getZ());
        set(path + ".yaw", pos.getYaw());
        set(path + ".pitch", pos.getPitch());
    }

    public Location getLocation(String path) {
        World world = Bukkit.getWorld(getString(path + ".world"));
        double x = getDouble(path + ".x");
        double y = getDouble(path + ".y");
        double z = getDouble(path + ".z");
        double yaw = getDouble(path + ".yaw");
        double pitch = getDouble(path + ".pitch");
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }
}
