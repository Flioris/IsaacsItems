package flioris.isaacsitems.listener;

import flioris.isaacsitems.event.SideEffectEvent;
import flioris.isaacsitems.item.ItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SideEffectListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private static void onSideEffectApplication(SideEffectEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Event source = event.getSource();

        switch (event.getType()) {
            case THE_CHARIOT:
                ItemHandler.applyTheChariotEffect(player);
                break;
            case SPIRIT_SHACKLES:
                ItemHandler.applySpiritShacklesEffect((EntityDamageByEntityEvent) source);
                break;
            case THE_TOWER:
                ItemHandler.applyTheTowerEffect((EntityDamageEvent) source);
                break;
        }
    }
}
