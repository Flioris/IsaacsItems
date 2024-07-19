package flioris.isaacsitems.util;

import flioris.isaacsitems.item.ItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryHandler {

    public static void addItem(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();

        for (int i = 0 ; i < 36 ; i++) {
            ItemStack current = inventory.getItem(i);
            if (current == null || (current.getType() == item.getType() &&
                    current.getAmount() + item.getAmount() <= current.getMaxStackSize())) {
                inventory.addItem(item);
                return;
            }
        }

        player.getWorld().dropItem(player.getLocation(), item);
    }

    public static ItemStack findItem(Inventory inventory, ItemType itemType) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == ItemType.getMaterial() && item.hasItemMeta() &&
                    getModelData(item) == itemType.getCustomModelData()) {
                return item;
            }
        }

        return null;
    }

    public static int getModelData(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        return itemMeta != null && itemMeta.hasCustomModelData()? itemMeta.getCustomModelData() : 0;
    }
}
