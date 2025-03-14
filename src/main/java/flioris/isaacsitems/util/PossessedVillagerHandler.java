package flioris.isaacsitems.util;

import flioris.isaacsitems.IsaacsItems;
import flioris.isaacsitems.item.ItemType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class PossessedVillagerHandler {
    private static final IsaacsItems plugin = IsaacsItems.getPlugin();

    // Changes the prices of the possessed villager.
    public static void setPossessedTrades(Villager villager) {
        List<MerchantRecipe> recipes = villager.getRecipes();
        List<ItemStack> items = new ArrayList<>();

        villager.setMetadata("isPossessed", new FixedMetadataValue(plugin, true));
        villager.setCustomName(ConfigHandler.improve("entities.possessed.title"));

        items.add(ConfigHandler.getItem(ItemType.YOUR_SOUL, 1));
        for (MerchantRecipe recipe : recipes) {
            recipe.setIngredients(items);
        }
    }

    // Spawns a possessed villager.
    public static void spawnPossessedVillager(Villager villager) {
        villager.setMetadata("isPossessed", new FixedMetadataValue(plugin, true));
        villager.setCustomName(ConfigHandler.improve("entities.possessed.title"));
        setPossessedTrades(villager);
    }
}
