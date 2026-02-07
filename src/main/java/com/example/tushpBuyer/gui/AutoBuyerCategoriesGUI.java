package com.example.tushpBuyer.gui;

import com.example.tushpBuyer.TushpBuyer;
import com.example.tushpBuyer.data.BuyerCategory;
import com.example.tushpBuyer.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AutoBuyerCategoriesGUI {
    private final TushpBuyer plugin;
    
    public static final int MINER_SLOT = 11;
    public static final int MOB_LOOT_SLOT = 12;
    public static final int WOOD_SLOT = 13;
    public static final int POTIONS_SLOT = 14;
    public static final int CROPS_SLOT = 15;
    public static final int TOGGLE_AUTO_BUYER_SLOT = 31;
    public static final int BACK_SLOT = 40;

    public AutoBuyerCategoriesGUI(TushpBuyer plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§8⎡ §a§l⚡ АВТОСКУПЩИК §8⎦");
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Декорация
        fillDecorative(inventory);

        // Категории
        inventory.setItem(MINER_SLOT, createCategoryItem(BuyerCategory.MINER, data));
        inventory.setItem(MOB_LOOT_SLOT, createCategoryItem(BuyerCategory.MOB_LOOT, data));
        inventory.setItem(WOOD_SLOT, createCategoryItem(BuyerCategory.WOOD, data));
        inventory.setItem(POTIONS_SLOT, createCategoryItem(BuyerCategory.POTIONS, data));
        inventory.setItem(CROPS_SLOT, createCategoryItem(BuyerCategory.CROPS, data));

        // Кнопка вкл/выкл автоскупщика
        inventory.setItem(TOGGLE_AUTO_BUYER_SLOT, createToggleButton(data));

        // Кнопка назад
        inventory.setItem(BACK_SLOT, createBackButton());

        player.openInventory(inventory);
    }

    private ItemStack createCategoryItem(BuyerCategory category, PlayerData data) {
        ItemStack item = new ItemStack(category.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§e§l▶ §7" + category.getDisplayName());
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Нажмите, чтобы открыть");
        lore.add("§7настройки категории");
        lore.add("");
        lore.add("§7Уровень: §e" + data.getLevel(category));
        lore.add("§7Множитель: §6×" + String.format("%.2f", data.getPriceMultiplier(category)));
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для настройки");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createToggleButton(PlayerData data) {
        boolean enabled = data.isAutoBuyerEnabled();
        ItemStack item = new ItemStack(enabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        
        if (enabled) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        meta.setDisplayName(enabled ? 
            "§a§l✓ АВТОСКУПЩИК ВКЛЮЧЕН" : 
            "§c§l✗ АВТОСКУПЩИК ВЫКЛЮЧЕН");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        
        if (enabled) {
            lore.add("§a§l✓ §7Автоматическая продажа §aактивна§7!");
            lore.add("§8⚊ §7Предметы автоматически продаются");
            lore.add("§8⚊ §7при поднятии с земли");
        } else {
            lore.add("§c§l✗ §7Автоматическая продажа §cвыключена");
            lore.add("§8⚊ §7Включите для автопродажи");
            lore.add("§8⚊ §7предметов при получении");
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для переключения");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§c§l◄ НАЗАД");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Вернуться в главное меню");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private void fillDecorative(Inventory inventory) {
        // Верхняя и нижняя границы
        ItemStack border = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
            inventory.setItem(45 + i, border);
        }
        
        // Боковые границы
        ItemStack sideBorder = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        int[] sides = {9, 18, 27, 36, 17, 26, 35, 44};
        for (int slot : sides) {
            inventory.setItem(slot, sideBorder);
        }
        
        // Декоративные элементы
        ItemStack limeGlass = createGlassPane(Material.LIME_STAINED_GLASS_PANE, "§a✦");
        inventory.setItem(10, limeGlass);
        inventory.setItem(16, limeGlass);
        inventory.setItem(19, limeGlass);
        inventory.setItem(25, limeGlass);
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
