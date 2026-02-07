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
import java.util.Map;

public class CategoryGUI {
    private final TushpBuyer plugin;

    public CategoryGUI(TushpBuyer plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, BuyerCategory category) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§8⎡ §6§l⚝ " + category.getDisplayName().toUpperCase() + " §8⎦");
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Map<Material, Double> prices = plugin.getConfigManager().getPrices(category);

        // Декорация
        fillBorders(inventory);

        // Информация о категории (слот 4)
        inventory.setItem(4, createCategoryInfoItem(category, data));

        // Режим продажи (слот 48)
        inventory.setItem(48, createSellModeItem(data));

        // Кнопка назад (слот 49)
        inventory.setItem(49, createBackButton());

        // Продаваемые предметы (начиная со слота 10)
        int slot = 10;
        for (Material material : prices.keySet()) {
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
            if (slot >= 44) break;

            inventory.setItem(slot, createSellableItem(material, category, data));
            slot++;
        }

        player.openInventory(inventory);
    }

    private ItemStack createCategoryInfoItem(BuyerCategory category, PlayerData data) {
        ItemStack item = new ItemStack(category.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        int level = data.getLevel(category);
        long exp = data.getExperience(category);
        long requiredExp = data.getRequiredExperience(level);
        double multiplier = data.getPriceMultiplier(category);
        
        meta.setDisplayName("§6§l⚝ " + category.getDisplayName().toUpperCase() + " §6§l⚝");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§8⚊ §7Ваш уровень§8: §e" + level + " §8/ §6" + data.getMaxLevel());
        
        if (level < data.getMaxLevel()) {
            double progress = (double) exp / requiredExp * 100;
            lore.add("§8⚊ §7Прогресс§8: §a" + formatNumber(exp) + " §8/ §e" + formatNumber(requiredExp));
            lore.add("  " + createProgressBar(exp, requiredExp) + " §6" + String.format("%.1f", progress) + "%");
            lore.add("");
            lore.add("§8⚊ §7До следующего§8: §e" + formatNumber(requiredExp - exp) + " опыта");
        } else {
            lore.add("§8⚊ §a§lМАКСИМАЛЬНЫЙ УРОВЕНЬ ДОСТИГНУТ§8!");
            lore.add("");
        }
        
        lore.add("");
        lore.add("§8⚊ §7Текущий множитель§8: §6✦ " + String.format("%.2f", multiplier) + "x");
        
        if (level < data.getMaxLevel()) {
            double nextMultiplier = data.getNextLevelMultiplier(level);
            lore.add("§8⚊ §7Следующий множитель§8: §6✦ " + String.format("%.2f", nextMultiplier) + "x");
            lore.add("§8⚊ §7Прирост§8: §a+" + String.format("%.2f", (nextMultiplier - multiplier)) + "x");
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createSellableItem(Material material, BuyerCategory category, PlayerData data) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        double basePrice = plugin.getConfigManager().getBasePrice(category, material);
        double finalPrice = basePrice * data.getPriceMultiplier(category);
        
        String itemName = getItemName(material);
        meta.setDisplayName("§e§l" + itemName);
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§8⚊ §7Базовая цена§8: §f" + String.format("%.1f", basePrice) + " §7монет");
        lore.add("§8⚊ §7Ваша цена§8: §6" + String.format("%.1f", finalPrice) + " §7монет §8(§6×" + String.format("%.2f", data.getPriceMultiplier(category)) + "§8)");
        lore.add("");
        
        // Расчет для разных режимов продажи
        PlayerData.SellMode mode = data.getSellMode();
        if (mode == PlayerData.SellMode.ALL) {
            lore.add("§7Режим§8: §a§lСдать всё");
            lore.add("§8⚊ §7Будет продано всё из инвентаря");
        } else {
            int amount = mode.getAmount();
            double totalPrice = finalPrice * amount;
            lore.add("§7Режим§8: §e" + mode.getDisplayName());
            lore.add("§8⚊ §7За §f" + amount + " §7шт§8: §6" + String.format("%.1f", totalPrice) + " §7монет");
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7ЛКМ §8- §fПродать (режим)");
        lore.add("§e§l▸ §7ПКМ §8- §fБыстрая продажа ×64");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createSellModeItem(PlayerData data) {
        PlayerData.SellMode mode = data.getSellMode();
        ItemStack item = new ItemStack(Material.COMPARATOR);
        ItemMeta meta = item.getItemMeta();
        
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        meta.setDisplayName("§a§l⚙ РЕЖИМ ПРОДАЖИ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Текущий§8: §e§l" + mode.getDisplayName());
        lore.add("");
        
        for (PlayerData.SellMode sellMode : PlayerData.SellMode.values()) {
            if (sellMode == mode) {
                lore.add("§a§l▸ §f" + sellMode.getDisplayName() + " §a§l← Активно");
            } else {
                lore.add("§8  • §7" + sellMode.getDisplayName());
            }
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для изменения");
        
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

    private void fillBorders(Inventory inventory) {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        // Верхний ряд
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glass);
        }
        
        // Нижний ряд
        for (int i = 45; i < 54; i++) {
            if (i != 48 && i != 49) {
                inventory.setItem(i, glass);
            }
        }
        
        // Боковые стороны
        ItemStack sideBorder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta sideMeta = sideBorder.getItemMeta();
        sideMeta.setDisplayName(" ");
        sideBorder.setItemMeta(sideMeta);
        
        int[] sides = {9, 17, 18, 26, 27, 35, 36, 44};
        for (int slot : sides) {
            inventory.setItem(slot, sideBorder);
        }
    }

    private String createProgressBar(long current, long max) {
        int bars = 20;
        int filled = (int) ((double) current / max * bars);
        
        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("§6▌");
            } else {
                bar.append("§7▌");
            }
        }
        bar.append("§8]");
        
        return bar.toString();
    }

    private String formatNumber(long number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        }
        return String.valueOf(number);
    }

    private String getItemName(Material material) {
        return switch (material) {
            // Фермер
            case SWEET_BERRIES -> "Сладкие ягоды";
            case NETHER_WART -> "Адский нарост";
            case BAMBOO -> "Бамбук";
            case MELON_SLICE -> "Ломтик арбуза";
            case POTATO -> "Картофель";
            case CARROT -> "Морковь";
            case COCOA_BEANS -> "Какао-бобы";
            case WHEAT -> "Пшеница";
            case BEETROOT -> "Свёкла";
            case SUGAR_CANE -> "Сахарный тростник";
            case PUMPKIN -> "Тыква";
            case MELON -> "Арбуз";
            case CACTUS -> "Кактус";
            case CHORUS_FRUIT -> "Плод хоруса";
            // Шахтёр
            case COAL -> "Уголь";
            case LAPIS_LAZULI -> "Лазурит";
            case REDSTONE -> "Красная пыль";
            case RAW_IRON -> "Необработанное железо";
            case IRON_INGOT -> "Железный слиток";
            case RAW_GOLD -> "Необработанное золото";
            case GOLD_INGOT -> "Золотой слиток";
            case DIAMOND -> "Алмаз";
            case EMERALD -> "Изумруд";
            case ANCIENT_DEBRIS -> "Древние обломки";
            case NETHERITE_SCRAP -> "Незеритовый обломок";
            case NETHERITE_INGOT -> "Незеритовый слиток";
            case QUARTZ -> "Кварц Нижнего мира";
            // Лут с мобов
            case ROTTEN_FLESH -> "Гнилая плоть";
            case ARROW -> "Стрела";
            case BONE -> "Кость";
            case STRING -> "Нить";
            case SPIDER_EYE -> "Паучий глаз";
            case BLAZE_ROD -> "Стержень ифрита";
            case SLIME_BALL -> "Сгусток слизи";
            case MAGMA_CREAM -> "Сгусток магмы";
            case COOKED_PORKCHOP -> "Жареная свинина";
            case COOKED_MUTTON -> "Жареная баранина";
            case LEATHER -> "Кожа";
            case ENDER_PEARL -> "Эндер-жемчуг";
            case GUNPOWDER -> "Порох";
            case GLOWSTONE_DUST -> "Светокаменная пыль";
            case GHAST_TEAR -> "Слеза гаста";
            case WITHER_SKELETON_SKULL -> "Череп визер-скелета";
            case SHULKER_SHELL -> "Панцирь шалкера";
            default -> material.name();
        };
    }
}
