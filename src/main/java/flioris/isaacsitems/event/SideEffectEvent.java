package flioris.isaacsitems.event;

import flioris.isaacsitems.item.ItemType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

// Side effects from triggering/using Isaac's Items.
@Getter
public class SideEffectEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final ItemType type;
    private final Player player;
    private final Event source;
    private boolean cancelled = false;

    public SideEffectEvent(ItemType type, Player player, Event source) {
        this.type = type;
        this.player = player;
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
