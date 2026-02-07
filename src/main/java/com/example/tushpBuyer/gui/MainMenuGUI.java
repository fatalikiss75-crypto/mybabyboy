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

public class MainMenuGUI {
    private final TushpBuyer plugin;

    // Слоты для размещения предметов на продажу (28 слотов)
    public static final int[] SELL_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,  // Ряд 1
            19, 20, 21, 22, 23, 24, 25,  // Ряд 2
            28, 29, 30, 31, 32, 33, 34,  // Ряд 3
            37, 38, 39, 40, 41, 42, 43   // Ряд 4
    };

    public static final int INFO_SLOT = 45;        // Книга с пером
    public static final int AUTO_BUYER_SLOT = 46;  // Звезда незера
    public static final int SHOP_SLOT = 49;        // Магнетит
    public static final int SELL_BUTTON_SLOT = 52; // Лаймовая краска

    public MainMenuGUI(TushpBuyer plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§8⎡ §6§lСКУПЩИК §8⎦");

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Декорация
        fillDecorative(inventory);

        // Книга с информацией (слот 45)
        inventory.setItem(INFO_SLOT, createInfoBook(data));

        // Кнопка автоскупщика (слот 46)
        inventory.setItem(AUTO_BUYER_SLOT, createAutoBuyerButton(data));

        // Магазин за очки скупщика (слот 49)
        inventory.setItem(SHOP_SLOT, createShopButton(data));

        // Кнопка сдачи всех предметов (слот 52)
        inventory.setItem(SELL_BUTTON_SLOT, createSellButton(data));

        // Слоты для размещения предметов остаются пустыми (10-16, 19-25, 28-34, 37-43)

        player.openInventory(inventory);
    }

    private ItemStack createInfoBook(PlayerData data) {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setDisplayName("§6§lТЕКУЩИЙ МНОЖИТЕЛЬ");

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7В этом меню Вы можете обменивать");
        lore.add("§7предметы на монеты. Больше монет");
        lore.add("§7и быстрее повышают §6НОМЕР КОЭФФИЦИЕНТ§7,");
        lore.add("§7Следовательно, которые легко добыть.");
        lore.add("§7Некоторые ресурсы обменять на");
        lore.add("§7МОНЕТЫ НЕВОЗМОЖНО.");
        lore.add("");
        lore.add("§7В основном можно сдавать след. предметы:");
        lore.add("§7из мобов: Ресурсы с §6шахты§7,");
        lore.add("§7растения и §6древесину§7. Чем больше и");
        lore.add("§7ценнее предметов вы сдаете, тем выше");
        lore.add("§7становится ваш множитель монет");
        lore.add("");

        // Показываем текущие множители по категориям
        for (BuyerCategory category : BuyerCategory.values()) {
            int level = data.getLevel(category);
            double multiplier = data.getPriceMultiplier(category);
            lore.add("§8⚊ §e" + category.getDisplayName() + "§8: §6×" + String.format("%.2f", multiplier) + " §8(ур. " + level + ")");
        }

        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createAutoBuyerButton(PlayerData data) {
        boolean enabled = data.isAutoBuyerEnabled();
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§e§l▶ §7Автоскупщик");

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Нажмите чтобы перейти в меню");
        lore.add("§7автоматической продажи предметов");
        lore.add("");
        lore.add("§7Статус: " + (enabled ? "§aВКЛЮЧЕН" : "§cВЫКЛЮЧЕН"));
        lore.add("§7Активных категорий: §e" + data.getActiveAutoBuyerCategories() + "§8/§75");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для перехода");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createShopButton(PlayerData data) {
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta meta = item.getItemMeta();

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setDisplayName("§d§l✦ МАГАЗИН ЗА ОЧКИ §d§l✦");

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Покупайте уникальные предметы");
        lore.add("§7за §dОчки Скупщика§7!");
        lore.add("");
        lore.add("§8⚊ §7Ваши очки§8: §d" + String.format("%,d", data.getBuyerPoints()) + " §7ОС");
        lore.add("");
        lore.add("§7Очки начисляются автоматически");
        lore.add("§7при продаже предметов скупщику");
        lore.add("§8(§7примерно §e0.001% §7от суммы сделки§8)");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для перехода");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createSellButton(PlayerData data) {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setDisplayName("§a§l✓ СДАТЬ ВСЕ ПРЕДМЕТЫ");

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Нажмите, чтобы сдать все предметы");
        lore.add("§7из слотов выше и получить");
        lore.add("§7на баланс §6монеты §7и §dочки§7!");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для сдачи");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private void fillDecorative(Inventory inventory) {
        // Верхняя граница (строка 0: слоты 0-8)
        ItemStack topBorder = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, topBorder);
        }

        // Боковые границы для рядов 1-4 (слоты 9, 17, 18, 26, 27, 35, 36, 44)
        ItemStack sideBorder = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        int[] sides = {9, 17, 18, 26, 27, 35, 36, 44};
        for (int slot : sides) {
            inventory.setItem(slot, sideBorder);
        }

        // Нижний ряд (строка 5): серые стеклянные панели на слотах 47, 48, 50, 51, 53
        ItemStack bottomBorder = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        int[] bottomSlots = {47, 48, 50, 51, 53};
        for (int slot : bottomSlots) {
            inventory.setItem(slot, bottomBorder);
        }
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSellSlot(int slot) {
        for (int sellSlot : SELL_SLOTS) {
            if (slot == sellSlot) return true;
        }
        return false;
    }
}