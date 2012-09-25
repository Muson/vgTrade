package ru.vgTrade.Trade;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.entity.Player;
import ru.vgTrade.Util.Log;
import ru.vgTrade.VgTrade;

/**
 * @author Muson (Original code: Oliver Brown (Arkel))
 */
public class TradeManager {

    HashMap<Player, TradeRequest> requests = new HashMap<Player, TradeRequest>();
    HashMap<Player, Trade> trades = new HashMap<Player, Trade>();
    VgTrade vgTrade;

    public TradeManager(VgTrade st) {
        this.vgTrade = st;
    }

    public void finish(TradeRequest request) {
        for (Iterator<Map.Entry<Player, TradeRequest>> iter = requests.entrySet().iterator();
                iter.hasNext();) {
            Map.Entry<Player, TradeRequest> entry = iter.next();
            if (request.equals(entry.getValue())) {
                iter.remove();
            }
        }
    }

    public void finish(Trade trade) {
        for (Iterator<Map.Entry<Player, Trade>> iter = trades.entrySet().iterator();
                iter.hasNext();) {
            Map.Entry<Player, Trade> entry = iter.next();
            if (trade.equals(entry.getValue())) {
                iter.remove();
            }
        }
    }

    public Trade getTrade(Player player) {
        if (trades.containsKey(player)) {
            return trades.get(player);
        }

        return null;
    }
    
    public TradeRequest getRequest(Player player) {
        if (requests.containsKey(player)) {
            return requests.get(player);
        }

        return null;
    }

    public void onPlayerQuit(Player player) {
        if (trades.containsKey(player)) {
            trades.get(player).abort();
        } else if (requests.containsKey(player)) {
            requests.get(player).decline();
        }
    }

    public void begin(TradePlayer player, TradePlayer target) {
        TradeRequest request = new TradeRequest(player, target, this);

        requests.put(player.getPlayer(), request);
        requests.put(target.getPlayer(), request);
    }

    public void progress(TradeRequest request) {
        finish(request);

        Trade trade = new Trade(request, this);
        trades.put(request.initiator.getPlayer(), trade);
        trades.put(request.target.getPlayer(), trade);
    }

    public boolean isBusy(Player player) {
        return trades.containsKey(player) || requests.containsKey(player);
    }

    public boolean isTrading(Player player) {
        return trades.containsKey(player);
    }

    public void handleCommand(String command, Player player) {
        if (trades.containsKey(player)) {
            if (command.equalsIgnoreCase("accept")) {
                trades.get(player).confirm(player);
            } else {
                trades.get(player).abort();
            }
        } else if (requests.containsKey(player)) {
            if (command.equalsIgnoreCase("decline")) {
                requests.get(player).decline();
            } else {
                requests.get(player).accept(player);
            }
        }
    }

    public void terminateActiveTrades() {

        if (!trades.isEmpty() || !requests.isEmpty()) {
            Log.warning("VgTrade detected that players were still trading. Attempting to cancel trades...");
            Player[] players = vgTrade.getServer().getOnlinePlayers();
            for (Player player : players) {
                if (trades.get(player) != null) {
                    trades.get(player).abort();
                } else if (requests.get(player) != null) {
                    requests.get(player).decline();
                }
            }
            Log.info("Trades cancelled");
        }

    }
}