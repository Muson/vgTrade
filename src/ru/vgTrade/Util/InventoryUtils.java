/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.vgTrade.Util;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vgTrade.Trade.Trade;

/**
 *
 * @author Muson
 */
public class InventoryUtils {
    static JavaPlugin plugin = null;
    
    static final public Integer[] TOP_SLOTS = {0,17};
    static final public Integer[] BOTTOM_SLOTS = {27,44};
            //{6,7,8,14,15,16,17,23,24,25,26,32,33,34,35};
    static final public Integer[] PROTECT_SLOTS = {18, 26, 45, 53};
    
    static final public Integer[] FACE_SLOTS = {18,45};
    static final public Integer[] CONFIRM_SLOTS = {25,52};
    static final public Integer[] DECLINE_SLOTS = {26,53};
    
    static final public Integer[] INITIATOR_STATUS_SLOTS = {21, 48};
    static final public Integer[] TARGET_STATUS_SLOTS = {22, 49};
    
    static final public Integer[] BUTTON_SLOTS = {4,13,22,30,31,32};
    
    public static void setPlugin(JavaPlugin pl) {
        plugin = pl;
    }
    
    public static Inventory createPlayerInventory(Player pl) {
        Inventory inv = null;
        
        if (plugin != null) {
            inv = plugin.getServer().createInventory(pl, 27, "Items to trade");
            ArrayList<String> al = new ArrayList<String>();
            
            ItemStack is = new ItemStack(Material.STICK);
            ItemMeta im = is.getItemMeta();
            
            is = new ItemStack(Material.SKULL_ITEM, 0, (short)SkullType.PLAYER.ordinal());
            SkullMeta sm = (SkullMeta)is.getItemMeta();
            sm.setOwner(pl.getName());
            sm.setDisplayName("Trader: "+pl.getName());
            is.setItemMeta(sm);
            inv.setItem(PROTECT_SLOTS[0], is);
            
            is = new Wool(DyeColor.LIME).toItemStack();
            im = is.getItemMeta();
            im.setDisplayName("Confirm trade");
            al.clear(); al.add("Click this block to confirm items to trade");
            im.setLore(al);
            is.setItemMeta(im);
            inv.setItem(PROTECT_SLOTS[1]-1, is);
        }
        
        return inv;
    }
    
    public static Inventory createInventory(Trade trade, String title) {
        Inventory inv = null;
        
        if (plugin != null && trade!=null) {
            inv = plugin.getServer().createInventory(null, 54, title);
            ItemStack is = new ItemStack(Material.WOOL, 1);
            ItemMeta im = null;
            ArrayList<String> al = new ArrayList<String>();            
            
            for (int i=0; i<PROTECT_SLOTS.length;) {
                is = new Wool(DyeColor.LIME).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm trade!");
                al.clear(); al.add(ChatColor.GREEN+"Click this block to confirm trading items");
                im.setLore(al);
                is.setItemMeta(im);
                inv.setItem(PROTECT_SLOTS[i+1]-1, is);
                
                is = new Wool(DyeColor.RED).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Cancel trade");
                al.clear(); al.add(ChatColor.RED+"Click this block to cancal trade");
                im.setLore(al);
                is.setItemMeta(im);
                inv.setItem(PROTECT_SLOTS[i+1], is);
                
                // Player that gets items
                is = new ItemStack(Material.SKULL_ITEM, 0, (short)SkullType.PLAYER.ordinal());
                SkullMeta sm = (SkullMeta)is.getItemMeta();
                if (i == 0) {
                    sm.setOwner(trade.target.getName());
                    sm.setDisplayName("Items goes to:");
                    al.clear(); al.add(ChatColor.GREEN+trade.target.getName());
                } else {
                    sm.setOwner(trade.initiator.getName());
                    sm.setDisplayName("Items goes to:");
                    al.clear(); al.add(ChatColor.GREEN+trade.initiator.getName());
                }
                sm.setLore(al);
                is.setItemMeta(sm);
                inv.setItem(PROTECT_SLOTS[i], is);
                
                // Confirmation block
                is = new Wool(DyeColor.RED).toItemStack();
                im = is.getItemMeta();
                im.setDisplayName("Confirm status");
                al.clear(); al.add(ChatColor.AQUA+trade.initiator.getName());
                al.add("Status: preparing items for trade");
                im.setLore(al);
                is.setItemMeta(im);
                inv.setItem(PROTECT_SLOTS[i]+3, is);
                
                al.clear(); al.add(ChatColor.AQUA+trade.target.getName());
                al.add("Status: preparing items for trade");
                im.setLore(al);
                is.setItemMeta(im);
                inv.setItem(PROTECT_SLOTS[i]+4, is);
                
                i+=2;
            }
        }
        
        return inv;
    }
    
    public static ItemStack[] getTopContents(Inventory inv) {
        ItemStack[] items = null;
        
        if (inv == null) {
            Log.severe("getTopContents: inventory is null");
            return items;
        }
        
        int count = 0;
        ItemStack[] ist = inv.getContents();
        if (ist == null) {
            return items;
        }
        
        for (int i=TOP_SLOTS[0]; i<TOP_SLOTS[1]; i++) {
            ItemStack is = ist[i];
            if (is != null && is.getType() != Material.AIR)
                count ++;
        }
        
        if (count > 0) {
            items = new ItemStack[count];
            int num = 0;
        
            for (int i=TOP_SLOTS[0]; i<TOP_SLOTS[1]; i++) {
                ItemStack is = ist[i];
                if (is != null && is.getType() != Material.AIR)
                {
                    items[num++] = is;
                }
            }
        }
        
        return items;
    }
    
    public static ItemStack[] getBottomContents(Inventory inv) {
        ItemStack[] items = null;
        
        if (inv == null) {
            Log.severe("getBottomContents: inventory is null");
            return items;
        }
        
        int count = 0;
        ItemStack[] ist = inv.getContents();
        if (ist == null) {
            return items;
        }
        
        for (int i=BOTTOM_SLOTS[0]; i<BOTTOM_SLOTS[1]; i++) {
            ItemStack is = ist[i];
            if (is != null && is.getType() != Material.AIR)
                count ++;
        }
        
        if (count > 0) {
            items = new ItemStack[count];
            int num = 0;
        
            for (int i=BOTTOM_SLOTS[0]; i<BOTTOM_SLOTS[1]; i++) {
                ItemStack is = ist[i];
                if (is != null && is.getType() != Material.AIR)
                {
                    items[num++] = is;
                }
            }
        }
        
        return items;
    }
}
