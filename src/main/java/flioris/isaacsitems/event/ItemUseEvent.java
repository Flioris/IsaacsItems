package flioris.isaacsitems.event;

import flioris.isaacsitems.item.ItemType;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// Triggering/using Isaac's Items.
@Getter
public class ItemUseEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final ItemStack item;
    private final ItemType type;
    private final Player player;
    private final LivingEntity target;
    private final Event source;
    private boolean cancelled = false;

    public ItemUseEvent(ItemStack item, ItemType type, Player player, LivingEntity target, Event source) {
        this.item = item;
        this.type = type;
        this.player = player;
        this.target = target;
        this.source = source;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
