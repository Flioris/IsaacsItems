package flioris.isaacsitems.command;

import flioris.isaacsitems.item.ItemType;
import flioris.isaacsitems.util.ConfigHandler;
import flioris.isaacsitems.util.InventoryHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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

        ItemType itemType;

        try {
            itemType = ItemType.valueOf(strings[0]);
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return false;
        }

        int count;

        if (strings.length > 1) {
            try {
                count = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e) {
                count = 1;
            }
        } else {
            count = 1;
        }

        Player player = null;

        if (strings.length > 2) {
            player = Bukkit.getServer().getPlayer(strings[2]);
        } else if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (player == null) {
            commandSender.sendMessage(ConfigHandler.improve("messages.usage"));
            return false;
        }

        InventoryHandler.addItem(player, ConfigHandler.getItem(itemType, count));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        List<String> stringList = new ArrayList<>();

        if (strings.length == 1) {
            for (ItemType type : ItemType.values()) {
                stringList.add(type.toString());
            }
        } else if (strings.length == 2) {
            for (int i = 1; i < 10; i++) {
                stringList.add(Integer.toString(i));
            }
        } else if (strings.length == 3) {
            stringList.addAll(Bukkit.getServer().getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList()));
        }

        return stringList;
    }
}
