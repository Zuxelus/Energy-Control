package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPanelToolkit extends Item {

	public ItemPanelToolkit() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18n.format("item.panel_toolkit.info"));
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityFacing))
			return EnumActionResult.PASS;

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof FacingBlock) {
			EnumFacing facing = ((FacingBlock) block).getFacing(world, pos);
			if (facing == side) {
				block.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
			} else
				((FacingBlock) block).setFacing(world, pos, side, player);
			return EnumActionResult.SUCCESS;
		}
		if (block instanceof FacingHorizontal) {
			EnumFacing facing = ((FacingHorizontal) block).getFacing(world, pos);
			if (facing == side) {
				block.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
			} else
				((FacingHorizontal) block).setFacing(world, pos, side, player);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
