package flioris.isaacsitems.item;

import com.google.common.collect.Multiset;
import flioris.isaacsitems.IsaacsItems;
import flioris.isaacsitems.data.EntityData;
import flioris.isaacsitems.data.PlayerData;
import flioris.isaacsitems.spirit.Spirit;
import flioris.isaacsitems.util.InventoryHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ItemHandler {
    private static final Random random = IsaacsItems.getRandom();
    private static final IsaacsItems plugin = IsaacsItems.getPlugin();

    public static void useTheHierophant(Player player, ItemStack item) {
        PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.ABSORPTION);
        int rawAmplifier = potionEffect == null ? 0 : potionEffect.getAmplifier() + 1;
        int amplifier = Math.min(rawAmplifier, 9);

        item.setAmount(item.getAmount() - 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 6000, amplifier));
        player.getWorld().playSound(player.getLocation(), "the_hierophant", 1f, 1f);
    }

    public static void useTheLovers(Player player, ItemStack item) {
        item.setAmount(item.getAmount() - 1);
        player.setHealth(Math.min(player.getHealth() + 4, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        player.getWorld().playSound(player.getLocation(), "the_lovers", 1f, 1f);
    }

    public static void useTheChariot(Player player, ItemStack item) {
        Multiset<UUID> chariotPlayers = PlayerData.getChariotPlayers();
        UUID uuid = player.getUniqueId();

        item.setAmount(item.getAmount() - 1);
        chariotPlayers.add(uuid);
        player.setGlowing(true);
        new BukkitRunnable() {;
            @Override
            public void run() {
                chariotPlayers.remove(uuid);
                player.setGlowing(false);
            }
        }.runTaskLater(plugin, 120);
        player.getWorld().playSound(player.getLocation(), "the_chariot", 1f, 1f);
    }

    public static void applyTheChariotEffect(Player player) {
        for (Entity entity: player.getNearbyEntities(0.5, 0.5, 0.5)) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            ((LivingEntity) entity).damage(8, player);
        }
    }

    public static void useMonstrance(Player player) {
        for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }

            LivingEntity living = (LivingEntity) entity;
            UUID damagerUUID = EntityData.getLastDamageByPlayer().get(living.getUniqueId());

            if (damagerUUID != null && damagerUUID.equals(player.getUniqueId())) {
                living.damage(4, player);
            }
        }
    }

    public static void useSpiritShackles(Player player, EntityDamageByEntityEvent event) {
        Map<UUID, Spirit> killedPlayers = PlayerData.getKilledPlayers();
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();
        Spirit spirit = new Spirit(location, true);
        Location center = location.clone();
        int points = 32;
        double increment = (2 * Math.PI) / points;

        event.setCancelled(true);
        center.setY(center.getY() + 1);
        killedPlayers.put(uuid, spirit);
        player.setInvisible(true);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/2);
        player.setFoodLevel(0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!spirit.isRunning()) {
                    cancel();
                }
                for (int i = 0; i < points; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (7 * Math.cos(angle));
                    double z = center.getZ() + (7 * Math.sin(angle));
                    Location point = new Location(center.getWorld(), x, center.getY(), z);
                    center.getWorld().spawnParticle(Particle.SOUL, point, 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvisible(false);
                spirit.setRunning(false);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        killedPlayers.remove(uuid);
                    }
                }.runTaskLater(plugin, 200);
            }
        }.runTaskLater(plugin, 200);
    }

    public static void applySpiritShacklesEffect(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    public static void useTheTower(Player player, ItemStack item) {
        Multiset<UUID> towerPlayers = PlayerData.getTowerPlayers();
        UUID uuid = player.getUniqueId();
        Vector[] offsets = new Vector[] {
                new Vector(1, 2, 0),
                new Vector(-1, 2, 0),
                new Vector(0, 2, 1),
                new Vector(0, 2, -1),
                new Vector(1, 2, 1),
                new Vector(-1, 2, -1)
        };

        item.setAmount(item.getAmount() - 1);

        for (Vector offset : offsets) {
            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation().add(offset), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(20);
        }

        towerPlayers.add(uuid);
        new BukkitRunnable() {;
            @Override
            public void run() {
                towerPlayers.remove(uuid);
            }
        }.runTaskLater(plugin, 40);

        player.getWorld().playSound(player.getLocation(), "the_tower", 1f, 1f);
    }

    public static void applyTheTowerEffect(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    public static void useSocks(Player player, EntityDamageEvent event) {
        player.setVelocity(player.getVelocity().setY(Math.sqrt(0.08 * player.getFallDistance())));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 1f, 1f);
        event.setCancelled(true);
    }

    public static void useBagLunch(Player player) {
        if (random.nextInt(5) < 1) {
            InventoryHandler.addItem(player, new ItemStack(Material.GOLDEN_CARROT));
        }
    }

    public static void useTheIntruder(Player player, LivingEntity entity) {
        if (random.nextInt(5) < 1) {
            CaveSpider caveSpider = player.getWorld().spawn(
                    player.getLocation(),
                    org.bukkit.entity.CaveSpider.class,
                    mob -> mob.setTarget(entity));

            caveSpider.setMetadata("constantTarget", new FixedMetadataValue(plugin, entity));

            new BukkitRunnable() {
                @Override
                public void run() {
                    caveSpider.remove();
                }
            }.runTaskLater(plugin, 160);
        }
    }

    public static void useRockBottom(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
