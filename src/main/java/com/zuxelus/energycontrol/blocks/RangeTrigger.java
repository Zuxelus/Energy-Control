package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInventory;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RangeTrigger extends BlockHorizontal implements ITileEntityProvider {
	public static final PropertyEnum<EnumState> STATE = PropertyEnum.<EnumState>create("state", EnumState.class);
	private boolean powered = false;

	public RangeTrigger() {
		super(Material.IRON);
		setHardness(0.5F);
		setCreativeTab(EnergyControl.creativeTab);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityRangeTrigger te = new TileEntityRangeTrigger();
		te.setFacing(meta);
		return te;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, EnumState.getState(meta / 4));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex() + 4 * state.getValue(STATE).getId();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING))).withProperty(STATE, state.getValue(STATE));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING))).withProperty(STATE, state.getValue(STATE));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, STATE });
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(STATE, EnumState.OFF);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory)te).dropItems(world, pos);		
		super.breakBlock(world, pos, state);
	}
	
	@Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return powered ? 15 : 0;
    }

	@Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return powered ? 15 : 0;
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_RANGE_TRIGGER, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
    
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	public void setPowered(boolean value) {
		powered = value; 
	}
	
    public static enum EnumState implements IStringSerializable
    {
        OFF(0,"off"),
        ON(1,"on"),
        ERROR(2,"error");

        private final int id;
        private final String name;

        private EnumState(int id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public int getId()
        {
            return this.id;
        }

		@Override
		public String getName() {
			return name;
		}
		
		public static EnumState getState(int id) {
			switch(id) {
			default:
				return OFF;
			case 1:
				return ON;
			case 2:
				return ERROR;
			}
		}
    }
}
