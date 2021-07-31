package com.spicymemes.veinminer.util

enum class PreferredMode(val codeName: String) {
    DISABLED("disabled"),
    PRESSED("key_pressed"),
    RELEASED("key_released"),
    SNEAK_ACTIVE("sneak_active"),
    SNEAK_INACTIVE("sneak_inactive")
}