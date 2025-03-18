package flioris.isaacsitems;

import flioris.isaacsitems.command.MainCommand;
import flioris.isaacsitems.listener.ItemUseListener;
import flioris.isaacsitems.listener.MainListener;
import flioris.isaacsitems.listener.SideEffectListener;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public final class IsaacsItems extends JavaPlugin {
    @Getter
    private static IsaacsItems plugin;
    @Getter
    private static final Random random = new Random();

    @Override
    public void onEnable() {
        plugin = this;

        File configFile = new File(getDataFolder(), "config.yml");

        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            String configVersion = config.getString("version");
            String pluginVersion = getDescription().getVersion();

            if (!Objects.equals(configVersion, pluginVersion)) {
                getLogger().warning("The config is outdated. I renamed the old config to old-config.yml and created a new one.");

                File oldConfig = new File(getDataFolder(), "old-config.yml");

                if (oldConfig.exists()) {
                    oldConfig.delete();
                }

                if (configFile.exists()) {
                    configFile.renameTo(oldConfig);
                }

                saveDefaultConfig();
                reloadConfig();
            }
        }

        registerEvents();
        registerCommands();

        checkForUpdates().thenAccept(isUpToDate -> {
            if (!isUpToDate) {
                getLogger().warning("Update available: https://www.spigotmc.org/resources/118175");
            }
        });
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

    public CompletableFuture<Boolean> checkForUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL("https://api.spigotmc.org/legacy/update.php?resource=118175").openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    if (scanner.hasNext()) {
                        String latestVersion = scanner.next();
                        String currentVersion = getDescription().getVersion();
                        return latestVersion.equalsIgnoreCase(currentVersion);
                    }
                }
            } catch (IOException e) {
                getLogger().warning("Error checking for updates: " + e.getMessage());
            }
            return false;
        });
    }
}
