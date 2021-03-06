package ru.vgTrade.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.vgTrade.Trade.Trade;
import ru.vgTrade.Trade.TradeManager;
import ru.vgTrade.Trade.TradeState;
import ru.vgTrade.VgTrade;

public class VgTradeInventoryListener implements Listener {

    private final VgTrade plugin;
    private TradeManager manager;

    public VgTradeInventoryListener(VgTrade instance) {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        manager = plugin.getTradeManager();
    }

    /**
     * Handles an inventory click event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled=true)
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        Event.Result result;
        Player player = (Player) event.getWhoClicked();
        
        // ditch the event early on if the player isn't trading to avoid unnecessary work
        if (!plugin.getTradeManager().isTrading(player)) {
            return;
        }
        
        ItemStack cursor = event.getCursor();
        ItemStack item = event.getCurrentItem();
        
        // That would be pretty pointless....
        if (cursor == null && item == null) {
            return;
        }

        // get the trade instance associated with the player
        Trade trade = manager.getTrade(player);

        Inventory inventory = event.getInventory();

        if (item != null && item.getAmount() < 0) {
            player.sendMessage("Empty item");
            result = Event.Result.DENY;
        } else if (cursor != null && cursor.getAmount() < 0) {
            player.sendMessage("Empty cursor");
            result = Event.Result.DENY;
        } else {
            result = trade.slotCheck(player, event.getRawSlot(), inventory);
        }
        
        event.setResult(result);
    }

    /**
     * Handles an inventory close event
     *
     * @param event the event to handle
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();

        // do nothing if the player isn't trading
        if (!manager.isTrading(player)) {
            return;
        }
        
        Trade trade = manager.getTrade(player);
        if (player.equals(trade.initiator.getPlayer()) && trade.initiator.getState().equals(TradeState.PREPARE)) {
            return;
        }
        if (player.equals(trade.target.getPlayer()) && trade.target.getState().equals(TradeState.PREPARE)) {
            return;
        }

        // abort trade
        trade.abort();
    }
}
