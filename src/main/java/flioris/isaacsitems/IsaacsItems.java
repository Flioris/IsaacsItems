package flioris.isaacsitems;

import flioris.isaacsitems.command.MainCommand;
import flioris.isaacsitems.listener.ItemUseListener;
import flioris.isaacsitems.listener.MainListener;
import flioris.isaacsitems.listener.SideEffectListener;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class IsaacsItems extends JavaPlugin {
    @Getter
    private static IsaacsItems plugin;
    @Getter
    private static final Random random = new Random();

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {}

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(new MainListener(), this);
        manager.registerEvents(new ItemUseListener(), this);
        manager.registerEvents(new SideEffectListener(), this);
    }

    private void registerCommands() {
        PluginCommand command = getCommand("isaacsitems");

        command.setExecutor(new MainCommand());
        command.setTabCompleter(new MainCommand());
    }
}
