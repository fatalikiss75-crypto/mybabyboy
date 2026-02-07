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

public class AutoBuyerSettingsGUI {
    private final TushpBuyer plugin;

    public AutoBuyerSettingsGUI(TushpBuyer plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, "§8⎡ §a§l⚡ НАСТРОЙКА АВТОСКУПЩИКА §8⎦");
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Декорация
        fillDecorative(inventory);

        // Информация (слот 4)
        inventory.setItem(4, createInfoItem(data));

        // Категории (слоты 20, 22, 24)
        inventory.setItem(20, createCategoryToggle(BuyerCategory.CROPS, data));
        inventory.setItem(22, createCategoryToggle(BuyerCategory.MINER, data));
        inventory.setItem(24, createCategoryToggle(BuyerCategory.MOB_LOOT, data));

        // Кнопка "Включить всё" (слот 38)
        inventory.setItem(38, createEnableAllButton());

        // Кнопка "Выключить всё" (слот 39)
        inventory.setItem(39, createDisableAllButton());

        // Кнопка назад (слот 40)
        inventory.setItem(40, createBackButton());

        player.openInventory(inventory);
    }

    private ItemStack createCategoryToggle(BuyerCategory category, PlayerData data) {
        boolean enabled = data.isAutoBuyerEnabledForCategory(category);
        ItemStack item = new ItemStack(category.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        if (enabled) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        meta.setDisplayName(enabled ? 
            "§a§l✓ " + category.getDisplayName() + " §8┃ §2§lВКЛ" : 
            "§c§l✗ " + category.getDisplayName() + " §8┃ §4§lВЫКЛ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        
        if (enabled) {
            lore.add("§a§l✓ §7Автопродажа §aактивна");
            lore.add("§8⚊ §7Предметы этой категории будут");
            lore.add("§8⚊ §7автоматически продаваться");
        } else {
            lore.add("§c§l✗ §7Автопродажа §cвыключена");
            lore.add("§8⚊ §7Предметы этой категории");
            lore.add("§8⚊ §7не будут продаваться");
        }
        
        lore.add("");
        lore.add("§7Примеры предметов§8:");
        
        // Показываем несколько примеров предметов
        List<String> examples = getExampleItems(category);
        for (String example : examples) {
            lore.add("§8  • §f" + example);
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для переключения");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private List<String> getExampleItems(BuyerCategory category) {
        List<String> examples = new ArrayList<>();
        switch (category) {
            case CROPS:
                examples.add("Пшеница, Морковь, Картофель");
                examples.add("Арбуз, Тыква, Сахарный тростник");
                examples.add("Адский нарост, Бамбук");
                break;
            case MINER:
                examples.add("Уголь, Железо, Золото");
                examples.add("Алмазы, Изумруды");
                examples.add("Незерит, Кварц");
                break;
            case MOB_LOOT:
                examples.add("Кости, Нить, Порох");
                examples.add("Стержень ифрита, Эндер-жемчуг");
                examples.add("Панцирь шалкера");
                break;
        }
        return examples;
    }

    private ItemStack createInfoItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§e§l⚙ ИНФОРМАЦИЯ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Настройте категории для");
        lore.add("§7автоматической продажи предметов");
        lore.add("");
        lore.add("§a§lАктивно§8: §f" + data.getActiveAutoBuyerCategories() + " §8/ §73");
        lore.add("");
        lore.add("§7Включённые категории будут");
        lore.add("§7автоматически продавать предметы");
        lore.add("§7при поднятии с земли");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createEnableAllButton() {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§a§l✓ ВКЛЮЧИТЬ ВСЁ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Включить автопродажу для");
        lore.add("§7всех категорий сразу");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для включения");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createDisableAllButton() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§c§l✗ ВЫКЛЮЧИТЬ ВСЁ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Выключить автопродажу для");
        lore.add("§7всех категорий сразу");
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для выключения");
        
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
        // Верхняя граница
        ItemStack topBorder = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, topBorder);
        }
        
        // Нижняя граница
        ItemStack bottomBorder = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 36; i < 45; i++) {
            if (i != 38 && i != 39 && i != 40) {
                inventory.setItem(i, bottomBorder);
            }
        }
        
        // Боковые границы
        ItemStack sideBorder = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        int[] sides = {9, 18, 27, 17, 26, 35};
        for (int slot : sides) {
            inventory.setItem(slot, sideBorder);
        }
        
        // Декоративные элементы
        ItemStack limeGlass = createGlassPane(Material.LIME_STAINED_GLASS_PANE, "§a✦");
        inventory.setItem(19, limeGlass);
        inventory.setItem(21, limeGlass);
        inventory.setItem(23, limeGlass);
        inventory.setItem(25, limeGlass);
        
        ItemStack greenGlass = createGlassPane(Material.GREEN_STAINED_GLASS_PANE, "§2◆");
        inventory.setItem(10, greenGlass);
        inventory.setItem(11, greenGlass);
        inventory.setItem(15, greenGlass);
        inventory.setItem(16, greenGlass);
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
