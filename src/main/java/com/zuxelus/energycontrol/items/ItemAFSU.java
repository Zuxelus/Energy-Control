package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;

import ic2.api.energy.EnergyNet;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemAFSU extends ItemBlock {

	public ItemAFSU(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		tooltip.add(I18n.format("ic2.item.tooltip.PowerTier",TileEntityAFSU.TIER));
		tooltip.add(String.format("%s %.0f %s %s %d M %s", I18n.format("ic2.item.tooltip.Output"), Double.valueOf(EnergyNet.instance.getPowerFromTier(TileEntityAFSU.TIER)),
				I18n.format("ic2.generic.text.EUt"), I18n.format("ic2.item.tooltip.Capacity"), Integer.valueOf(TileEntityAFSU.CAPACITY) / 1000000, I18n.format("ic2.generic.text.EU")));
		tooltip.add(I18n.format("ic2.item.tooltip.Store") + " " + (long) ItemStackHelper.getOrCreateNbtData(stack).getDouble("energy") + " " + I18n.format("ic2.generic.text.EU"));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble("energy", 0);
	}
}
