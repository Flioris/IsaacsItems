package flioris.isaacsitems;

import flioris.isaacsitems.command.MainCommand;
import flioris.isaacsitems.listener.ItemUseListener;
import flioris.isaacsitems.listener.MainListener;
import flioris.isaacsitems.listener.SideEffectListener;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

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
        checkForUpdates();
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

    public void checkForUpdates() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL("https://api.spigotmc.org/legacy/update.php?resource=118175").openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                Scanner scanner = new Scanner(connection.getInputStream());
                if (scanner.hasNext()) {
                    String latestVersion = scanner.next();
                    String currentVersion = getDescription().getVersion();

                    if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                        getLogger().warning("Update available: https://www.spigotmc.org/resources/118175");
                    }
                }
                scanner.close();
            } catch (IOException e) {
                getLogger().warning("Error checking for updates: " + e.getMessage());
            }
        });
    }
}
