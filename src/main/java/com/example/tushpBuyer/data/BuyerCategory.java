package com.example.tushpBuyer.data;

import org.bukkit.Material;

public enum BuyerCategory {
    MINER("Ресурсы из шахты", Material.NETHERITE_INGOT, new String[]{
        "§7Продавайте руды и минералы",
        "§7и зарабатывайте монеты!",
        "",
        "§eПрокачивайте уровень для",
        "§eувеличения цены продажи!"
    }),
    MOB_LOOT("Ресурсы с мобов", Material.BONE, new String[]{
        "§7Продавайте дроп с мобов",
        "§7и зарабатывайте монеты!",
        "",
        "§eПрокачивайте уровень для",
        "§eувеличения цены продажи!"
    }),
    WOOD("Древесина", Material.OAK_LOG, new String[]{
        "§7Продавайте древесину",
        "§7и зарабатывайте монеты!",
        "",
        "§eПрокачивайте уровень для",
        "§eувеличения цены продажи!"
    }),
    POTIONS("Зелья", Material.POTION, new String[]{
        "§7Продавайте зелья",
        "§7и зарабатывайте монеты!",
        "",
        "§eПрокачивайте уровень для",
        "§eувеличения цены продажи!"
    }),
    CROPS("Культуры", Material.WHEAT, new String[]{
        "§7Продавайте культуры",
        "§7и зарабатывайте монеты!",
        "",
        "§eПрокачивайте уровень для",
        "§eувеличения цены продажи!"
    });

    private final String displayName;
    private final Material icon;
    private final String[] description;

    BuyerCategory(String displayName, Material icon, String[] description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public String[] getDescription() {
        return description;
    }
}
