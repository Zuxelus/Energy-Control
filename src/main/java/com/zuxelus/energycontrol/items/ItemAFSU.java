package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.EnergyNet;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemAFSU extends ItemBase {

	public ItemAFSU(Block block) {
		super(block);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		//tooltip.add(I18n.format("ic2.item.tooltip.PowerTier",TileEntityAFSU.TIER));
		tooltip.add(String.format("%s %.0f %s %s %d M %s", I18n.format("ic2.item.tooltip.Output"), Double.valueOf(EnergyNet.instance.getPowerFromTier(TileEntityAFSU.TIER)),
				I18n.format("ic2.generic.text.EUt"), I18n.format("ic2.item.tooltip.Capacity"), Integer.valueOf(TileEntityAFSU.CAPACITY) / 1000000, I18n.format("ic2.generic.text.EU")));
		tooltip.add(I18n.format("ic2.item.tooltip.Store") + " " + (long) ItemStackHelper.getTagCompound(stack).getDouble("energy") + " " + I18n.format("ic2.generic.text.EU"));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.uncommon;
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble("energy", 0);
	}
}
