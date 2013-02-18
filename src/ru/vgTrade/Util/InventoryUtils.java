/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.vgTrade.Util;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Muson
 */
public class InventoryUtils {
    static JavaPlugin plugin = null;
    
    static final public Integer[] LEFT_SLOTS = {0,1,2,3,9,10,11,12,18,19,20,21,27,28,29};
            //{0,1,2,9,10,11,12,18,19,20,21,27,28,29,30};
    static final public Integer[] RIGHT_SLOTS = {5,6,7,8,14,15,16,17,23,24,25,26,33,34,35};
            //{6,7,8,14,15,16,17,23,24,25,26,32,33,34,35};
    static final public Integer[] BUTTON_SLOTS = {4,13,22,30,31,32};
    static final public Integer CONFIRM_SLOT = 4;
    static final public Integer DECLINE_SLOT = 22;
    static final public Integer P1_CONFIRM_SLOT = 30;
    static final public Integer P2_CONFIRM_SLOT = 32;
    
    public static void setPlugin(JavaPlugin pl) {
        plugin = pl;
    }
    
    public static Inventory createInventory(String id) {
        Inventory inv = null;
        
        if (plugin != null) {
            inv = plugin.getServer().createInventory(null, 36, id);
            ItemStack is = new ItemStack(Material.WOOL, 1);
            ItemMeta im = null;
            ArrayList<String> al = new ArrayList<String>();
            
            for (int i=0; i<BUTTON_SLOTS.length; i++) {
                is = new ItemStack(Material.STICK);
                im = is.getItemMeta();
                im.setDisplayName("---");
                is.setItemMeta(im);
                inv.setItem(BUTTON_SLOTS[i], is);
            }
            
            is = new Wool(DyeColor.LIME).toItemStack();
            im = is.getItemMeta();
            im.setDisplayName("Confirm trade!");
            al.clear(); al.add(ChatColor.GREEN+"Click this block to confirm trade");
            im.setLore(al);
            is.setItemMeta(im);
            inv.setItem(CONFIRM_SLOT, is);
            
            is = new Wool(DyeColor.RED).toItemStack();
            im = is.getItemMeta();
            im.setDisplayName("Cancel trade");
            al.clear(); al.add(ChatColor.RED+"Click this block to cancal trade");
            im.setLore(al);
            is.setItemMeta(im);
            inv.setItem(DECLINE_SLOT, is);
            
            is = new Wool(DyeColor.BLACK).toItemStack();
            im = is.getItemMeta();
            im.setDisplayName("Player trade status");
            al.clear(); al.add(ChatColor.WHITE+"Status of trade confirmation");
            al.add(ChatColor.GREEN+"Player 1");
            im.setLore(al);
            is.setItemMeta(im);
            inv.setItem(P1_CONFIRM_SLOT, is);
            
            is = new Wool(DyeColor.BLACK).toItemStack();
            im = is.getItemMeta();
            im.setDisplayName("Player trade status");
            al.clear(); al.add(ChatColor.WHITE+"Status of trade confirmation");
            al.add(ChatColor.GREEN+"Player 2");
            im.setLore(al);
            is.setItemMeta(im);
            inv.setItem(P2_CONFIRM_SLOT, is);
        }
        
        return inv;
    }
    
    public static ItemStack[] getLeftContents(Inventory inv) {
        ItemStack[] items = null;
        
        if (inv == null) {
            Log.severe("getLeftContents: inventory is null");
            return items;
        }
        
        int count = 0;
        ItemStack[] ist = inv.getContents();
        if (ist == null) {
            return items;
        }
        
        for (int i=0; i<LEFT_SLOTS.length; i++) {
            ItemStack is = ist[LEFT_SLOTS[i]];
            if (is != null && is.getType() != Material.AIR)
                count ++;
        }
        
        if (count > 0) {
            items = new ItemStack[count];
            int num = 0;
        
            for (int i=0; i<LEFT_SLOTS.length; i++) {
                ItemStack is = ist[LEFT_SLOTS[i]];
                if (is != null && is.getType() != Material.AIR)
                {
                    items[num++] = is;
                }
            }
        }
        
        return items;
    }
    
    public static ItemStack[] getRightContents(Inventory inv) {
        ItemStack[] items = null;
        
        if (inv == null) {
            Log.severe("getRightContents: inventory is null");
            return items;
        }
        
        int count = 0;
        ItemStack[] ist = inv.getContents();
        if (ist == null) {
            return items;
        }
        
        for (int i=0; i<RIGHT_SLOTS.length; i++) {
            ItemStack is = ist[RIGHT_SLOTS[i]];
            if (is != null && is.getType() != Material.AIR)
                count ++;
        }
        
        if (count > 0) {
            items = new ItemStack[count];
            int num = 0;
        
            for (int i=0; i<RIGHT_SLOTS.length; i++) {
                ItemStack is = ist[RIGHT_SLOTS[i]];
                if (is != null && is.getType() != Material.AIR)
                {
                    items[num++] = is;
                }
            }
        }
        
        return items;
    }
}
