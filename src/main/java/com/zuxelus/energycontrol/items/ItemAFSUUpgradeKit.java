package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;

import ic2.core.block.wiring.BlockElectric;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemAFSUUpgradeKit extends Item {

	public ItemAFSUUpgradeKit() {
		setCreativeTab(EnergyControl.creativeTab);
		setTextureName(EnergyControl.MODID + ":" + "afsu_upgrade_kit");
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return false;
		
		Block block = world.getBlock(x, y, z);
		if (!(block instanceof BlockElectric))
			return false;
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityElectricMFSU))
			return false;

		TileEntityElectricMFSU mfsu = ((TileEntityElectricMFSU) te);
		int eustored = mfsu.getStored();
		int facing = mfsu.getFacing();
		byte mode = mfsu.redstoneMode;
		ItemStack[] items = new ItemStack[mfsu.getSizeInventory()];
		for (int i = 0; i < items.length; i++)
			items[i] = mfsu.getStackInSlot(i);
		world.removeTileEntity(x, y, z);
		world.setBlock(x, y, z, ItemHelper.blockMain, BlockDamages.DAMAGE_AFSU, 2);
		TileEntityAFSU afsu = new TileEntityAFSU();
		afsu.addEnergy(eustored);
		afsu.setFacing(facing);
		afsu.setRedstoneMode(mode);
		for (int j = 0; j < items.length; j++)
			afsu.setInventorySlotContents(j, items[j]);
		world.setTileEntity(x, y, z, afsu);
		afsu.markDirty();
		stack.stackSize--;
		if (stack.stackSize < 0)
			stack = null;
		return true;
	}
}
