package net.routemc.sg.utility;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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

    public static ItemStack createWool(String name, int amount, int color, String... lore) {
        if (amount == 0) {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(Material.WOOL, amount, (short) color);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        if (lore != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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

    public static ItemStack createPlayerSkull(String playerName, String displayName, String... lore){
        ItemStack myAwesomeSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta myAwesomeSkullMeta = (SkullMeta) myAwesomeSkull.getItemMeta();
        myAwesomeSkullMeta.setOwner(playerName);
        myAwesomeSkull.setItemMeta(myAwesomeSkullMeta);
        ItemMeta meta = myAwesomeSkull.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        myAwesomeSkull.setItemMeta(meta);
        return myAwesomeSkull;
    }

    public static ItemStack createPlayerSkull(String playerName, String displayName, List<String> lore){
        ItemStack myAwesomeSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta myAwesomeSkullMeta = (SkullMeta) myAwesomeSkull.getItemMeta();
        myAwesomeSkullMeta.setOwner(playerName);
        myAwesomeSkull.setItemMeta(myAwesomeSkullMeta);
        ItemMeta meta = myAwesomeSkull.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            meta.setLore(lore);
        }
        myAwesomeSkull.setItemMeta(meta);
        return myAwesomeSkull;
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

    public static ItemStack createGlass(String name, int amount, int color, String... lore) {
        if (amount == 0) {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS, amount, (short) color);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        if (lore != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createGlassPane(String name, int amount, int color, String... lore) {
        if (amount == 0) {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, amount, (short) color);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        if (lore != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createDye(String name, int amount, int color, String... lore) {
        if (amount == 0) {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(Material.INK_SACK, amount, (short) color);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        if (lore != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
