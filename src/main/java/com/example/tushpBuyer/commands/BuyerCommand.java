package com.example.tushpBuyer.commands;

import com.example.tushpBuyer.TushpBuyer;
import com.example.tushpBuyer.gui.MainMenuGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuyerCommand implements CommandExecutor {
    private final TushpBuyer plugin;
    private final MainMenuGUI mainMenuGUI;

    public BuyerCommand(TushpBuyer plugin) {
        this.plugin = plugin;
        this.mainMenuGUI = new MainMenuGUI(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!", NamedTextColor.RED));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && player.hasPermission("buyer.reload")) {
            plugin.reloadConfig();
            player.sendMessage(Component.text("✓ ", NamedTextColor.GREEN)
                    .append(Component.text("Конфигурация перезагружена!", NamedTextColor.YELLOW)));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            return true;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        mainMenuGUI.open(player);
        return true;
    }
}
