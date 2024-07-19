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
}
