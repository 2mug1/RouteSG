package net.hotsmc.sg.utility;

import com.google.common.collect.Lists;
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

    public static List<ItemStack> getSponsorItems(){
        List<ItemStack> items = Lists.newArrayList();
        items.add(new ItemStack(Material.ENDER_PEARL));
        items.add(new ItemStack(Material.IRON_INGOT));
        items.add(new ItemStack(Material.ARROW, 5));
        items.add(new ItemStack(Material.EXP_BOTTLE, 2));
        items.add(new ItemStack(Material.CAKE));
        items.add(new ItemStack(Material.PORK));
        items.add(new ItemStack(Material.BOW));
        items.add(new ItemStack(ItemUtility.createFlintAndSteel()));
        items.add(new ItemStack(new ItemStack(Material.MUSHROOM_SOUP)));
        return items;
    }

    public static ItemStack addLore(ItemStack itemStack, String... lore){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
