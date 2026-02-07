package com.example.tushpBuyer.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final Map<BuyerCategory, Integer> levels;
    private final Map<BuyerCategory, Long> experience;
    private boolean autoBuyerEnabled;
    private final Map<BuyerCategory, Boolean> autoBuyerCategories;
    private SellMode sellMode;
    private long buyerPoints; // Очки скупщика
    private static final int MAX_LEVEL = 10;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.levels = new HashMap<>();
        this.experience = new HashMap<>();
        this.autoBuyerCategories = new HashMap<>();
        this.autoBuyerEnabled = false;
        this.sellMode = SellMode.ONE;
        this.buyerPoints = 0L;
        
        // Инициализация уровней
        for (BuyerCategory category : BuyerCategory.values()) {
            levels.put(category, 1);
            experience.put(category, 0L);
            autoBuyerCategories.put(category, false);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getLevel(BuyerCategory category) {
        return levels.getOrDefault(category, 1);
    }

    public void setLevel(BuyerCategory category, int level) {
        levels.put(category, Math.min(level, MAX_LEVEL));
    }

    public long getExperience(BuyerCategory category) {
        return experience.getOrDefault(category, 0L);
    }

    public void addExperience(BuyerCategory category, long exp) {
        long current = experience.getOrDefault(category, 0L);
        experience.put(category, current + exp);
        
        // Проверка на повышение уровня
        checkLevelUp(category);
    }

    private void checkLevelUp(BuyerCategory category) {
        int currentLevel = getLevel(category);
        if (currentLevel >= MAX_LEVEL) return;
        
        long currentExp = getExperience(category);
        long requiredExp = getRequiredExperience(currentLevel);
        
        if (currentExp >= requiredExp) {
            setLevel(category, currentLevel + 1);
            experience.put(category, currentExp - requiredExp);
            // Рекурсивная проверка на случай если опыта хватает на несколько уровней
            checkLevelUp(category);
        }
    }

    public long getRequiredExperience(int level) {
        return switch (level) {
            case 1 -> 5000L;      // 1 -> 2
            case 2 -> 15000L;     // 2 -> 3
            case 3 -> 35000L;     // 3 -> 4
            case 4 -> 75000L;     // 4 -> 5
            case 5 -> 150000L;    // 5 -> 6
            case 6 -> 300000L;    // 6 -> 7
            case 7 -> 600000L;    // 7 -> 8
            case 8 -> 1200000L;   // 8 -> 9
            case 9 -> 2500000L;   // 9 -> 10
            default -> Long.MAX_VALUE;
        };
    }

    public double getPriceMultiplier(BuyerCategory category) {
        int level = getLevel(category);
        return switch (level) {
            case 1 -> 1.00;
            case 2 -> 1.15;
            case 3 -> 1.35;
            case 4 -> 1.60;
            case 5 -> 1.90;
            case 6 -> 2.25;
            case 7 -> 2.50;
            case 8 -> 2.70;
            case 9 -> 2.85;
            case 10 -> 3.00;
            default -> 1.00;
        };
    }

    public double getNextLevelMultiplier(int level) {
        return switch (level + 1) {
            case 2 -> 1.15;
            case 3 -> 1.35;
            case 4 -> 1.60;
            case 5 -> 1.90;
            case 6 -> 2.25;
            case 7 -> 2.50;
            case 8 -> 2.70;
            case 9 -> 2.85;
            case 10 -> 3.00;
            default -> 1.00;
        };
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public boolean isAutoBuyerEnabled() {
        return autoBuyerEnabled;
    }

    public void setAutoBuyerEnabled(boolean enabled) {
        this.autoBuyerEnabled = enabled;
    }

    public boolean isAutoBuyerEnabledForCategory(BuyerCategory category) {
        return autoBuyerCategories.getOrDefault(category, false);
    }

    public void setAutoBuyerEnabledForCategory(BuyerCategory category, boolean enabled) {
        autoBuyerCategories.put(category, enabled);
    }

    public void enableAllCategoriesInCurrent(BuyerCategory currentCategory) {
        autoBuyerCategories.put(currentCategory, true);
    }

    public void disableAllCategories() {
        for (BuyerCategory category : BuyerCategory.values()) {
            autoBuyerCategories.put(category, false);
        }
    }

    public int getActiveAutoBuyerCategories() {
        return (int) autoBuyerCategories.values().stream().filter(enabled -> enabled).count();
    }

    public SellMode getSellMode() {
        return sellMode;
    }

    public void setSellMode(SellMode mode) {
        this.sellMode = mode;
    }

    // Методы для работы с очками скупщика
    public long getBuyerPoints() {
        return buyerPoints;
    }

    public void setBuyerPoints(long points) {
        this.buyerPoints = Math.max(0, points);
    }

    public void addBuyerPoints(long points) {
        this.buyerPoints += points;
    }

    public boolean removeBuyerPoints(long points) {
        if (this.buyerPoints >= points) {
            this.buyerPoints -= points;
            return true;
        }
        return false;
    }

    public enum SellMode {
        ONE(1, "По 1 шт."),
        SIXTEEN(16, "По 16 шт."),
        SIXTY_FOUR(64, "По 64 шт."),
        ALL(-1, "Сдать всё");

        private final int amount;
        private final String displayName;

        SellMode(int amount, String displayName) {
            this.amount = amount;
            this.displayName = displayName;
        }

        public int getAmount() {
            return amount;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SellMode next() {
            int nextIndex = (this.ordinal() + 1) % values().length;
            return values()[nextIndex];
        }
    }
}
