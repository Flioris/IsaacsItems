package flioris.isaacsitems.command;

import flioris.isaacsitems.item.ItemType;
import flioris.isaacsitems.util.ConfigHandler;
import flioris.isaacsitems.util.InventoryHandler;
import flioris.isaacsitems.util.PossessedVillagerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!commandSender.hasPermission("isaacsitems.admin")) {
            commandSender.sendMessage(ConfigHandler.improve("messages.no-permission"));
            return false;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return false;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            processReload(commandSender);
            return true;
        } else if (strings[0].equalsIgnoreCase("get")) {
            processGet(commandSender, strings);
            return true;
        } else if (strings[0].equalsIgnoreCase("summon")) {
            processSummon(commandSender, strings);
            return true;
        }

        commandSender.sendMessage(ConfigHandler.improve("messages.usage"));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        List<String> stringList = new ArrayList<>();

        if (strings.length == 1) {
            stringList.add("reload");
            stringList.add("get");
            stringList.add("summon");
            stringList.add("help");
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("get")) {
                for (ItemType type : ItemType.values()) {
                    stringList.add(type.toString());
                }
            } else if (strings[0].equalsIgnoreCase("summon")) {
                stringList.add("possessed");
            }
        } else if (strings[0].equalsIgnoreCase("get")) {
            if (strings.length == 3) {
                for (int i = 1; i < 10; i++) {
                    stringList.add(Integer.toString(i));
                }
            } else if (strings.length == 4) {
                stringList.addAll(Bukkit.getServer().getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .collect(Collectors.toList()));
            }
        }

        return stringList;
    }

    private static void processReload(CommandSender commandSender) {
        ConfigHandler.reload();
        commandSender.sendMessage(ConfigHandler.improve("messages.reloaded"));
    }

    private static void processSummon(CommandSender commandSender, String[] strings) {
        if (strings.length < 2 || !strings[1].equalsIgnoreCase("possessed")) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return;
        }

        Location location = ((Player) commandSender).getLocation();

        location.getWorld().spawn(location, Villager.class, PossessedVillagerHandler::spawnPossessedVillager);
    }

    private static void processGet(CommandSender commandSender, String[] strings) {
        if (strings.length < 2) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return;
        }

        ItemType itemType;

        try {
            itemType = ItemType.valueOf(strings[1]);
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return;
        }

        int count;

        if (strings.length > 2) {
            try {
                count = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                count = 1;
            }
        } else {
            count = 1;
        }

        Player player = null;

        if (strings.length > 3) {
            player = Bukkit.getServer().getPlayer(strings[3]);
        } else if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (player == null) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return;
        }

        InventoryHandler.addItem(player, ConfigHandler.getItem(itemType, count));
    }
}
