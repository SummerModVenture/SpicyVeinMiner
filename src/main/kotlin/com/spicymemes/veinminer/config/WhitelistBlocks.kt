package com.spicymemes.veinminer.config

import net.minecraft.world.level.block.*

object WhitelistBlocks {

//    fun addDefaultBlocks() {
//        VANILLA_BLOCKS.forEach {
//            ifModLoaded(it.namespace) {
//                IMCHelper.addBlockWhitelist(it)
//            }
//        }
//    }

    val VANILLA_BLOCKS = setOf(
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.GLOWSTONE,
            Blocks.ACACIA_LOG,
            Blocks.BIRCH_LOG,
            Blocks.DARK_OAK_LOG,
            Blocks.JUNGLE_LOG,
            Blocks.OAK_LOG,
            Blocks.SPRUCE_LOG,
            Blocks.STRIPPED_ACACIA_LOG,
            Blocks.STRIPPED_BIRCH_LOG,
            Blocks.STRIPPED_DARK_OAK_LOG,
            Blocks.STRIPPED_JUNGLE_LOG,
            Blocks.STRIPPED_OAK_LOG,
            Blocks.STRIPPED_SPRUCE_LOG,
            Blocks.CRIMSON_STEM,
            Blocks.WARPED_STEM
    ).mapNotNull { it.registryName.toString() }
}
