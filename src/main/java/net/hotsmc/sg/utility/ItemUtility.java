package net.hotsmc.sg.utility;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemUtility {
    public static ItemStack createItemStack(String name, Material mat, boolean unbreakable, String... lore) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        if(unbreakable) {
            meta.spigot().setUnbreakable(true);
        }
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemStack(String name, Material mat, boolean unbreakable, List<String> lore) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(lore);
        }
        if(unbreakable) {
            meta.spigot().setUnbreakable(true);
        }
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createFlintAndSteel(){
        ItemStack is = new ItemStack(Material.FLINT_AND_STEEL);
        is.setDurability((short)61);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Flint and Steel");
        is.setItemMeta(meta);
        return is;
    }
}
