package com.example.tushpBuyer.data;

import com.example.tushpBuyer.TushpBuyer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final TushpBuyer plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final File dataFolder;

    public PlayerDataManager(TushpBuyer plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> {
            PlayerData data = new PlayerData(uuid);
            loadPlayerData(data);
            return data;
        });
    }

    private void loadPlayerData(PlayerData data) {
        File file = new File(dataFolder, data.getUuid().toString() + ".yml");
        if (!file.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Загрузка уровней и опыта
        for (BuyerCategory category : BuyerCategory.values()) {
            String path = category.name().toLowerCase();
            data.setLevel(category, config.getInt(path + ".level", 1));
            long exp = config.getLong(path + ".experience", 0L);
            long currentExp = data.getExperience(category);
            data.addExperience(category, exp - currentExp);
        }
        
        // Загрузка настроек автоскупщика
        data.setAutoBuyerEnabled(config.getBoolean("auto_buyer.enabled", false));
        
        for (BuyerCategory category : BuyerCategory.values()) {
            String path = "auto_buyer.categories." + category.name().toLowerCase();
            data.setAutoBuyerEnabledForCategory(category, config.getBoolean(path, false));
        }
        
        // Загрузка режима продажи
        String sellMode = config.getString("sell_mode", "ONE");
        try {
            data.setSellMode(PlayerData.SellMode.valueOf(sellMode));
        } catch (IllegalArgumentException e) {
            data.setSellMode(PlayerData.SellMode.ONE);
        }
        
        // Загрузка очков скупщика
        data.setBuyerPoints(config.getLong("buyer_points", 0L));
    }

    public void savePlayerData(PlayerData data) {
        File file = new File(dataFolder, data.getUuid().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        // Сохранение уровней и опыта
        for (BuyerCategory category : BuyerCategory.values()) {
            String path = category.name().toLowerCase();
            config.set(path + ".level", data.getLevel(category));
            config.set(path + ".experience", data.getExperience(category));
        }
        
        // Сохранение настроек автоскупщика
        config.set("auto_buyer.enabled", data.isAutoBuyerEnabled());
        
        for (BuyerCategory category : BuyerCategory.values()) {
            String path = "auto_buyer.categories." + category.name().toLowerCase();
            config.set(path, data.isAutoBuyerEnabledForCategory(category));
        }
        
        // Сохранение режима продажи
        config.set("sell_mode", data.getSellMode().name());
        
        // Сохранение очков скупщика
        config.set("buyer_points", data.getBuyerPoints());
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить данные игрока " + data.getUuid());
            e.printStackTrace();
        }
    }

    public void saveAll() {
        for (PlayerData data : playerDataMap.values()) {
            savePlayerData(data);
        }
    }

    public void unloadPlayerData(UUID uuid) {
        PlayerData data = playerDataMap.remove(uuid);
        if (data != null) {
            savePlayerData(data);
        }
    }
}
