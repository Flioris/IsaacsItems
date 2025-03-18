package flioris.isaacsitems.listener;

import flioris.isaacsitems.event.ItemUseEvent;
import flioris.isaacsitems.item.ItemHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUseListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private static void onItemUse(ItemUseEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        LivingEntity target = event.getTarget();
        ItemStack item = event.getItem();
        Event source = event.getSource();

        switch (event.getType()) {
            case THE_HIEROPHANT:
                ItemHandler.useTheHierophant(player, item);
                break;
            case THE_LOVERS:
                ItemHandler.useTheLovers(player, item);
                break;
            case THE_CHARIOT:
                ItemHandler.useTheChariot(player, item);
                break;
            case THE_TOWER:
                ItemHandler.useTheTower(player, item);
                break;
            case BAG_LUNCH:
                ItemHandler.useBagLunch(player);
                break;
            case THE_INTRUDER:
                ItemHandler.useTheIntruder(player, target);
                break;
            case ROCK_BOTTOM:
                ItemHandler.useRockBottom((EntityDamageEvent) source);
                break;
            case SOCKS:
                ItemHandler.useSocks(player, (EntityDamageEvent) source);
                break;
            case MONSTRANCE:
                ItemHandler.useMonstrance(player);
                break;
            case SPIRIT_SHACKLES:
                ItemHandler.useSpiritShackles(player, (EntityDamageByEntityEvent) source);
                break;
            case IT_HURTS:
                ItemHandler.useItHurts(player);
                break;
            default:
                ItemHandler.useFood(player, item);
                break;
        }
    }
}
