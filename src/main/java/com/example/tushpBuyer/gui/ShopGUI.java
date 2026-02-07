package com.example.tushpBuyer.gui;

import com.example.tushpBuyer.TushpBuyer;
import com.example.tushpBuyer.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShopGUI {
    private final TushpBuyer plugin;
    
    public static final int BACK_SLOT = 49;
    public static final int INFO_SLOT = 4;

    public ShopGUI(TushpBuyer plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, 
            "§8⎡ §d§lМАГАЗИН ЗА ОЧКИ §8⎦");
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Декорация
        fillDecorative(inventory);

        // Информационный предмет
        inventory.setItem(INFO_SLOT, createInfoItem(data));

        // Загружаем предметы из конфигурации
        loadShopItems(inventory, data);

        // Кнопка назад
        inventory.setItem(BACK_SLOT, createBackButton());

        player.openInventory(inventory);
    }

    private void loadShopItems(Inventory inventory, PlayerData data) {
        ConfigurationSection shopSection = plugin.getConfigManager().getShopConfig()
            .getConfigurationSection("shop_items");
        
        if (shopSection == null) {
            return;
        }

        int slot = 19; // Начинаем со второй строки
        Set<String> keys = shopSection.getKeys(false);
        
        for (String key : keys) {
            if (slot >= 35) break; // Ограничиваем количество слотов
            if (slot % 9 == 0 || slot % 9 == 8) {
                slot++; // Пропускаем боковые границы
                continue;
            }
            
            ConfigurationSection itemSection = shopSection.getConfigurationSection(key);
            if (itemSection == null) continue;
            
            try {
                Material material = Material.valueOf(itemSection.getString("material", "STONE"));
                int amount = itemSection.getInt("amount", 1);
                long price = itemSection.getLong("price", 1000000);
                String displayName = itemSection.getString("display_name", material.name());
                List<String> description = itemSection.getStringList("description");
                
                inventory.setItem(slot, createShopItem(material, amount, price, displayName, description, data));
                slot++;
            } catch (Exception e) {
                plugin.getLogger().warning("Ошибка загрузки предмета из магазина: " + key);
            }
        }
    }

    private ItemStack createShopItem(Material material, int amount, long price, 
                                     String displayName, List<String> description, PlayerData data) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        boolean canAfford = data.getBuyerPoints() >= price;
        
        if (canAfford) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        meta.setDisplayName("§6§l" + displayName);
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        
        for (String line : description) {
            lore.add("§7" + line);
        }
        
        lore.add("");
        lore.add("§8⚊ §7Цена: §d" + String.format("%,d", price) + " §7ОС");
        lore.add("§8⚊ §7Количество: §e" + amount + " §7шт.");
        lore.add("");
        
        if (canAfford) {
            lore.add("§a§l✓ §7У вас достаточно очков!");
        } else {
            lore.add("§c§l✗ §7Недостаточно очков");
            lore.add("§8⚊ §7Нужно ещё: §c" + String.format("%,d", price - data.getBuyerPoints()) + " §7ОС");
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add(canAfford ? "§e§l▸ §7Нажмите для покупки" : "§8Недоступно для покупки");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createInfoItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        meta.setDisplayName("§d§l✦ ИНФОРМАЦИЯ О МАГАЗИНЕ §d§l✦");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Добро пожаловать в магазин");
        lore.add("§7за §dОчки Скупщика§7!");
        lore.add("");
        lore.add("§8⚊ §7Ваш баланс§8: §d" + String.format("%,d", data.getBuyerPoints()) + " §7ОС");
        lore.add("");
        lore.add("§7Очки начисляются автоматически");
        lore.add("§7при продаже предметов скупщику");
        lore.add("§8(§7примерно §e0.001% §7от суммы сделки§8)");
        lore.add("");
        lore.add("§7Здесь вы можете купить:");
        lore.add("§8  • §eУникальные предметы");
        lore.add("§8  • §eРедкие ресурсы");
        lore.add("§8  • §eПолезные вещи");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
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
            if (i != BACK_SLOT - 45) {
                inventory.setItem(45 + i, border);
            }
        }
        
        // Боковые границы
        ItemStack sideBorder = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int row = 1; row < 5; row++) {
            inventory.setItem(row * 9, sideBorder);
            inventory.setItem(row * 9 + 8, sideBorder);
        }
        
        // Декоративные элементы
        ItemStack purpleGlass = createGlassPane(Material.PURPLE_STAINED_GLASS_PANE, "§d✦");
        inventory.setItem(1, purpleGlass);
        inventory.setItem(7, purpleGlass);
        inventory.setItem(10, purpleGlass);
        inventory.setItem(16, purpleGlass);
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static long getItemPrice(Inventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        if (item == null || !item.hasItemMeta()) return 0;
        
        List<String> lore = item.getItemMeta().getLore();
        if (lore == null) return 0;
        
        for (String line : lore) {
            if (line.contains("Цена:")) {
                try {
                    String priceStr = line.replaceAll("[^0-9]", "");
                    return Long.parseLong(priceStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
