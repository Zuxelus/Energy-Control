package com.zuxelus.energycontroladdon;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KitFurnace extends KitBase {

	public KitFurnace() {
		super("kit_furnace");
	}
	
	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityFurnace))
			return ItemStack.EMPTY;

		ItemStack sensorLocationCard = new ItemStack(EnergyControlAddon.CARD_FURNACE);
		ItemStackHelper.setCoordinates(sensorLocationCard, pos);
		return sensorLocationCard;
	}
}
