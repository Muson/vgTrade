package ru.vgTrade.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.vgTrade.Config.LanguageManager;
import ru.vgTrade.Trade.Trade;
import ru.vgTrade.Trade.TradeRequest;
import ru.vgTrade.VgTrade;

public class VgTradePlayerListener implements Listener {

    private final VgTrade plugin;

    public VgTradePlayerListener(VgTrade instance) {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles a player interact entity event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!plugin.getConfigManager().isRightClickTradeEnabled()) return;

        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player target = (Player) event.getRightClicked();
        Player player = (Player) event.getPlayer();

        // accept trade request on click to initiator
        TradeRequest tr = plugin.getTradeManager().getRequest(player);
        TradeRequest tr1 = plugin.getTradeManager().getRequest(target);
        if (tr != null && tr == tr1 && !tr.isInitiator(player)) {
            player.performCommand("/trade accept");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.isBusy(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.NOTNOW));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTradeManager().onPlayerQuit(event.getPlayer());
    }

}
