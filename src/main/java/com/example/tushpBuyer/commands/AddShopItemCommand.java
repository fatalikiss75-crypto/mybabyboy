package com.example.tushpBuyer.commands;

import com.example.tushpBuyer.TushpBuyer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddShopItemCommand implements CommandExecutor {
    private final TushpBuyer plugin;

    public AddShopItemCommand(TushpBuyer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("buyer.admin")) {
            player.sendMessage(Component.text("§c§l✗ §7У вас нет прав для использования этой команды!"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("  §6§lДобавление предмета в магазин"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("  §7Использование§8:"));
            player.sendMessage(Component.text("  §e/tushpbuyeradditem <цена> <название>"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("  §7Пример§8:"));
            player.sendMessage(Component.text("  §e/tushpbuyeradditem 1000000 §6Редкий алмаз"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("  §8⚊ §7Возьмите предмет в руку"));
            player.sendMessage(Component.text("  §8⚊ §7Укажите цену в очках скупщика"));
            player.sendMessage(Component.text("  §8⚊ §7Укажите отображаемое название"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            return true;
        }

        // Проверяем предмет в руке
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("§c§l✗ §7Возьмите предмет в руку!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        // Парсим цену
        long price;
        try {
            price = Long.parseLong(args[0]);
            if (price < 1) {
                player.sendMessage(Component.text("§c§l✗ §7Цена должна быть больше 0!"));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c§l✗ §7Неверный формат цены!"));
            return true;
        }

        // Собираем название
        StringBuilder displayName = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) displayName.append(" ");
            displayName.append(args[i]);
        }

        // Добавляем предмет в конфигурацию
        FileConfiguration shopConfig = plugin.getConfigManager().getShopConfig();
        
        // Получаем следующий ID
        int nextId = shopConfig.getConfigurationSection("shop_items") == null ? 
            1 : shopConfig.getConfigurationSection("shop_items").getKeys(false).size() + 1;
        
        String path = "shop_items.item_" + nextId;
        shopConfig.set(path + ".material", item.getType().name());
        shopConfig.set(path + ".amount", item.getAmount());
        shopConfig.set(path + ".price", price);
        shopConfig.set(path + ".display_name", displayName.toString());
        
        List<String> description = new ArrayList<>();
        description.add("Уникальный предмет из магазина");
        description.add("Доступен за очки скупщика");
        shopConfig.set(path + ".description", description);
        
        plugin.getConfigManager().saveShopConfig();

        // Уведомление
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  §a§l✓ ПРЕДМЕТ ДОБАВЛЕН В МАГАЗИН!"));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  §7Предмет§8: §e" + item.getType().name()));
        player.sendMessage(Component.text("  §7Количество§8: §e" + item.getAmount() + " §7шт."));
        player.sendMessage(Component.text("  §7Цена§8: §d" + String.format("%,d", price) + " §7ОС"));
        player.sendMessage(Component.text("  §7Название§8: §6" + displayName));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));

        return true;
    }
}
