package com.zuxelus.zlib.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class FacingBlock extends BlockContainer {
	protected static int[][] mapping = {{1, 0, 2, 2, 2, 2 }, { 0, 1, 2, 2, 2, 2 }, { 2, 2, 1, 0, 3, 3 },
		{ 2, 2, 0, 1, 3, 3 }, { 3, 3, 3, 3, 1, 0 }, { 3, 3, 3, 3, 0, 1 } };
	protected IIcon[] icons = new IIcon[7];

	public FacingBlock() {
		super(Material.iron);
		setStepSound(Block.soundTypeMetal);
		setHardness(3.0F);
		setCreativeTab(EnergyControl.creativeTab);
	}

	protected abstract TileEntityFacing createTileEntity();

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityFacing te = createTileEntity();
		te.setFacing(meta % 6);
		return te;
	}

	@Override
	public int getRenderType() {
		return EnergyControl.instance.modelId;
	}

	public int getMetaForPlacement(World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ, int meta, EntityPlayer placer) {
		if (placer.rotationPitch >= 65)
			return ForgeDirection.UP.ordinal();
		if (placer.rotationPitch <= -65)
			return ForgeDirection.DOWN.ordinal();
		switch (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			default:
			return ForgeDirection.NORTH.ordinal();
		case 1:
			return ForgeDirection.EAST.ordinal();
		case 2:
			return ForgeDirection.SOUTH.ordinal();
		case 3:
			return ForgeDirection.WEST.ordinal();
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityFacing)
			switch (((TileEntityFacing) te).getFacingForge()) {
			case UP:
			case DOWN:
				((TileEntityFacing) te).setRotation(getHorizontalFacing(placer).getOpposite());
				break;
			default: // 1.7.10
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				((TileEntityFacing) te).setRotation(ForgeDirection.DOWN);
				break;
			}
	}

	protected static ForgeDirection getHorizontalFacing(EntityLivingBase placer) { // 1.7.10, no HORIZONTALS in ForgeDirection
		switch (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return ForgeDirection.SOUTH;
		case 1:
			return ForgeDirection.WEST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.EAST;
		}
		return ForgeDirection.NORTH;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory) te).dropItems(world, x, y, z); // 1.7.10, no InventoryHelper
		super.breakBlock(world, x, y, z, block, meta);
	}

	protected abstract int getBlockGuiId();

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem()))
			return true;
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, getBlockGuiId(), world, x, y, z);
		return true;
	}

	public float[] getBlockBounds(ForgeDirection dir) {
		return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
	}

	public float[] getBlockBounds(TileEntityFacing tile) {
		return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		int meta = blockAccess.getBlockMetadata(x, y, z);
		float[] bounds = getBlockBounds(ForgeDirection.getOrientation(meta % 6));

		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityAdvancedInfoPanel || te instanceof TileEntityAdvancedInfoPanelExtender)
			bounds = getBlockBounds((TileEntityFacing) te);
		setBlockBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	public IIcon registerIcon(IIconRegister ir, String name) { // 1.7.10
		return ir.registerIcon(EnergyControl.MODID + ":" + name);
	}
}
