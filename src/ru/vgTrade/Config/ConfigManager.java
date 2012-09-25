package ru.vgTrade.Config;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ConfigManager {

    private final FileConfiguration config;
    private final Plugin plugin;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Check if the right click to trade feature is enabled, defaults to false
     *
     * @return - whether right click trade is enabled
     */
    public boolean isRightClickTradeEnabled() {
        return config.getBoolean("RightClickTrade", false);
    }

    /**
     * Check if the right click to trade feature is enabled, defaults to false
     *
     * @return - whether right click trade is enabled
     */
    public boolean isVerboseLoggingEnabled() {
        return config.getBoolean("Verbose", true);
    }

    /**
     * Check if the range checking feature is enabled, defaults to false
     *
     * @return - whether range checking is enabled
     */
    boolean isRangeCheckEnabled() {
        return config.getBoolean("RangeCheck.Enabled", false);
    }

    /**
     * Get the configured range check distance, defaults to 30
     *
     * @return - the distance, as an integer
     */
    int getRangeCheckDistance() {
        return config.getInt("RangeCheck.MaxDistance", 30);
    }


    /**
     * Determines whether or not the two given players can trade
     *
     * @param player - the first player
     * @param target - the second player
     * @return - whether or not the two players can trade
     */
    public boolean canTrade(Player player, Player target) {

        if (!player.hasPermission("vgtrade.trade")) {
            return false;
        }

        if (player.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.UNABLE));
            player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.CREATIVE));
            return false;
        }

        if (isRangeCheckEnabled()) {
            if (player.getWorld() != target.getWorld()) {
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TOOFAR));
                return false;
            }
            if (player.getLocation().distance(target.getLocation()) > getRangeCheckDistance()) {
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TOOFAR));
                return false;
            }
        }

        return true;
    }

}
