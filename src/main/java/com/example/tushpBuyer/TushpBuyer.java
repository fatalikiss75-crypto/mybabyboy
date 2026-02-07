package com.example.tushpBuyer;

import com.example.tushpBuyer.commands.AddShopItemCommand;
import com.example.tushpBuyer.commands.BuyerCommand;
import com.example.tushpBuyer.config.ConfigManager;
import com.example.tushpBuyer.data.PlayerDataManager;
import com.example.tushpBuyer.economy.VaultEconomy;
import com.example.tushpBuyer.listeners.AutoBuyerListener;
import com.example.tushpBuyer.listeners.InventoryListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TushpBuyer extends JavaPlugin {

    private static TushpBuyer instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private VaultEconomy vaultEconomy;

    @Override
    public void onEnable() {
        instance = this;
        
        // Создание конфигурационных файлов
        saveDefaultConfig();
        
        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.vaultEconomy = new VaultEconomy(this);
        this.playerDataManager = new PlayerDataManager(this);
        
        // Регистрация команд
        getCommand("buyer").setExecutor(new BuyerCommand(this));
        getCommand("tushpbuyeradditem").setExecutor(new AddShopItemCommand(this));
        
        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new AutoBuyerListener(this), this);
        
        getLogger().info("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("§a");
        getLogger().info("§6  TushpBuyer v2.0 успешно запущен!");
        getLogger().info("§a");
        getLogger().info("§7  Новые возможности:");
        getLogger().info("§7  • Расширенные категории (5 категорий)");
        getLogger().info("§7  • Система очков скупщика");
        getLogger().info("§7  • Магазин за очки");
        getLogger().info("§7  • Улучшенный интерфейс");
        getLogger().info("§a");
        getLogger().info("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    @Override
    public void onDisable() {
        // Сохранение данных игроков
        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }
        
        getLogger().info("§c▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("§c");
        getLogger().info("§7  TushpBuyer отключен!");
        getLogger().info("§c");
        getLogger().info("§c▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    public static TushpBuyer getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public VaultEconomy getVaultEconomy() {
        return vaultEconomy;
    }
}
