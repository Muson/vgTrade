package ru.vgTrade.Trade;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import ru.vgTrade.Config.LanguageManager;
import ru.vgTrade.Util.InventoryUtils;
import ru.vgTrade.Util.Log;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 30/10/11
 */
public class Trade {

    public final TradePlayer initiator;
    public final TradePlayer target;

    private final Inventory inventory;
    private final Inventory initiatorInv;
    private final Inventory targetInv;
    
    private final String inventoryId;

    private TradeManager manager;

    private int cancellerID;

    public Trade(TradeRequest request, TradeManager manager) {
        this.initiator = request.initiator;
        this.target = request.target;
        this.manager = manager;
        
        inventoryId = initiator.getName()+" - "+target.getName();

        inventory = InventoryUtils.createInventory(this, inventoryId);
        
        initiatorInv = InventoryUtils.createPlayerInventory(initiator.getPlayer());
        targetInv = InventoryUtils.createPlayerInventory(target.getPlayer());

        target.getPlayer().openInventory(targetInv);
        initiator.getPlayer().openInventory(initiatorInv);

        Log.trade(initiator.getName() + " began trading with " + target.getName());
    }

    private void scheduleCancellation() {

        cancellerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(manager.vgTrade, new Runnable() {

            public void run() {
                abort();

                initiator.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TIMED));
                target.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TIMED));
                Log.trade("The trade between " + initiator.getName() + " and " + target.getName() + " timed out");
            }
        }, 120L);

    }

    private void unscheduleCancellation() {
        Bukkit.getServer().getScheduler().cancelTask(cancellerID);
    }

    public void abort() {

        if (!Bukkit.getServer().getScheduler().isCurrentlyRunning(cancellerID)) {
            unscheduleCancellation();
        }
        
        manager.finish(this);
        
        if (initiator.getState().equals(TradeState.CHEST_OPEN)) {
            initiator.restore(InventoryUtils.getTopContents(initiatorInv));
        } else {
            initiator.restore(InventoryUtils.getTopContents(inventory));
        }
        
        if (target.getState().equals(TradeState.CHEST_OPEN)) {
            target.restore(InventoryUtils.getTopContents(targetInv));
        } else {
            target.restore(InventoryUtils.getBottomContents(inventory));
        }

        target.getPlayer().closeInventory();
        initiator.getPlayer().closeInventory();

        Log.trade("The trade between " + initiator.getName() + " and " + target.getName() + " was aborted");

        sendMessage(LanguageManager.getString(LanguageManager.Strings.CANCELLED));
    }
    
    public void confirm(Player player) {
        confirm(player, null);
    }

    public void confirm(Player player, Integer slot) {
        ItemStack is = new Wool(DyeColor.YELLOW).toItemStack();
        ItemMeta im = null;
        ArrayList<String> al = new ArrayList<String>();

        if (player.equals(initiator.getPlayer())) {
            if (initiator.getState().equals(TradeState.PREPARE)) {
                initiator.setState(TradeState.CHEST_CLOSED);
                
                is =  new Wool(DyeColor.YELLOW).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+initiator.getName());
                al.add("Status: waiting for confirmation");
                im.setLore(al);
                is.setItemMeta(im);
                inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[0], is); 
                inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[1], is); 
            }
            
            if (slot != null) {
                is =  new Wool(DyeColor.LIME).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+initiator.getName());
                al.add("Status: confirmed");
                im.setLore(al);
                is.setItemMeta(im);
                    
                if (slot == 0 && initiator.getState().equals(TradeState.CHEST_CLOSED)) {
                    initiator.setState(TradeState.CONFIRM1);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[0], is); 
                }
                if (slot == 0 && initiator.getState().equals(TradeState.CONFIRM2)) {
                    initiator.setState(TradeState.CONFIRMED);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[0], is); 
                }
                if (slot == 1 && initiator.getState().equals(TradeState.CHEST_CLOSED)) {
                    initiator.setState(TradeState.CONFIRM2);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[1], is); 
                }
                if (slot == 1 && initiator.getState().equals(TradeState.CONFIRM1)) {
                    initiator.setState(TradeState.CONFIRMED);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[1], is); 
                }
            }
        } else {
            if (target.getState().equals(TradeState.PREPARE)) {
                target.setState(TradeState.CHEST_CLOSED);
                
                is =  new Wool(DyeColor.YELLOW).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+target.getName());
                al.add("Status: waiting for confirmation");
                im.setLore(al);
                is.setItemMeta(im);
                inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[0], is); 
                inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[1], is); 
            }
            
            if (slot != null) {
                is =  new Wool(DyeColor.LIME).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+target.getName());
                al.add("Status: confirmed");
                im.setLore(al);
                is.setItemMeta(im);
                    
                if (slot == 0 && target.getState().equals(TradeState.CHEST_CLOSED)) {
                    target.setState(TradeState.CONFIRM1);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[0], is); 
                }
                if (slot == 0 && target.getState().equals(TradeState.CONFIRM2)) {
                    target.setState(TradeState.CONFIRMED);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[0], is); 
                }
                if (slot == 1 && target.getState().equals(TradeState.CHEST_CLOSED)) {
                    target.setState(TradeState.CONFIRM2);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[1], is); 
                }
                if (slot == 1 && target.getState().equals(TradeState.CONFIRM1)) {
                    target.setState(TradeState.CONFIRMED);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[1], is); 
                }
            }
        }

        if (target.getState().equals(TradeState.CONFIRMED) && initiator.getState().equals(TradeState.CONFIRMED)) {
            unscheduleCancellation();
            doTrade();
        }
    }
    
    public void cancel(Player player) {
        cancel(player, null);
    }
    
    public void cancel(Player player, Integer slot) {
        ItemStack is = new Wool(DyeColor.YELLOW).toItemStack();
        ItemMeta im = null;
        ArrayList<String> al = new ArrayList<String>();

        if (player.equals(initiator.getPlayer())) {
            if (slot != null) {
                is =  new Wool(DyeColor.YELLOW).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+initiator.getName());
                al.add("Status: waiting for confirmation");
                im.setLore(al);
                is.setItemMeta(im);
                    
                if (slot == 0 && initiator.getState().equals(TradeState.CONFIRM1)) {
                    initiator.setState(TradeState.CHEST_CLOSED);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[0], is); 
                }
                if (slot == 0 && initiator.getState().equals(TradeState.CONFIRMED)) {
                    initiator.setState(TradeState.CONFIRM2);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[0], is); 
                }
                if (slot == 1 && initiator.getState().equals(TradeState.CONFIRM2)) {
                    initiator.setState(TradeState.CHEST_CLOSED);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[1], is); 
                }
                if (slot == 1 && initiator.getState().equals(TradeState.CONFIRMED)) {
                    initiator.setState(TradeState.CONFIRM1);
                    inventory.setItem(InventoryUtils.INITIATOR_STATUS_SLOTS[1], is); 
                }
            }
        } else {
            if (slot != null) {
                is =  new Wool(DyeColor.YELLOW).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+target.getName());
                al.add("Status: waiting for confirmation");
                im.setLore(al);
                is.setItemMeta(im);
                    
                if (slot == 0 && target.getState().equals(TradeState.CONFIRM1)) {
                    target.setState(TradeState.CHEST_CLOSED);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[0], is); 
                }
                if (slot == 0 && target.getState().equals(TradeState.CONFIRMED)) {
                    target.setState(TradeState.CONFIRM2);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[0], is); 
                }
                if (slot == 1 && target.getState().equals(TradeState.CONFIRM2)) {
                    target.setState(TradeState.CHEST_CLOSED);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[1], is); 
                }
                if (slot == 1 && target.getState().equals(TradeState.CONFIRMED)) {
                    target.setState(TradeState.CONFIRM1);
                    inventory.setItem(InventoryUtils.TARGET_STATUS_SLOTS[1], is); 
                }
            }
        }
    }

    public Event.Result slotCheck(Player player, int slot, Inventory inventoryToCheck) {
        if (inventoryToCheck.getTitle().equals("Items to trade") && player.equals(initiator.getPlayer())) {
            if (Arrays.asList(InventoryUtils.CONFIRM_SLOTS).contains(slot)) {
                ItemStack[] items = InventoryUtils.getTopContents(inventoryToCheck);
                
                if (items != null) {
                    for (int i=0; i<items.length; i++) {
                        inventory.setItem(InventoryUtils.TOP_SLOTS[0]+i, items[i]);
                    }
                }
                initiator.setState(TradeState.PREPARE);
                
                player.closeInventory();
                player.openInventory(inventory);
                
                confirm(player);
            }
            
            if ((slot >= InventoryUtils.PROTECT_SLOTS[0] && slot <= InventoryUtils.PROTECT_SLOTS[1])) {
                return Event.Result.DENY;
            }
            
            return Event.Result.ALLOW;
        } else 
        if (inventoryToCheck.getTitle().equals("Items to trade") && player.equals(target.getPlayer())) {
            if (Arrays.asList(InventoryUtils.CONFIRM_SLOTS).contains(slot)) {
                ItemStack[] items = InventoryUtils.getTopContents(inventoryToCheck);
                
                if (items != null) {
                    for (int i=0; i<items.length; i++) {
                        inventory.setItem(InventoryUtils.BOTTOM_SLOTS[0]+i, items[i]);
                    }
                }
                
                target.setState(TradeState.PREPARE);
                
                player.closeInventory();
                player.openInventory(inventory);
                
                confirm(player);
            }
            
            if ((slot >= InventoryUtils.PROTECT_SLOTS[0] && slot <= InventoryUtils.PROTECT_SLOTS[1])) {
                return Event.Result.DENY;
            }
            
            return Event.Result.ALLOW;
        } else 
            
        if (inventoryToCheck.getTitle().equals(inventoryId) && 
            initiator.getState() != TradeState.CHEST_OPEN && target.getState() != TradeState.CHEST_OPEN) {
            
            // Обработка кнопок
            if (Arrays.asList(InventoryUtils.CONFIRM_SLOTS).contains(slot)) {
                if (target.getState() != TradeState.CONFIRMED || initiator.getState() != TradeState.CONFIRMED) {

                    if (getUsedCases(InventoryUtils.getTopContents(inventoryToCheck)
                        ) > getEmptyCases(target.getInventory().getContents()) || 
                            getUsedCases(InventoryUtils.getBottomContents(inventoryToCheck)) > getEmptyCases(initiator.getInventory().getContents())) {
                        abort();
                        sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.NOROOM));
                        return Event.Result.DENY;
                    }
                    
                    if (slot == InventoryUtils.CONFIRM_SLOTS[0])
                        confirm(player, 0);
                    else 
                        confirm(player, 1);
                }
            }

            if (Arrays.asList(InventoryUtils.DECLINE_SLOTS).contains(slot)) {
                if (target.getState() != TradeState.CHEST_CLOSED || initiator.getState() != TradeState.CHEST_CLOSED) {

                    if (getUsedCases(InventoryUtils.getTopContents(inventoryToCheck)
                        ) > getEmptyCases(target.getInventory().getContents()) || 
                            getUsedCases(InventoryUtils.getBottomContents(inventoryToCheck)) > getEmptyCases(initiator.getInventory().getContents())) {
                        abort();
                        sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.NOROOM));
                        return Event.Result.DENY;
                    }
                    
                    if (slot == InventoryUtils.DECLINE_SLOTS[0])
                        cancel(player, 0);
                    else 
                        cancel(player, 1);
                }
            }
        }
        
        return Event.Result.DENY;
    }

    private void doTrade() {
        initiator.tradeFinish(InventoryUtils.getBottomContents(inventory), InventoryUtils.getTopContents(inventory));
        target.tradeFinish(InventoryUtils.getTopContents(inventory), InventoryUtils.getBottomContents(inventory));
        
        manager.finish(this);
        
        initiator.getPlayer().closeInventory();
        target.getPlayer().closeInventory();
        
        initiator.doTrade(InventoryUtils.getBottomContents(inventory));
        target.doTrade(InventoryUtils.getTopContents(inventory));
        
        Log.trade("The trade between " + initiator.getName() + " and " + target.getName() + " was completed");
        if (Log.verbose) {
            String tradeList = "";
            int i=0;
            ItemStack[] items = InventoryUtils.getBottomContents(inventory);
            if (items != null) {
                for (ItemStack is : items) {
                    if (i == 0) tradeList += ",";
                    tradeList += is.getType().name() + "x"+is.getAmount();
                    i++;
                }
            }
            Log.trade(initiator.getName()+" got:"+tradeList);
            tradeList = "";
            i = 0;
            items = InventoryUtils.getTopContents(inventory);
            if (items != null) {
                for (ItemStack is : items) {
                    if (i == 0) tradeList += ",";
                    tradeList += is.getType().name() + "x"+is.getAmount();
                }
            }
            Log.trade(target.getName()+" got:"+tradeList);
        }

        sendMessage(LanguageManager.getString(LanguageManager.Strings.FINISHED));
        
    }

    private int getEmptyCases(ItemStack[] contents) {
        int count = 0;
        if (contents == null) {
            return 0;
        }
        for (ItemStack content : contents) {
            if (content == null || content.getType() == Material.AIR) {
                count++;
            }
        }
        return count;
    }

    private int getUsedCases(ItemStack[] contents) {
        int count = 0;
        if (contents == null) {
            //Log.info("Close contents is empty!");
            return 0;
        }
        for (ItemStack content : contents) {
            if (content != null && content.getType() != Material.AIR) {
                count++;
            }
        }
        return count;
    }

    void sendMessage(String msg) {
        target.sendMessage(msg);
        initiator.sendMessage(msg);
    }

}
