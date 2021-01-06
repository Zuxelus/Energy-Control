package com.zuxelus.energycontrol.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMain extends BlockContainer {
	static int[][] mapping = {
			{1, 0, 2, 2, 2, 2 }, { 0, 1, 2, 2, 2, 2 },
			{ 2, 2, 1, 0, 3, 3 }, { 2, 2, 0, 1, 3, 3 },
			{ 3, 3, 3, 3, 1, 0 }, { 3, 3, 3, 3, 0, 1 } };
	static int[][] horMapping = {
			{ 1, 0, 3, 2, 4, 5 }, { 0, 1, 2, 3, 4, 5 },
			{ 3, 2, 1, 0, 4, 5 }, { 3, 2, 0, 1, 5, 4 },
			{ 3, 2, 4, 5, 1, 0 }, { 3, 2, 5, 4, 0, 1 } };
	public static Map<Integer, BlockBase> blocks;

	public BlockMain() {
		super(Material.iron);
		setHardness(6.0F);
		setCreativeTab(EnergyControl.creativeTab);
		blocks = new HashMap<Integer, BlockBase>();
		register(new ThermalMonitor());
		register(new IndustrialAlarm());
		register(new HowlerAlarm());
		register(new RemoteThermo());
		register(new InfoPanel());
		register(new InfoPanelExtender());
		register(new EnergyCounter());
		register(new AverageCounter());
		register(new RangeTrigger());
		register(new AdvancedInfoPanel());
		register(new AdvancedInfoPanelExtender());
		register(new KitAssembler());
		if (CrossModLoader.ic2.getModType() == "IC2Exp")
			register(new AFSU());
		register(new Timer());
	}

	public void register(BlockBase block) {
		blocks.put(block.getDamage(), block);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return blocks.containsKey(metadata) ? blocks.get(metadata).createNewTileEntity() : null;
	}

	@Override
	public int getRenderType() {
		return EnergyControl.instance.modelId;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, int side, int meta, ItemStack stack) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (isSolidBlockRequired(meta)) {
			ForgeDirection sidedir = ForgeDirection.getOrientation(side);
			if (canPlaceBlock(world, x, y, z, sidedir.getOpposite())) {
				((TileEntityFacing) te).setFacing(sidedir);
				setRotation((TileEntityFacing) te, player);
			} else
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
					if (canPlaceBlock(world, x, y, z, dir)) {
						((TileEntityFacing) te).setFacing(dir.getOpposite());
						setRotation((TileEntityFacing) te, player);
						break;
					}
			checkForDrop(world, x, y, z, te, meta);
		} else if (te instanceof IBlockHorizontal) {
			((TileEntityFacing) te).setFacing(TileEntityFacing.getHorizontalFacing(player));
		} else if (te instanceof TileEntityFacing) {
			((TileEntityFacing) te).setFacing(getBlockDirection(player));
			if (player.rotationPitch <= -65)
				((TileEntityFacing) te).setRotation(TileEntityFacing.getHorizontalFacing(player).getOpposite());
			else
				((TileEntityFacing) te).setRotation(TileEntityFacing.getHorizontalFacing(player));
		}
		if (te instanceof TileEntityAFSU) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null) {
				double energy = tag.getDouble("energy");
				if (energy != 0)
					((TileEntityAFSU) te).setEnergy(energy);
			}
		}
	}

	private static boolean isSolidBlockRequired(int meta) {
		return blocks.containsKey(meta) && blocks.get(meta).isSolidBlockRequired();
	}

	private static boolean canPlaceBlock(World world, int x, int y, int z, ForgeDirection direction) {
		return world.isSideSolid(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, direction.getOpposite());
	}

	private boolean checkForDrop(World world, int x, int y, int z, TileEntity te, int meta) {
		if (!isSolidBlockRequired(meta))
			return true;

		if (!(te instanceof TileEntityFacing))
			return true;
		
		if (canPlaceBlock(world, x, y, z, ((TileEntityFacing) te).getFacingForge().getOpposite()))
			return true;

		if (world.getBlock(x, y, z) == this) {
			dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		return false;
	}

	private static ForgeDirection getBlockDirection(EntityLivingBase placer) {
		if (placer.rotationPitch >= 65)
			return ForgeDirection.UP;
		if (placer.rotationPitch <= -65)
			return ForgeDirection.DOWN;
		return TileEntityFacing.getHorizontalFacing(placer);
	}

	private static void setRotation(TileEntityFacing te, EntityLivingBase placer) {
		switch (te.getFacingForge()) {
		case UP:
		case DOWN:
			te.setRotation(TileEntityFacing.getHorizontalFacing(placer));
			break;
		case UNKNOWN:
		case NORTH:
		case SOUTH:
		case EAST:
		case WEST:
			te.setRotation(ForgeDirection.DOWN);
			break;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (!world.isRemote)
			world.markBlockForUpdate(x, y, z);
	}

	public static float[] getBlockBounds(int damage, TileEntity te) {
		return blocks.containsKey(damage) ? blocks.get(damage).getBlockBounds(te) : new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		int meta = blockAccess.getBlockMetadata(x, y, z);
		float[] bounds = getBlockBounds(meta, blockAccess.getTileEntity(x, y, z));
		setBlockBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem()))
			return true;
		int meta = world.getBlockMetadata(x, y, z);
		if ((world.isRemote && (meta == BlockDamages.DAMAGE_THERMAL_MONITOR || meta == BlockDamages.DAMAGE_HOWLER_ALARM
				|| meta == BlockDamages.DAMAGE_INDUSTRIAL_ALARM || meta == BlockDamages.DAMAGE_TIMER)) || !world.isRemote)
			player.openGui(EnergyControl.instance, meta, world, x, y, z);
		return true;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return false;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int direction) {
		TileEntity te = iblockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityThermo)
			return ((TileEntityThermo) te).getPowered() ? ((TileEntityThermo) te).getFacingForge().ordinal() != direction ? 15 : 0 : 0;
		if (te instanceof TileEntityRangeTrigger)
			return ((TileEntityRangeTrigger) te).getPowered() ? 15 : 0;
		if (te instanceof TileEntityAFSU)
			return ((TileEntityAFSU) te).getPowered() ? 15 : 0;
		if (te instanceof TileEntityTimer)
			return ((TileEntityTimer) te).getPowered() ? ((TileEntityTimer) te).getFacingForge().ordinal() != direction ? 15 : 0 : 0;
		return 0;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (blocks.containsKey(meta))
			return blocks.get(meta).getIconFromSide(side);
		return null;
	}

	@Override
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) {
		TileEntity te = blockaccess.getTileEntity(x, y, z);
		int meta = blockaccess.getBlockMetadata(x, y, z);
		if (!(te instanceof TileEntityFacing))
			return getIcon(side, meta);
		if (te instanceof TileEntityKitAssembler) {
			int face = getFacingSide((TileEntityFacing) te, side);
			if (face == 1 && ((TileEntityKitAssembler) te).getActive())
				face = 6;
			return getIcon(face, meta);
		}
		if (te instanceof TileEntityRangeTrigger) {
			int face = getFacingSide((TileEntityFacing) te, side);
			if (face == 1)
				switch (((TileEntityRangeTrigger) te).getStatus())
				{
				case 1:
					face = 6;
					break;
				case 2:
					face = 7;
					break;
				}
			return getIcon(face, meta);
		}
		if (te instanceof TileEntityAFSU) {
			switch (side) {
			case 1:
				if (((TileEntityFacing) te).getFacingForge().ordinal() == 1)
					return getIcon(1, meta);
				return getIcon(2, meta);
			default:
				if (((TileEntityFacing) te).getFacingForge().ordinal() == side)
					return getIcon(1, meta);
				return getIcon(0, meta);
			}
		}
		return getIcon(getFacingSide((TileEntityFacing) te, side), meta);
	}
	
	private static int getFacingSide(TileEntityFacing te, int side) {
		if (te instanceof TileEntityKitAssembler)
			return horMapping[te.getFacingForge().ordinal()][side];
		return mapping[te.getFacingForge().ordinal()][side];
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		for (BlockBase block : blocks.values())
			block.registerIcons(iconRegister);
	}

	public BlockBase getBlockBase(int metadata) {
		return blocks.containsKey(metadata) ? blocks.get(metadata) : null;
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		int metadata = world.getBlockMetadata(x, y, z);
		return !isSolidBlockRequired(metadata);
	}

	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityIndustrialAlarm)
			return ((TileEntityIndustrialAlarm) te).lightLevel;
		if (te instanceof TileEntityInfoPanel)
			return ((TileEntityInfoPanel) te).getPowered() ? 10 : 0;
		if (te instanceof TileEntityInfoPanelExtender)
			return ((TileEntityInfoPanelExtender) te).getPowered() ? 10 : 0;
		return getLightValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (BlockBase block : blocks.values())
			if (block.damage == BlockDamages.DAMAGE_AFSU) {
				list.add(AFSU.getStackwithEnergy(0));
				list.add(AFSU.getStackwithEnergy(TileEntityAFSU.CAPACITY));
			} else
				list.add(new ItemStack(this, 1, block.damage));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory) te).dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}
}
