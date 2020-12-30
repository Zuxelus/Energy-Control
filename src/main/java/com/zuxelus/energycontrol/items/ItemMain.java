package com.zuxelus.energycontrol.items;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockBase;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockMain;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMain extends ItemBlock {

	public ItemMain(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		BlockBase block = ItemHelper.blockMain.getBlockBase(item.getItemDamage());
		if (block == null)
			return "";
		return block.getName();
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			if (field_150939_a instanceof BlockMain)
				((BlockMain) field_150939_a).onBlockPlacedBy(world, x, y, z, player, side, metadata, stack);
			else
				field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
		if (stack.getItemDamage() == BlockDamages.DAMAGE_AFSU) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null) {
				tooltip.add(I18n.format("ic2.item.tooltip.Output") + String.format(" %sEU/t ", TileEntityAFSU.OUTPUT)
						+ I18n.format("ic2.item.tooltip.Capacity")
						+ String.format(" %sm EU ", TileEntityAFSU.CAPACITY / 1000000));
				tooltip.add(I18n.format("ic2.item.tooltip.Store") + String.format(" %s EU", Math.round(tag.getDouble("energy"))));
			}
		}
	}
}