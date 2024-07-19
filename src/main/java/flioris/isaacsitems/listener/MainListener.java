package flioris.isaacsitems.listener;

import flioris.isaacsitems.IsaacsItems;
import flioris.isaacsitems.data.EntityData;
import flioris.isaacsitems.data.PlayerData;
import flioris.isaacsitems.event.ItemUseEvent;
import flioris.isaacsitems.event.SideEffectEvent;
import flioris.isaacsitems.item.ItemType;
import flioris.isaacsitems.spirit.Spirit;
import flioris.isaacsitems.util.ConfigHandler;
import flioris.isaacsitems.util.InventoryHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class MainListener implements Listener {
    private static final IsaacsItems plugin = IsaacsItems.getPlugin();
    private static final PluginManager manager = plugin.getServer().getPluginManager();
    private static final Random random = IsaacsItems.getRandom();

    // Checking the possibility of using Monstrance.
    static  {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ItemStack monstrance = InventoryHandler.findItem(player.getInventory(), ItemType.MONSTRANCE);
                    if (monstrance != null) {
                        manager.callEvent(new ItemUseEvent(monstrance, ItemType.MONSTRANCE, player, null, null));
                    }
                }
            }
        }.runTaskTimer(IsaacsItems.getPlugin(), 20, 20);
    }

    @EventHandler
    private static void onEntityDeath(EntityDeathEvent event) {
        if (!ConfigHandler.getBoolean("isaacsitems-drops-from-mobs")) {
            return;
        }

        if (random.nextInt(100) >= ConfigHandler.getInt("chance")) {
            return;
        }

        Location location = event.getEntity().getLocation();
        ItemStack item = ConfigHandler.getItem(ItemType.values()[random.nextInt(ItemType.values().length)], 1);

        location.getWorld().dropItem(location, item);
    }

    // Check for the location of the Spirit and the possibility of damage from The Chariot effect.
    @EventHandler(priority = EventPriority.LOWEST)
    private static void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (PlayerData.getChariotPlayers().contains(uuid)) {
            manager.callEvent(new SideEffectEvent(ItemType.THE_CHARIOT, player, event));
        }

        Map<UUID, Spirit> killedPlayers = PlayerData.getKilledPlayers();
        Spirit spirit = killedPlayers.get(uuid);

        if (spirit == null || !spirit.isRunning()) {
            return;
        }

        Location oldLocation = spirit.getDeathLocation();
        Location newLocation = player.getLocation();

        newLocation.setY(oldLocation.getY());

        double distance = oldLocation.distance(newLocation);

        if (distance > 7) {
            killedPlayers.remove(uuid);
            player.setHealth(0.0);
        }
    }

    // Check for The Tower effect and the possibility of using Socks, Rock Bottom and Bag Lunch.
    @EventHandler(priority = EventPriority.LOWEST)
    private static void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        ItemStack socks = InventoryHandler.findItem(player.getInventory(), ItemType.SOCKS);
        ItemStack rock_bottom = InventoryHandler.findItem(player.getInventory(), ItemType.ROCK_BOTTOM);
        ItemStack bag_lunch = InventoryHandler.findItem(player.getInventory(), ItemType.BAG_LUNCH);

        if (PlayerData.getTowerPlayers().contains(player.getUniqueId()) &&
                cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            manager.callEvent(new SideEffectEvent(ItemType.THE_TOWER, player, event));
        } else if (socks != null && cause == EntityDamageEvent.DamageCause.FALL) {
            manager.callEvent(new ItemUseEvent(socks, ItemType.SOCKS, player, null, event));
        } else if (rock_bottom != null && cause == EntityDamageEvent.DamageCause.MAGIC ||
                cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.WITHER) {
            manager.callEvent(new ItemUseEvent(rock_bottom, ItemType.ROCK_BOTTOM, player, null, event));
        } else if (bag_lunch != null) {
            manager.callEvent(new ItemUseEvent(bag_lunch, ItemType.BAG_LUNCH, player, null, event));
        }
    }

    // Monitoring the correct operation of The Intruder.
    @EventHandler
    private static void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof CaveSpider)) {
            return;
        }

        CaveSpider spider = (CaveSpider) event.getEntity();

        if (spider.hasMetadata("constantTarget") &&
                !Objects.equals(spider.getMetadata("constantTarget").get(0).value(), event.getTarget())) {
            event.setCancelled(true);
        }
    }

    // Checking for the ability to use Spirit Shackles and recording damage.
    @EventHandler(priority = EventPriority.LOWEST)
    private static void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.isCancelled() || !(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity damager = (LivingEntity) event.getDamager();
        LivingEntity entity = (LivingEntity) event.getEntity();

        if (damager instanceof Player) {
            Player player = (Player) damager;
            Spirit spirit = PlayerData.getKilledPlayers().get(player.getUniqueId());
            if (spirit != null && spirit.isRunning()) {
                manager.callEvent(new SideEffectEvent(ItemType.SPIRIT_SHACKLES, player, event));
            } else {
                EntityData.getLastDamageByPlayer().put(event.getEntity().getUniqueId(), player.getUniqueId());
            }
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;
            ItemStack the_intruder = InventoryHandler.findItem(player.getInventory(), ItemType.THE_INTRUDER);
            ItemStack spirit_shackles = InventoryHandler.findItem(player.getInventory(), ItemType.SPIRIT_SHACKLES);
            if (the_intruder != null) {
                manager.callEvent(new ItemUseEvent(the_intruder, ItemType.THE_INTRUDER, player, damager, event));
            }
            if (spirit_shackles != null && !PlayerData.getKilledPlayers().containsKey(player.getUniqueId()) &&
                    player.getHealth() - event.getDamage() <= 0) {
                manager.callEvent(new ItemUseEvent(spirit_shackles, ItemType.SPIRIT_SHACKLES, player, damager, event));
            }
        }
    }

    // Using cards.
    @EventHandler
    private static void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        ItemType itemType = ItemType.getFromCustomModelData(InventoryHandler.getModelData(item));

        if (itemType != null && itemType.getCustomModelData() <= 4) {
            manager.callEvent(new ItemUseEvent(item, itemType, player, null, event));
        }
    }
}
