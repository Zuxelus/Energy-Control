package com.zuxelus.zlib.blocks;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = ModIDs.IC2, iface = "ic2.api.tile.IWrenchable")
public abstract class FacingBlock extends BlockDirectional implements ITileEntityProvider, IWrenchable {

	public FacingBlock() {
		super(Material.IRON);
		setSoundType(SoundType.METAL);
		setHardness(3.0F);
		setCreativeTab(EnergyControl.creativeTab);
	}

	protected abstract TileEntityFacing createTileEntity();

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityFacing te = createTileEntity();
		te.setFacing(meta);
		return te;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch >= 65)
			return getDefaultState().withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch <= -65)
			return getDefaultState().withProperty(FACING, EnumFacing.DOWN);
		switch (MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return getDefaultState().withProperty(FACING, EnumFacing.NORTH);
		case 1:
			return getDefaultState().withProperty(FACING, EnumFacing.EAST);
		case 2:
			return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
		case 3:
			return getDefaultState().withProperty(FACING, EnumFacing.WEST);
		}
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			switch (((TileEntityFacing) te).getFacing()) {
			case UP:
			case DOWN:
				((TileEntityFacing) te).setRotation(placer.getHorizontalFacing().getOpposite());
				break;
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				((TileEntityFacing) te).setRotation(EnumFacing.DOWN);
				break;
			}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
			world.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(world, pos, state);
	}

	protected abstract int getBlockGuiId();

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem(hand)))
			return true;
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, getBlockGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	// IWrenchable
	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			return ((TileEntityFacing) te).getFacing();
		return EnumFacing.NORTH;
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing) {
			((TileEntityFacing) te).setFacing(newDirection.getIndex());
			if (newDirection.getIndex() > 1 && te instanceof TileEntityInfoPanel)
				((TileEntityFacing) te).setRotation(newDirection.getIndex());
			world.setBlockState(pos, getDefaultState().withProperty(FACING, newDirection));
			return true;
		}
		return false;
	}

	@Override
	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		if (!(te instanceof TileEntityInventory))
			return getDrops(world, pos, state, 1);
		List<ItemStack> list = ((TileEntityInventory) te).getDrops(fortune);
		list.add(new ItemStack(this));
		return list;
	}
}
