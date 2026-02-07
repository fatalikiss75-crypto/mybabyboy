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

import java.util.*;

public class CategoryItemsGUI {
    private final TushpBuyer plugin;
    private final BuyerCategory category;
    
    public static final int ENABLE_ALL_SLOT = 48;
    public static final int DISABLE_ALL_SLOT = 49;
    public static final int BACK_SLOT = 50;
    
    // Храним состояние включенности предметов для каждого игрока и категории
    private static final Map<UUID, Map<BuyerCategory, Set<Material>>> enabledItems = new HashMap<>();

    public CategoryItemsGUI(TushpBuyer plugin, BuyerCategory category) {
        this.plugin = plugin;
        this.category = category;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, 
            "§8⎡ §e" + category.getDisplayName() + " §8⎦");
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        // Получаем список предметов категории
        Set<Material> materials = plugin.getConfigManager().getAcceptedItems(category);
        List<Material> materialList = new ArrayList<>(materials);
        
        // Инициализируем включенные предметы для игрока если еще нет
        enabledItems.putIfAbsent(player.getUniqueId(), new HashMap<>());
        enabledItems.get(player.getUniqueId()).putIfAbsent(category, new HashSet<>());
        
        Set<Material> playerEnabledItems = enabledItems.get(player.getUniqueId()).get(category);

        // Декорация
        fillDecorative(inventory);

        // Размещаем предметы (слоты 10-43, кроме последнего ряда)
        int slot = 10;
        for (Material material : materialList) {
            if (slot >= 44) break; // Не заходим на последний ряд
            if (slot % 9 == 0 || slot % 9 == 8) {
                slot++; // Пропускаем боковые границы
                continue;
            }
            
            boolean enabled = playerEnabledItems.contains(material);
            inventory.setItem(slot, createItemToggle(material, enabled, data));
            slot++;
        }

        // Кнопки управления
        inventory.setItem(ENABLE_ALL_SLOT, createEnableAllButton());
        inventory.setItem(DISABLE_ALL_SLOT, createDisableAllButton());
        inventory.setItem(BACK_SLOT, createBackButton());

        player.openInventory(inventory);
    }

    private ItemStack createItemToggle(Material material, boolean enabled, PlayerData data) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (enabled) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        String itemName = getItemDisplayName(material);
        meta.setDisplayName(enabled ? 
            "§a§l✓ " + itemName : 
            "§c§l✗ " + itemName);
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        
        if (enabled) {
            lore.add("§a§l✓ §7Автопродажа §aВКЛ");
        } else {
            lore.add("§c§l✗ §7Автопродажа §cВЫКЛ");
        }
        
        double basePrice = plugin.getConfigManager().getBasePrice(category, material);
        double multiplier = data.getPriceMultiplier(category);
        double price = basePrice * multiplier;
        
        lore.add("");
        lore.add("§8⚊ §7Цена: §6" + String.format("%.2f", price) + " §7монет/шт");
        lore.add("§8⚊ §7Базовая: §e" + String.format("%.2f", basePrice));
        lore.add("§8⚊ §7Множитель: §6×" + String.format("%.2f", multiplier));
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§e§l▸ §7Нажмите для переключения");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private ItemStack createEnableAllButton() {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§a§l✓ ВКЛЮЧИТЬ ВСЕ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Включить автопродажу для");
        lore.add("§7всех предметов категории");
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
        
        meta.setDisplayName("§c§l✗ ВЫКЛЮЧИТЬ ВСЕ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("");
        lore.add("§7Выключить автопродажу для");
        lore.add("§7всех предметов категории");
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
        lore.add("§7Вернуться к категориям");
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
        for (int i = 45; i < 54; i++) {
            if (i != ENABLE_ALL_SLOT && i != DISABLE_ALL_SLOT && i != BACK_SLOT) {
                inventory.setItem(i, bottomBorder);
            }
        }
        
        // Боковые границы
        ItemStack sideBorder = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int row = 1; row < 5; row++) {
            inventory.setItem(row * 9, sideBorder);
            inventory.setItem(row * 9 + 8, sideBorder);
        }
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private String getItemDisplayName(Material material) {
        // Возвращаем русские названия предметов
        return switch (material) {
            // Руды
            case COAL -> "Уголь";
            case LAPIS_LAZULI -> "Лазурит";
            case REDSTONE -> "Красная пыль";
            case IRON_INGOT -> "Железный слиток";
            case GOLD_INGOT -> "Золотой слиток";
            case DIAMOND -> "Алмаз";
            case EMERALD -> "Изумруд";
            case NETHERITE_INGOT -> "Незеритовый слиток";
            case NETHERITE_SCRAP -> "Незеритовый лом";
            case ANCIENT_DEBRIS -> "Древний обломок";
            case QUARTZ -> "Кварц";
            case OBSIDIAN -> "Обсидиан";
            case END_STONE -> "Эндерняк";
            
            // Лут с мобов
            case ROTTEN_FLESH -> "Гнилая плоть";
            case BONE -> "Кость";
            case STRING -> "Нить";
            case SPIDER_EYE -> "Паучий глаз";
            case GUNPOWDER -> "Порох";
            case ENDER_PEARL -> "Эндер-жемчуг";
            case BLAZE_ROD -> "Стержень ифрита";
            case GHAST_TEAR -> "Слеза гаста";
            case MAGMA_CREAM -> "Сгусток магмы";
            case SLIME_BALL -> "Сгусток слизи";
            case WHITE_WOOL -> "Шерсть";
            case LEATHER -> "Кожа";
            case ARROW -> "Стрела";
            case COOKED_PORKCHOP -> "Жареная свинина";
            case COOKED_MUTTON -> "Жареная баранина";
            case GLOWSTONE_DUST -> "Светокаменная пыль";
            case WITHER_SKELETON_SKULL -> "Череп визер-скелета";
            case SHULKER_SHELL -> "Панцирь шалкера";
            case DRAGON_BREATH -> "Драконье дыхание";
            
            // Древесина
            case OAK_LOG -> "Дуб";
            case SPRUCE_LOG -> "Ель";
            case BIRCH_LOG -> "Берёза";
            case JUNGLE_LOG -> "Тропическое дерево";
            case ACACIA_LOG -> "Акация";
            case DARK_OAK_LOG -> "Тёмный дуб";
            case CRIMSON_STEM -> "Багровый стебель";
            case WARPED_STEM -> "Искажённый стебель";
            
            // Культуры
            case WHEAT -> "Пшеница";
            case POTATO -> "Картофель";
            case CARROT -> "Морковь";
            case GOLDEN_CARROT -> "Золотая морковь";
            case BEETROOT -> "Свёкла";
            case SWEET_BERRIES -> "Сладкие ягоды";
            case BAMBOO -> "Бамбук";
            case SUGAR_CANE -> "Сахарный тростник";
            case CACTUS -> "Кактус";
            case PUMPKIN -> "Тыква";
            case MELON -> "Арбуз";
            case MELON_SLICE -> "Ломтик арбуза";
            case COCOA_BEANS -> "Какао-бобы";
            case NETHER_WART -> "Адский нарост";
            case CHORUS_FRUIT -> "Плод хоруса";
            case CHORUS_FLOWER -> "Цветок хоруса";
            case KELP -> "Ламинария";
            
            // Зелья
            case POTION -> "Зелье";
            
            default -> material.name();
        };
    }

    public static void toggleItem(Player player, BuyerCategory category, Material material) {
        enabledItems.putIfAbsent(player.getUniqueId(), new HashMap<>());
        enabledItems.get(player.getUniqueId()).putIfAbsent(category, new HashSet<>());
        
        Set<Material> items = enabledItems.get(player.getUniqueId()).get(category);
        if (items.contains(material)) {
            items.remove(material);
        } else {
            items.add(material);
        }
    }

    public static void enableAll(Player player, BuyerCategory category, Set<Material> materials) {
        enabledItems.putIfAbsent(player.getUniqueId(), new HashMap<>());
        enabledItems.get(player.getUniqueId()).put(category, new HashSet<>(materials));
    }

    public static void disableAll(Player player, BuyerCategory category) {
        enabledItems.putIfAbsent(player.getUniqueId(), new HashMap<>());
        enabledItems.get(player.getUniqueId()).put(category, new HashSet<>());
    }

    public static boolean isItemEnabled(Player player, BuyerCategory category, Material material) {
        if (!enabledItems.containsKey(player.getUniqueId())) return false;
        if (!enabledItems.get(player.getUniqueId()).containsKey(category)) return false;
        return enabledItems.get(player.getUniqueId()).get(category).contains(material);
    }
}
