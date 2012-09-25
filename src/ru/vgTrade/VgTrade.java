package ru.vgTrade;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vgTrade.Config.ConfigManager;
import ru.vgTrade.Config.LanguageManager;
import ru.vgTrade.Listeners.VgTradeInventoryListener;
import ru.vgTrade.Listeners.VgTradePlayerListener;
import ru.vgTrade.Trade.TradeManager;
import ru.vgTrade.Trade.TradePlayer;
import ru.vgTrade.Util.InventoryUtils;
import ru.vgTrade.Util.Log;
import org.bukkit.plugin.Plugin;
/**
 * @author Muson (Original code: Oliver Brown (Arkel))
 * 
 */
public class VgTrade extends JavaPlugin {

    private ConfigManager config;
    private LanguageManager languageManager;

    private TradeManager manager;

    private List<String> playersIgnoring = new ArrayList<String>();

    public void onDisable() {
        manager.terminateActiveTrades();

        languageManager.save();
        Log.info(this + " disabled");
    }

    public void onEnable() {
        Log.serLogger(this.getLogger());
        InventoryUtils.setPlugin(this);
        
        config = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        manager = new TradeManager(this);

        new VgTradeInventoryListener(this);
        new VgTradePlayerListener(this);

        Log.verbose = config.isVerboseLoggingEnabled();
        
        Log.info(this + " enabled");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            if (args.length > 0 && args[0].compareToIgnoreCase("reload")==0) {
                this.reloadConfig();
                sender.sendMessage("Configuration reloaded");
            }
            else sender.sendMessage("You must be a player to do that");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("trade")) {
            Player player = ((Player) sender);
            return doCommand(player, args);
        }

        return super.onCommand(sender, cmd, commandLabel, args);
    }

    /**
     * @param player the player who sent the command
     * @param args   the command arguments
     * @return whether the command was successful
     */
    private boolean doCommand(Player player, String[] args) {

        if (!player.hasPermission("vgtrade.trade")) {
            player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.UNABLE));
            return true;
        }            
        
        if (args.length == 0) {
            // You must specify an option
            player.sendMessage(ChatColor.RED + languageManager.getString(LanguageManager.Strings.OPTION));

        } else if ("accept".equalsIgnoreCase(args[0]) || "decline".equalsIgnoreCase(args[0])) {

            manager.handleCommand(args[0], player);

        } else if ("ignore".equalsIgnoreCase(args[0])) {

            if (playersIgnoring.contains(player.getName())) {
                playersIgnoring.remove(player.getName());
                player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.NOTIGNORING));
            } else {
                playersIgnoring.add(player.getName());
                player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.IGNORING));
            }

        } else {

            Player target;

            if ((target = getServer().getPlayer(args[0])) == null) {
                player.sendMessage(ChatColor.RED
                        // The player you specified is not online
                        + languageManager.getString(LanguageManager.Strings.ONLINE));
                return true;
            } else if (player.equals(target)) {
                player.sendMessage(ChatColor.RED
                        // You can't trade with yourself!
                        + languageManager.getString(LanguageManager.Strings.YOURSELF));
                return true;
            }


            if (!isBusy(player)) {
                requestTrade(player, target);
            } else {
                // Unable to trade with <target name>
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.UNABLE) + " " + target.getName());
            }

        }
        return true;
    }

    /**
     * Attempts to begin a trade for the two given players
     *
     * @param initiator The player who initiated the trade
     * @param target    The target of the initiator
     */
    public void requestTrade(Player initiator, Player target) {
        if (playersIgnoring.contains(target.getName())) {
            initiator.sendMessage(ChatColor.RED + target.getName() + " " + LanguageManager.getString(LanguageManager.Strings.PLAYERIGNORING));
        } else if (config.canTrade(initiator, target)) {
            manager.begin(new TradePlayer(initiator), new TradePlayer(target));
        }
    }

    /**
     * Get the current instance of the TradeManager
     *
     * @return The current instance of the TradeManager
     */
    public TradeManager getTradeManager() {
        return manager;
    }

    /**
     * Checks if the player is currently involved in a trade or request
     *
     * @param player - the player to check
     * @return - if they are involved in a trade or request
     */
    public boolean isBusy(Player player) {
        return manager.isBusy((Player) player);
    }

    public ConfigManager getConfigManager() {
        return config;
    }

}
