package com.example.tushpBuyer.config;

import com.example.tushpBuyer.TushpBuyer;
import com.example.tushpBuyer.data.BuyerCategory;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private final TushpBuyer plugin;
    private final Map<BuyerCategory, Map<Material, Double>> prices;
    private final Map<Material, Map<String, Object>> potionPrices; // Для зелий с эффектами
    private final File shopFile;
    private FileConfiguration shopConfig;

    public ConfigManager(TushpBuyer plugin) {
        this.plugin = plugin;
        this.prices = new HashMap<>();
        this.potionPrices = new HashMap<>();
        this.shopFile = new File(plugin.getDataFolder(), "shop.yml");
        loadPrices();
        loadShopConfig();
    }

    private void loadShopConfig() {
        if (!shopFile.exists()) {
            try {
                shopFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать shop.yml");
                e.printStackTrace();
            }
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopFile);
    }

    public void saveShopConfig() {
        try {
            shopConfig.save(shopFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить shop.yml");
            e.printStackTrace();
        }
    }

    public FileConfiguration getShopConfig() {
        return shopConfig;
    }

    private void loadPrices() {
        // Шахтёр (MINER)
        Map<Material, Double> minerPrices = new HashMap<>();
        minerPrices.put(Material.COAL, getPrice("miner.coal", 1.0));
        minerPrices.put(Material.LAPIS_LAZULI, getPrice("miner.lapis_lazuli", 1.5));
        minerPrices.put(Material.REDSTONE, getPrice("miner.redstone", 1.2));
        minerPrices.put(Material.END_STONE, getPrice("miner.end_stone", 2.0));
        minerPrices.put(Material.QUARTZ, getPrice("miner.quartz", 2.0));
        minerPrices.put(Material.OBSIDIAN, getPrice("miner.obsidian", 3.0));
        minerPrices.put(Material.IRON_INGOT, getPrice("miner.iron_ingot", 3.0));
        minerPrices.put(Material.GOLD_INGOT, getPrice("miner.gold_ingot", 5.0));
        minerPrices.put(Material.DIAMOND, getPrice("miner.diamond", 10.0));
        minerPrices.put(Material.ANCIENT_DEBRIS, getPrice("miner.ancient_debris", 50.0));
        minerPrices.put(Material.NETHERITE_SCRAP, getPrice("miner.netherite_scrap", 60.0));
        minerPrices.put(Material.EMERALD, getPrice("miner.emerald", 12.0));
        minerPrices.put(Material.NETHERITE_INGOT, getPrice("miner.netherite_ingot", 250.0));
        prices.put(BuyerCategory.MINER, minerPrices);

        // Лут с мобов (MOB_LOOT)
        Map<Material, Double> mobLootPrices = new HashMap<>();
        mobLootPrices.put(Material.ROTTEN_FLESH, getPrice("mob_loot.rotten_flesh", 0.4));
        mobLootPrices.put(Material.ARROW, getPrice("mob_loot.arrow", 0.2));
        mobLootPrices.put(Material.BONE, getPrice("mob_loot.bone", 0.5));
        mobLootPrices.put(Material.STRING, getPrice("mob_loot.string", 0.6));
        mobLootPrices.put(Material.SPIDER_EYE, getPrice("mob_loot.spider_eye", 0.8));
        mobLootPrices.put(Material.BLAZE_ROD, getPrice("mob_loot.blaze_rod", 3.0));
        mobLootPrices.put(Material.SLIME_BALL, getPrice("mob_loot.slime_ball", 1.5));
        mobLootPrices.put(Material.MAGMA_CREAM, getPrice("mob_loot.magma_cream", 2.0));
        mobLootPrices.put(Material.WHITE_WOOL, getPrice("mob_loot.wool", 0.5));
        mobLootPrices.put(Material.COOKED_PORKCHOP, getPrice("mob_loot.cooked_porkchop", 1.2));
        mobLootPrices.put(Material.COOKED_MUTTON, getPrice("mob_loot.cooked_mutton", 1.2));
        mobLootPrices.put(Material.LEATHER, getPrice("mob_loot.leather", 1.0));
        mobLootPrices.put(Material.ENDER_PEARL, getPrice("mob_loot.ender_pearl", 3.5));
        mobLootPrices.put(Material.GUNPOWDER, getPrice("mob_loot.gunpowder", 1.8));
        mobLootPrices.put(Material.GLOWSTONE_DUST, getPrice("mob_loot.glowstone_dust", 1.4));
        mobLootPrices.put(Material.WITHER_SKELETON_SKULL, getPrice("mob_loot.wither_skeleton_skull", 100.0));
        mobLootPrices.put(Material.SHULKER_SHELL, getPrice("mob_loot.shulker_shell", 25.0));
        mobLootPrices.put(Material.DRAGON_BREATH, getPrice("mob_loot.dragon_breath", 15.0));
        mobLootPrices.put(Material.GHAST_TEAR, getPrice("mob_loot.ghast_tear", 5.0));
        prices.put(BuyerCategory.MOB_LOOT, mobLootPrices);

        // Древесина (WOOD)
        Map<Material, Double> woodPrices = new HashMap<>();
        woodPrices.put(Material.OAK_LOG, getPrice("wood.oak_log", 0.5));
        woodPrices.put(Material.SPRUCE_LOG, getPrice("wood.spruce_log", 0.5));
        woodPrices.put(Material.DARK_OAK_LOG, getPrice("wood.dark_oak_log", 0.6));
        woodPrices.put(Material.BIRCH_LOG, getPrice("wood.birch_log", 0.5));
        woodPrices.put(Material.JUNGLE_LOG, getPrice("wood.jungle_log", 0.6));
        woodPrices.put(Material.ACACIA_LOG, getPrice("wood.acacia_log", 0.5));
        woodPrices.put(Material.CRIMSON_STEM, getPrice("wood.crimson_stem", 1.0));
        woodPrices.put(Material.WARPED_STEM, getPrice("wood.warped_stem", 1.0));
        prices.put(BuyerCategory.WOOD, woodPrices);

        // Культуры (CROPS)
        Map<Material, Double> cropsPrices = new HashMap<>();
        cropsPrices.put(Material.SWEET_BERRIES, getPrice("crops.sweet_berries", 0.5));
        cropsPrices.put(Material.BAMBOO, getPrice("crops.bamboo", 0.3));
        cropsPrices.put(Material.WHEAT, getPrice("crops.wheat", 0.6));
        cropsPrices.put(Material.POTATO, getPrice("crops.potato", 0.5));
        cropsPrices.put(Material.BEETROOT, getPrice("crops.beetroot", 0.5));
        cropsPrices.put(Material.NETHER_WART, getPrice("crops.nether_wart", 2.0));
        cropsPrices.put(Material.CARROT, getPrice("crops.carrot", 0.5));
        cropsPrices.put(Material.GOLDEN_CARROT, getPrice("crops.golden_carrot", 5.0));
        cropsPrices.put(Material.COCOA_BEANS, getPrice("crops.cocoa_beans", 0.8));
        cropsPrices.put(Material.CHORUS_FRUIT, getPrice("crops.chorus_fruit", 1.5));
        cropsPrices.put(Material.CHORUS_FLOWER, getPrice("crops.chorus_flower", 2.0));
        cropsPrices.put(Material.KELP, getPrice("crops.kelp", 0.4));
        cropsPrices.put(Material.CACTUS, getPrice("crops.cactus", 0.8));
        cropsPrices.put(Material.PUMPKIN, getPrice("crops.pumpkin", 1.0));
        cropsPrices.put(Material.MELON, getPrice("crops.melon", 1.2));
        cropsPrices.put(Material.MELON_SLICE, getPrice("crops.melon_slice", 0.4));
        prices.put(BuyerCategory.CROPS, cropsPrices);

        // Зелья (POTIONS) - специальная обработка
        loadPotionPrices();

        savePrices();
    }

    private void loadPotionPrices() {
        // Зелья имеют особую структуру: тип + уровень
        Map<String, Double> potionBasePrices = new HashMap<>();
        potionBasePrices.put("STRENGTH_2", getPrice("potions.strength_2", 15.0));
        potionBasePrices.put("SPEED_2", getPrice("potions.speed_2", 12.0));
        potionBasePrices.put("INVISIBILITY_2", getPrice("potions.invisibility_2", 20.0));
        potionBasePrices.put("FIRE_RESISTANCE", getPrice("potions.fire_resistance", 10.0));
        potionBasePrices.put("INSTANT_HEAL_2", getPrice("potions.instant_heal_2", 18.0));
        
        for (Map.Entry<String, Double> entry : potionBasePrices.entrySet()) {
            Map<String, Object> potionData = new HashMap<>();
            potionData.put("price", entry.getValue());
            potionPrices.put(Material.POTION, potionData);
        }
    }

    private double getPrice(String path, double defaultValue) {
        return plugin.getConfig().getDouble("prices." + path, defaultValue);
    }

    private void savePrices() {
        for (Map.Entry<BuyerCategory, Map<Material, Double>> entry : prices.entrySet()) {
            String categoryPath = entry.getKey().name().toLowerCase();
            for (Map.Entry<Material, Double> priceEntry : entry.getValue().entrySet()) {
                String itemPath = priceEntry.getKey().name().toLowerCase();
                plugin.getConfig().set("prices." + categoryPath + "." + itemPath, priceEntry.getValue());
            }
        }
        plugin.saveConfig();
    }

    public Map<Material, Double> getPrices(BuyerCategory category) {
        return prices.getOrDefault(category, new HashMap<>());
    }

    public double getBasePrice(BuyerCategory category, Material material) {
        if (category == BuyerCategory.POTIONS && material == Material.POTION) {
            // Зелья требуют специальной обработки через ItemStack
            return 10.0; // Базовая цена
        }
        return prices.getOrDefault(category, new HashMap<>()).getOrDefault(material, 0.0);
    }

    public double getPotionPrice(ItemStack potion) {
        if (potion.getType() != Material.POTION) return 0.0;

        if (potion.getItemMeta() instanceof PotionMeta potionMeta) {
            PotionType type = potionMeta.getBasePotionType();
            return getPrice("potions." + type.name().toLowerCase(), 5.0);
        }
        return 5.0;
    }


    public Set<Material> getAcceptedItems(BuyerCategory category) {
        if (category == BuyerCategory.POTIONS) {
            return Set.of(Material.POTION);
        }
        return prices.getOrDefault(category, new HashMap<>()).keySet();
    }

    public boolean isPotionAccepted(ItemStack potion) {
        if (potion.getType() != Material.POTION) return false;

        if (potion.getItemMeta() instanceof PotionMeta potionMeta) {
            PotionType type = potionMeta.getBasePotionType();

            return switch (type) {
                case STRENGTH,
                     STRONG_STRENGTH,
                     LONG_STRENGTH,
                     SWIFTNESS,
                     STRONG_SWIFTNESS,
                     LONG_SWIFTNESS,
                     INVISIBILITY,
                     LONG_INVISIBILITY,
                     FIRE_RESISTANCE,
                     LONG_FIRE_RESISTANCE,
                     HEALING,
                     STRONG_HEALING-> true;
                default -> false;
            };
        }
        return false;
    }

}
