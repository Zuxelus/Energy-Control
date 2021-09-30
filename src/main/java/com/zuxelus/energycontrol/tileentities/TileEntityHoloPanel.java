package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.HoloPanel;
import com.zuxelus.energycontrol.blocks.RangeTrigger;
import com.zuxelus.energycontrol.containers.ContainerHoloPanel;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityHoloPanel extends TileEntityInfoPanel {
	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_POWER = 2;

	public int getPower() {
		ItemStack stack = getStackInSlot(SLOT_UPGRADE_POWER);
		if (stack.isEmpty())
			return 1;
		return stack.getCount() + 1;
	}

	@Override
	public void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		if (iblockstate.getValue(HoloPanel.ACTIVE) == powered) {
			world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
			return;
		}
		IBlockState newState = iblockstate.getBlock().getDefaultState()
				.withProperty(HoloPanel.FACING, iblockstate.getValue(HoloPanel.FACING))
				.withProperty(HoloPanel.ACTIVE, powered);
		world.setBlockState(pos, newState, 3);
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		boolean old = powered;
		super.readProperties(tag);
		if (powered != old && world.isRemote) {
			IBlockState iblockstate = world.getBlockState(pos);
			IBlockState newState = iblockstate.getBlock().getDefaultState()
					.withProperty(HoloPanel.FACING, iblockstate.getValue(HoloPanel.FACING))
					.withProperty(HoloPanel.ACTIVE, powered);
			world.setBlockState(pos, newState, 3);
		}
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
		case SLOT_UPGRADE_POWER:
			return stack.getItem() instanceof ItemComponent && stack.getItemDamage() == ItemComponent.ADVANCED_CIRCUIT;
		default:
			return false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (screen == null)
			return new AxisAlignedBB(pos.add(0, 0, 0), pos.add(1, 1, 1));
		return new AxisAlignedBB(new BlockPos(screen.minX, screen.minY, screen.minZ), new BlockPos(screen.maxX + 1, screen.maxY + getPower(), screen.maxZ + 1));
	}
}
