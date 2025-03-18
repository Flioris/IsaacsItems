package flioris.isaacsitems.listener;

import flioris.isaacsitems.IsaacsItems;
import flioris.isaacsitems.data.EntityData;
import flioris.isaacsitems.data.PlayerData;
import flioris.isaacsitems.event.ItemUseEvent;
import flioris.isaacsitems.event.SideEffectEvent;
import flioris.isaacsitems.item.ItemHandler;
import flioris.isaacsitems.item.ItemType;
import flioris.isaacsitems.spirit.Spirit;
import flioris.isaacsitems.util.ConfigHandler;
import flioris.isaacsitems.util.InventoryHandler;
import flioris.isaacsitems.util.PossessedVillagerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
        }.runTaskTimer(plugin, 20, 20);
    }

    // Drop an item with a certain chance when killing a mob.
    @EventHandler
    private static void onEntityDeath(EntityDeathEvent event) {
        if (!ConfigHandler.getBoolean("isaacsitems-drops-from-mobs")) {
            return;
        }

        if (random.nextInt(100) >= ConfigHandler.getInt("chance")) {
            return;
        }

        Location location = event.getEntity().getLocation();
        List<ItemType> enabledItems = ConfigHandler.getEnabledItems();
        ItemStack item = ConfigHandler.getItem(enabledItems.get(random.nextInt(enabledItems.size())), 1);

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
                (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                        cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            manager.callEvent(new SideEffectEvent(ItemType.THE_TOWER, player, event));
        } else if (socks != null && (cause == EntityDamageEvent.DamageCause.FALL ||
                cause == EntityDamageEvent.DamageCause.HOT_FLOOR || cause == EntityDamageEvent.DamageCause.CONTACT)) {
            manager.callEvent(new ItemUseEvent(socks, ItemType.SOCKS, player, null, event));
        } else if (rock_bottom != null && (cause == EntityDamageEvent.DamageCause.MAGIC ||
                cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.WITHER)) {
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
    private static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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
            Inventory inventory = player.getInventory();
            UUID uuid = player.getUniqueId();
            ItemStack the_intruder = InventoryHandler.findItem(inventory, ItemType.THE_INTRUDER);
            ItemStack spirit_shackles = InventoryHandler.findItem(inventory, ItemType.SPIRIT_SHACKLES);
            ItemStack it_hurts = InventoryHandler.findItem(inventory, ItemType.IT_HURTS);
            if (the_intruder != null) {
                manager.callEvent(new ItemUseEvent(the_intruder, ItemType.THE_INTRUDER, player, damager, event));
            }
            if (spirit_shackles != null && !PlayerData.getKilledPlayers().containsKey(uuid) &&
                    player.getHealth() - event.getDamage() <= 0) {
                manager.callEvent(new ItemUseEvent(spirit_shackles, ItemType.SPIRIT_SHACKLES, player, damager, event));
            }
            if (it_hurts != null && !PlayerData.getItHurtsPlayers().contains(uuid)) {
                manager.callEvent(new ItemUseEvent(it_hurts, ItemType.IT_HURTS, player, damager, event));
            }
        }
    }

    // Using cards.
    @EventHandler
    private static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != ItemType.getMaterial()) {
            return;
        }

        ItemType itemType = ItemType.getFromCustomModelData(InventoryHandler.getModelData(item));

        if (itemType == null) {
            return;
        }

        int cmd = itemType.getCustomModelData();

        // cmd < 5 - cards
        if (cmd < 5) {
            manager.callEvent(new ItemUseEvent(item, itemType, player, null, event));
        // 12 < cmd < 21 - food
        } else if ( 12 < cmd && cmd < 21) {
            manager.callEvent(new ItemUseEvent(item, itemType, player, null, event));
        }
    }

    // Spawns a possessed villager.
    @EventHandler
    private static void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Villager) {
            if (ConfigHandler.getBoolean("entities.possessed.enabled") &&
                    random.nextInt(100) < ConfigHandler.getInt("entities.possessed.chance")) {
                PossessedVillagerHandler.spawnPossessedVillager((Villager) entity);
            }
        }
    }

    // Prevents zombies from targeting possessed villagers.
    @EventHandler
    private static void onEntityTarget(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        EntityType type = event.getEntity().getType();
        boolean isZombie = type == EntityType.ZOMBIE || type == EntityType.ZOMBIE_VILLAGER ||
                type == EntityType.DROWNED || type == EntityType.HUSK;

        if (isZombie && target != null && target.hasMetadata("isPossessed")) {
            event.setCancelled(true);
        }
    }

    // Changes the possessed villager's goods when changing profession.
    @EventHandler
    private static void onVillagerCareerChange(VillagerCareerChangeEvent event) {
        Villager villager = event.getEntity();

        if (!villager.hasMetadata("isPossessed")) {
            return;
        }

        PossessedVillagerHandler.setPossessedTrades(villager);
    }

    // Changes the prices of the possessed villager when a new product appears.
    @EventHandler
    private static void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Villager) || !entity.hasMetadata("isPossessed")) {
            return;
        }

        Villager villager = (Villager) entity;
        List<ItemStack> items = new ArrayList<>();

        items.add(ConfigHandler.getItem(ItemType.YOUR_SOUL, villager.getVillagerLevel()));
        event.getRecipe().setIngredients(items);
    }

    // Sending resource pack to players.
    @EventHandler
    private static void onPlayerJoin(PlayerJoinEvent event) {
        if (!ConfigHandler.getBoolean("resourcepack.enabled")) {
            return;
        }

        Player player = event.getPlayer();

        String url = ConfigHandler.getString("resourcepack.url");
        byte[] sha1 = ConfigHandler.getBytes("resourcepack.sha1-hash");

        player.setResourcePack(url, sha1);

        if (player.isOp()) {
            plugin.checkForUpdates().thenAccept(isUpToDate -> {
                if (!isUpToDate) {
                    player.sendMessage(ConfigHandler.improve("messages.update-available"));
                }
            });
        }
    }

    // Removes extra HP from IsaacsItems food on death.
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attribute == null) {
            return;
        }

        attribute.getModifiers().stream()
                .filter(modifier -> modifier.getUniqueId().equals(ItemHandler.getHealthModifierID()))
                .findFirst()
                .ifPresent(attribute::removeModifier);
    }
}
