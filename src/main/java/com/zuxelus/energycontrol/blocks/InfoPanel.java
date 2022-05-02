package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class InfoPanel extends FacingBlock {

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) > 5 ? 10 : 0;
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityInfoPanel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		int id = mapping[meta % 6][side];
		if (id > 1)
			return icons[2];
		return icons[id];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"info_panel/panel_back");
		icons[1] = registerIcon(ir,"info_panel/panel_face");
		icons[2] = registerIcon(ir,"info_panel/panel_side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityInfoPanel))
			return true;
		if (!world.isRemote && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == facing)
			if (((TileEntityInfoPanel) te).runTouchAction(player.getHeldItem(), x, y, z, hitX, hitY, hitZ))
				return true;
		return super.onBlockActivated(world, x, y, z, player, facing, hitX, hitY, hitZ);
	}

	@Override
	public int getMetaForPlacement(World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ, int meta, EntityPlayer placer) {
		int result = super.getMetaForPlacement(world, x, y, z, facing, hitX, hitY, hitZ, meta, placer);
		return world.isBlockIndirectlyGettingPowered(x, y, z) ? result + 6 : result;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.isRemote)
			return;

		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityInfoPanel))
			return;

		((TileEntityInfoPanel) te).updateBlockState();
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int meta = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, meta > 5 ? meta - 6 : meta + 6, 2);
		TileEntity be = world.getTileEntity(x, y, z);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(meta > 5);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityFacing)
			return side == ((TileEntityFacing) te).getFacingForge().ordinal();
		return false;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_INFO_PANEL;
	}
}