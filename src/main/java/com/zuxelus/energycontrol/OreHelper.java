package com.zuxelus.energycontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class OreHelper {
	public Block block;
	int minHeight, maxHeight, size, count;

	public OreHelper(int minHeight, int maxHeight, int size, int count) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.size = size;
		this.count = count;
	}

	public String getDescription() {
		return String.format("height %d-%d, size %d, count %d", minHeight, maxHeight, size, count);
	}

	public static String getId(Block block, int meta) {
		return String.format("#%04d/%d", Block.getIdFromBlock(block), meta);
	}

	public static void initList(WorldServer[] worlds) {
		EnergyControl.oreHelper = new HashMap<String, OreHelper>();
		for (int i = 0; i < worlds.length; i++) {
			IChunkGenerator generator = worlds[i].provider.createChunkGenerator();
			if (generator instanceof ChunkGeneratorOverworld) {
				try {
					Field field = ChunkGeneratorOverworld.class.getDeclaredField("settings");
					field.setAccessible(true);
					ChunkGeneratorSettings over = (ChunkGeneratorSettings) field.get(generator);

					EnergyControl.oreHelper.put(getId(Blocks.GRAVEL, 0), new OreHelper(over.gravelMinHeight, over.gravelMaxHeight, over.gravelSize, over.gravelCount));
					EnergyControl.oreHelper.put(getId(Blocks.STONE, BlockStone.EnumType.GRANITE.ordinal()), new OreHelper(over.graniteMinHeight, over.graniteMaxHeight, over.graniteSize, over.graniteCount));
					EnergyControl.oreHelper.put(getId(Blocks.STONE, BlockStone.EnumType.DIORITE.ordinal()), new OreHelper(over.dioriteMinHeight, over.dioriteMaxHeight, over.dioriteSize, over.dioriteCount));
					EnergyControl.oreHelper.put(getId(Blocks.STONE, BlockStone.EnumType.ANDESITE.ordinal()), new OreHelper(over.andesiteMinHeight, over.andesiteMaxHeight, over.andesiteSize, over.andesiteCount));
					EnergyControl.oreHelper.put(getId(Blocks.COAL_ORE, 0), new OreHelper(over.coalMinHeight, over.coalMaxHeight, over.coalSize, over.coalCount));
					EnergyControl.oreHelper.put(getId(Blocks.IRON_ORE, 0), new OreHelper(over.ironMinHeight, over.ironMaxHeight, over.ironSize, over.ironCount));
					EnergyControl.oreHelper.put(getId(Blocks.GOLD_ORE, 0), new OreHelper(over.goldMinHeight, over.goldMaxHeight, over.goldSize, over.goldCount));
					EnergyControl.oreHelper.put(getId(Blocks.REDSTONE_ORE, 0), new OreHelper(over.redstoneMinHeight, over.redstoneMaxHeight, over.redstoneSize, over.redstoneCount));
					//EnergyControl.oreHelper.put(Item.getItemFromBlock(Blocks.LAPIS_ORE), new OreHelper(Blocks.LAPIS_ORE, over.lap .lapisMinHeight, over.goldMaxHeight, over.goldSize, over.goldCount));
					EnergyControl.oreHelper.put(getId(Blocks.DIAMOND_ORE, 0), new OreHelper(over.diamondMinHeight, over.diamondMaxHeight, over.diamondSize, over.diamondCount));
					//EnergyControl.oreHelper.put(Item.getItemFromBlock(Blocks.EMERALD_ORE), new OreHelper(Blocks.EMERALD_ORE, over .goldMinHeight, over.goldMaxHeight, over.goldSize, over.goldCount));
					//EnergyControl.oreHelper.put(Item.getItemFromBlock(Blocks.QUARTZ_ORE), new OreHelper(Blocks.QUARTZ_ORE, over  .goldMinHeight, over.goldMaxHeight, over.goldSize, over.goldCount));
				} catch (Throwable t) { }
				CrossModLoader.nuclearCraft.loadOreInfo();
				CrossModLoader.ic2.loadOreInfo();
			}
		}
	}
}
