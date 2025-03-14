package flioris.isaacsitems.util;

import flioris.isaacsitems.IsaacsItems;
import flioris.isaacsitems.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private static final Plugin plugin = IsaacsItems.getPlugin();

    public static void reload() {
        plugin.reloadConfig();
    }

    public static boolean getBoolean(String path) {
        return plugin.getConfig().getBoolean(path);
    }

    public static String getString(String path) {
        return plugin.getConfig().getString(path);
    }

    public static int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

    public static ItemStack getItem(ItemType type, int count) {
        ItemStack item = new ItemStack(ItemType.getMaterial(), count);
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(type.getCustomModelData());
        meta.setDisplayName(improve("items." + type + ".display-name"));
        meta.setLore(improveList("items." + type + ".lore"));
        item.setItemMeta(meta);

        return item;
    }

    public static String improve(String path) {
        String s = getString(path);

        return ChatColor.translateAlternateColorCodes('&', s == null? path + " не найдено." : s
                .replaceAll("&#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", "&x&$1&$2&$3&$4&$5&$6"));
    }

    public static List<String> improveList(String path) {
        List<String> list = new ArrayList<>();

        for (String s : plugin.getConfig().getStringList(path)) {
            list.add(ChatColor.translateAlternateColorCodes('&', s
                    .replaceAll("&#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", "&x&$1&$2&$3&$4&$5&$6")));
        }

        return list;
    }

    public static List<ItemType> getEnabledItems() {
        List<ItemType> list = new ArrayList<>();

        for (String itemName : plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
            if (getBoolean("items." + itemName + ".enabled")) {
                list.add(ItemType.valueOf(itemName));
            }
        }

        return list;
    }

    public static byte[] getBytes(String path) {
        String string = plugin.getConfig().getString(path);
        int len = string.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4)
                    + Character.digit(string.charAt(i+1), 16));
        }

        return data;
    }
}
