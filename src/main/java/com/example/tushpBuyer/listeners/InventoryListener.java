package com.example.tushpBuyer.listeners;

import com.example.tushpBuyer.TushpBuyer;
import com.example.tushpBuyer.data.BuyerCategory;
import com.example.tushpBuyer.data.PlayerData;
import com.example.tushpBuyer.gui.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class InventoryListener implements Listener {
    private final TushpBuyer plugin;
    private final Map<Player, Long> sellCooldown;

    public InventoryListener(TushpBuyer plugin) {
        this.plugin = plugin;
        this.sellCooldown = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        // Главное меню скупщика
        if (title.contains("СКУПЩИК")) {
            handleMainMenuClick(player, event);
            return;
        }

        // Меню категорий автоскупщика
        if (title.contains("АВТОСКУПЩИК") && !title.contains("Ресурсы") && !title.contains("Древесина") &&
                !title.contains("Зелья") && !title.contains("Культуры")) {
            handleAutoBuyerCategoriesClick(player, event);
            return;
        }

        // Меню предметов категории (для автоскупщика)
        if (title.contains("Ресурсы из шахты") || title.contains("Ресурсы с мобов") ||
                title.contains("Древесина") || title.contains("Зелья") || title.contains("Культуры")) {
            handleCategoryItemsClick(player, event);
            return;
        }

        // Магазин за очки
        if (title.contains("МАГАЗИН ЗА ОЧКИ")) {
            handleShopMenuClick(player, event);
            return;
        }
    }

    private void handleMainMenuClick(Player player, InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Inventory inventory = event.getInventory();

        // Проверяем клики в инвентаре игрока
        if (slot >= 54) {
            event.setCancelled(false);
            return;
        }

        // Разрешаем размещать предметы в слоты продажи
        if (MainMenuGUI.isSellSlot(slot)) {
            ClickType clickType = event.getClick();

            if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT ||
                    clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT ||
                    clickType == ClickType.NUMBER_KEY || clickType == ClickType.SWAP_OFFHAND) {
                event.setCancelled(false);
                return;
            }
        }

        // Все остальные клики в GUI отменяем
        event.setCancelled(true);

        // Кнопка информации
        if (slot == MainMenuGUI.INFO_SLOT) {
            return;
        }

        // Кнопка автоскупщика
        if (slot == MainMenuGUI.AUTO_BUYER_SLOT) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new AutoBuyerCategoriesGUI(plugin).open(player);
            return;
        }

        // Кнопка магазина
        if (slot == MainMenuGUI.SHOP_SLOT) {
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
            new ShopGUI(plugin).open(player);
            return;
        }

        // Кнопка сдачи
        if (slot == MainMenuGUI.SELL_BUTTON_SLOT) {
            handleSellItems(player, inventory);
            return;
        }
    }

    private void handleAutoBuyerCategoriesClick(Player player, InventoryClickEvent event) {
        // КРИТИЧЕСКИ ВАЖНО: отменяем ВСЕ клики в этом GUI
        event.setCancelled(true);

        int slot = event.getRawSlot();

        // Блокируем ВСЕ клики в инвентаре игрока тоже
        if (slot >= 54) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Кнопка переключения автоскупщика
        if (slot == AutoBuyerCategoriesGUI.TOGGLE_AUTO_BUYER_SLOT) {
            data.setAutoBuyerEnabled(!data.isAutoBuyerEnabled());
            plugin.getPlayerDataManager().savePlayerData(data);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f,
                    data.isAutoBuyerEnabled() ? 2.0f : 0.5f);

            // Обновляем GUI
            new AutoBuyerCategoriesGUI(plugin).open(player);
            return;
        }

        // Кнопка назад
        if (slot == AutoBuyerCategoriesGUI.BACK_SLOT) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new MainMenuGUI(plugin).open(player);
            return;
        }

        // Открытие категорий
        BuyerCategory category = null;
        if (slot == AutoBuyerCategoriesGUI.MINER_SLOT) category = BuyerCategory.MINER;
        else if (slot == AutoBuyerCategoriesGUI.MOB_LOOT_SLOT) category = BuyerCategory.MOB_LOOT;
        else if (slot == AutoBuyerCategoriesGUI.WOOD_SLOT) category = BuyerCategory.WOOD;
        else if (slot == AutoBuyerCategoriesGUI.POTIONS_SLOT) category = BuyerCategory.POTIONS;
        else if (slot == AutoBuyerCategoriesGUI.CROPS_SLOT) category = BuyerCategory.CROPS;

        if (category != null) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new CategoryItemsGUI(plugin, category).open(player);
        }

        // Все остальные клики игнорируем - ничего не делаем
    }

    private void handleCategoryItemsClick(Player player, InventoryClickEvent event) {
        // КРИТИЧЕСКИ ВАЖНО: отменяем ВСЕ клики в этом GUI
        event.setCancelled(true);

        int slot = event.getRawSlot();
        String title = event.getView().getTitle();

        // Блокируем ВСЕ клики в инвентаре игрока тоже
        if (slot >= 54) {
            return;
        }

        // Определяем категорию по заголовку
        BuyerCategory category = null;
        if (title.contains("Ресурсы из шахты")) category = BuyerCategory.MINER;
        else if (title.contains("Ресурсы с мобов")) category = BuyerCategory.MOB_LOOT;
        else if (title.contains("Древесина")) category = BuyerCategory.WOOD;
        else if (title.contains("Зелья")) category = BuyerCategory.POTIONS;
        else if (title.contains("Культуры")) category = BuyerCategory.CROPS;

        if (category == null) return;

        // Кнопка назад
        if (slot == CategoryItemsGUI.BACK_SLOT) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new AutoBuyerCategoriesGUI(plugin).open(player);
            return;
        }

        // Кнопка "Включить все"
        if (slot == CategoryItemsGUI.ENABLE_ALL_SLOT) {
            CategoryItemsGUI.enableAll(player, category, plugin.getConfigManager().getAcceptedItems(category));

            // Обновляем данные игрока - включаем категорию
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            data.setAutoBuyerEnabledForCategory(category, true);
            plugin.getPlayerDataManager().savePlayerData(data);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            new CategoryItemsGUI(plugin, category).open(player);
            return;
        }

        // Кнопка "Выключить все"
        if (slot == CategoryItemsGUI.DISABLE_ALL_SLOT) {
            CategoryItemsGUI.disableAll(player, category);

            // Обновляем данные игрока - выключаем категорию
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            data.setAutoBuyerEnabledForCategory(category, false);
            plugin.getPlayerDataManager().savePlayerData(data);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
            new CategoryItemsGUI(plugin, category).open(player);
            return;
        }

        // Получаем кликнутый предмет
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Игнорируем клики по стеклянным панелям (границы) и другим декоративным элементам
        Material clickedType = clickedItem.getType();
        if (clickedType.name().contains("GLASS_PANE") ||
                clickedType == Material.BARRIER ||
                clickedType == Material.LIME_DYE ||
                clickedType == Material.RED_DYE) {
            return;
        }

        // Переключение предмета - только если это действительно продаваемый предмет из категории
        if (plugin.getConfigManager().getAcceptedItems(category).contains(clickedType)) {
            CategoryItemsGUI.toggleItem(player, category, clickedType);

            // Обновляем данные - включаем категорию если хотя бы 1 предмет включен
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            boolean anyEnabled = CategoryItemsGUI.isItemEnabled(player, category, clickedType);
            if (anyEnabled) {
                data.setAutoBuyerEnabledForCategory(category, true);
            } else {
                // Проверяем есть ли еще включенные предметы
                boolean hasAnyEnabled = false;
                for (Material mat : plugin.getConfigManager().getAcceptedItems(category)) {
                    if (CategoryItemsGUI.isItemEnabled(player, category, mat)) {
                        hasAnyEnabled = true;
                        break;
                    }
                }
                if (!hasAnyEnabled) {
                    data.setAutoBuyerEnabledForCategory(category, false);
                }
            }
            plugin.getPlayerDataManager().savePlayerData(data);

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new CategoryItemsGUI(plugin, category).open(player);
        }

        // Все остальные клики игнорируем - ничего не делаем
    }

    private void handleShopMenuClick(Player player, InventoryClickEvent event) {
        // КРИТИЧЕСКИ ВАЖНО: отменяем событие СРАЗУ ЖЕ
        event.setCancelled(true);

        int slot = event.getRawSlot();

        // Если клик в инвентаре игрока - просто отменяем и выходим
        if (slot >= 54) {
            return;
        }

        // Кнопка назад
        if (slot == ShopGUI.BACK_SLOT) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            new MainMenuGUI(plugin).open(player);
            return;
        }

        // Игнорируем декоративные слоты
        if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8 || slot == ShopGUI.INFO_SLOT) {
            return;
        }

        // Попытка покупки
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchant(Enchantment.UNBREAKING)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(Component.text("§c§l✗ §7Недостаточно очков для покупки!"));
            return;
        }

        long price = ShopGUI.getItemPrice(event.getInventory(), slot);
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.removeBuyerPoints(price)) {
            // Создаем копию предмета без лора и энчантов
            ItemStack purchasedItem = new ItemStack(item.getType(), item.getAmount());

            // Выдаем предмет игроку
            player.getInventory().addItem(purchasedItem);

            // Сохраняем данные
            plugin.getPlayerDataManager().savePlayerData(data);

            // Уведомление
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            player.sendMessage(Component.text("§a§l✓ §7Покупка успешна! Потрачено: §d" +
                    String.format("%,d", price) + " §7ОС"));

            // Обновляем меню
            new ShopGUI(plugin).open(player);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(Component.text("§c§l✗ §7Ошибка покупки!"));
        }
    }

    private void handleSellItems(Player player, Inventory inventory) {
        // Проверка кулдауна
        Long lastSell = sellCooldown.get(player);
        if (lastSell != null && System.currentTimeMillis() - lastSell < 1000) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        double totalMoney = 0;
        long totalPoints = 0;
        long totalExp = 0;
        int totalItems = 0;

        // Собираем предметы из слотов продажи
        for (int sellSlot : MainMenuGUI.SELL_SLOTS) {
            ItemStack item = inventory.getItem(sellSlot);
            if (item == null || item.getType() == Material.AIR) continue;

            // Определяем категорию предмета
            BuyerCategory category = findCategory(item.getType());
            if (category == null) {
                // Возвращаем предмет игроку
                player.getInventory().addItem(item);
                inventory.setItem(sellSlot, null);
                continue;
            }

            int amount = item.getAmount();
            double basePrice = plugin.getConfigManager().getBasePrice(category, item.getType());
            double multiplier = data.getPriceMultiplier(category);
            double price = basePrice * multiplier * amount;

            totalMoney += price;
            totalItems += amount;

            // Начисляем опыт
            long exp = (long) (amount * (basePrice * 2.0));
            data.addExperience(category, exp);
            totalExp += exp;

            // Начисляем очки (0.001% от суммы продажи, но минимум 1)
            long points = Math.max(1, (long) (price * 0.00001));
            totalPoints += points;

            // Очищаем слот
            inventory.setItem(sellSlot, null);
        }

        if (totalItems == 0) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(Component.text("§c§l✗ §7Нет предметов для продажи!"));
            return;
        }

        // Выдаем деньги
        if (plugin.getVaultEconomy().isEnabled()) {
            plugin.getVaultEconomy().deposit(player, totalMoney);
        }

        // Добавляем очки
        data.addBuyerPoints(totalPoints);

        // Сохраняем данные
        plugin.getPlayerDataManager().savePlayerData(data);

        // Уведомление
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  §a§l✓ ПРОДАЖА УСПЕШНА!"));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  §7Продано предметов§8: §e" + totalItems + " §7шт."));
        player.sendMessage(Component.text("  §7Получено денег§8: §6" + String.format("%.1f", totalMoney) + " §7монет"));
        player.sendMessage(Component.text("  §7Получено очков§8: §d" + String.format("%,d", totalPoints) + " §7ОС"));
        player.sendMessage(Component.text("  §7Получено опыта§8: §e" + String.format("%,d", totalExp) + " §7ОП"));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));

        sellCooldown.put(player, System.currentTimeMillis());

        // Обновляем меню
        new MainMenuGUI(plugin).open(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        String title = event.getView().getTitle();

        // Возвращаем предметы из слотов продажи при закрытии главного меню
        // Важно: проверяем только главное меню скупщика, не меню автоскупщика
        if (title.contains("СКУПЩИК") && !title.contains("АВТО")) {
            Inventory inventory = event.getInventory();
            for (int sellSlot : MainMenuGUI.SELL_SLOTS) {
                ItemStack item = inventory.getItem(sellSlot);
                if (item != null && item.getType() != Material.AIR) {
                    player.getInventory().addItem(item);
                }
            }
        }
    }

    private BuyerCategory findCategory(Material material) {
        for (BuyerCategory category : BuyerCategory.values()) {
            if (plugin.getConfigManager().getAcceptedItems(category).contains(material)) {
                return category;
            }
        }
        return null;
    }
}