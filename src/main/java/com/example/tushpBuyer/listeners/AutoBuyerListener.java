package com.example.tushpBuyer.listeners;

import com.example.tushpBuyer.TushpBuyer;
import com.example.tushpBuyer.data.BuyerCategory;
import com.example.tushpBuyer.data.PlayerData;
import com.example.tushpBuyer.gui.CategoryItemsGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AutoBuyerListener implements Listener {
    private final TushpBuyer plugin;
    private final Map<Player, Long> lastNotification;

    public AutoBuyerListener(TushpBuyer plugin) {
        this.plugin = plugin;
        this.lastNotification = new HashMap<>();
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Проверка включен ли автоскупщик
        if (!data.isAutoBuyerEnabled()) return;

        ItemStack item = event.getItem().getItemStack();
        Material material = item.getType();

        // Поиск категории для данного предмета
        BuyerCategory category = findCategory(material);
        if (category == null) return;

        // Проверка включена ли категория в автоскупщике
        if (!data.isAutoBuyerEnabledForCategory(category)) return;

        // ВАЖНО: Проверяем включен ли конкретный предмет через CategoryItemsGUI
        if (!CategoryItemsGUI.isItemEnabled(player, category, material)) return;

        // Предотвращение поднятия предмета
        event.setCancelled(true);
        event.getItem().remove();

        int amount = item.getAmount();

        // Расчет цены
        double basePrice = plugin.getConfigManager().getBasePrice(category, material);
        double multiplier = data.getPriceMultiplier(category);
        double pricePerItem = basePrice * multiplier;
        double totalPrice = pricePerItem * amount;

        // Выдача денег
        if (plugin.getVaultEconomy().isEnabled()) {
            plugin.getVaultEconomy().deposit(player, totalPrice);
        }

        // Добавление опыта
        long expGained = (long) (amount * (basePrice * 2.0));
        int oldLevel = data.getLevel(category);
        data.addExperience(category, expGained);
        int newLevel = data.getLevel(category);

        // Начисление очков скупщика (0.001% от суммы)
        long points = Math.max(1, (long) (totalPrice * 0.00001));
        data.addBuyerPoints(points);

        // Сохранение данных
        plugin.getPlayerDataManager().savePlayerData(data);

        // Уведомление (с ограничением частоты)
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastNotification.get(player);

        if (lastTime == null || currentTime - lastTime > 2000) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);
            player.sendActionBar(Component.text("§8[§a§l⚡§8] ", NamedTextColor.GRAY)
                    .append(Component.text("§f" + amount + "x ", NamedTextColor.WHITE))
                    .append(Component.text(getItemName(material), NamedTextColor.YELLOW))
                    .append(Component.text(" §8→ §6" + String.format("%.1f", totalPrice) + " §7монет", NamedTextColor.GRAY)));

            lastNotification.put(player, currentTime);
        }

        // Уведомление о повышении уровня
        if (newLevel > oldLevel) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.0f);

            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("  §6§l✦ ПОВЫШЕНИЕ УРОВНЯ! ✦"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("  §7Категория§8: §e" + category.getDisplayName()));
            player.sendMessage(Component.text("  §7Уровень§8: §e" + oldLevel + " §8→ §6§l" + newLevel));
            player.sendMessage(Component.text("  §7Множитель§8: §6×" + String.format("%.2f", data.getPriceMultiplier(category))));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
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

    private String getItemName(Material material) {
        return switch (material) {
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
            case OAK_LOG -> "Дубовое бревно";
            case SPRUCE_LOG -> "Еловое бревно";
            case BIRCH_LOG -> "Берёзовое бревно";
            case JUNGLE_LOG -> "Тропическое бревно";
            case ACACIA_LOG -> "Акациевое бревно";
            case DARK_OAK_LOG -> "Бревно тёмного дуба";
            case CRIMSON_STEM -> "Багровый стебель";
            case WARPED_STEM -> "Искажённый стебель";
            default -> material.name();
        };
    }
}