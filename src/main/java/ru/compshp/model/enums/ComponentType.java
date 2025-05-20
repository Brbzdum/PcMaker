package ru.compshp.model.enums;

public enum ComponentType {
    // Core Components
    CPU,
    GPU,
    MB,
    RAM,
    PSU,
    CASE,
    COOLER,

    // Storage
    STORAGE,
    SSD("Solid State Drive"),
    HDD("Hard Disk Drive"),
    M2("M.2 SSD"),

    // Cooling
    CASE_FAN("Case Fan"),
    LIQUID_COOLER("Liquid Cooler"),

    // Peripherals
    MONITOR("Monitor"),
    KEYBOARD("Keyboard"),
    MOUSE("Mouse"),
    HEADSET("Headset"),

    // Networking
    NETWORK_CARD("Network Card"),
    WIFI_CARD("Wi-Fi Card");

    private final String description;

    ComponentType(String description) {
        this.description = description;
    }

    ComponentType() {
        this.description = null;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return this == CPU || this == MB || this == RAM || this == PSU || this == CASE;
    }

    public boolean isStorage() {
        return this == STORAGE || this == SSD || this == HDD || this == M2;
    }

    public boolean isCooling() {
        return this == COOLER || this == CASE_FAN || this == LIQUID_COOLER;
    }

    public boolean isPeripheral() {
        return this == MONITOR || this == KEYBOARD || this == MOUSE || this == HEADSET;
    }

    public boolean isNetworking() {
        return this == NETWORK_CARD || this == WIFI_CARD;
    }
} 