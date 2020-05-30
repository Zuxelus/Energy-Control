package com.zuxelus.energycontrol.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.IBlockHorizontal;
import com.zuxelus.energycontrol.tileentities.IEnergyCounter;
import com.zuxelus.energycontrol.tileentities.IRedstoneConsumer;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInventory;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;
import ic2.core.item.tool.ItemToolPainter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

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

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, int side, int meta) {
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
			((TileEntityFacing) te).setFacing(getHorizontalFacing(player));
		} else if (te instanceof TileEntityFacing) {
			((TileEntityFacing) te).setFacing(getBlockDirection(player));
			if (player.rotationPitch <= -65)
				((TileEntityFacing) te).setRotation(getHorizontalFacing(player).getOpposite());
			else
				((TileEntityFacing) te).setRotation(getHorizontalFacing(player));
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
		return getHorizontalFacing(placer);
	}

	private static ForgeDirection getHorizontalFacing(EntityLivingBase placer) {
		switch (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return ForgeDirection.NORTH;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.SOUTH;
		case 3:
			return ForgeDirection.WEST;
		}
		return ForgeDirection.NORTH;
	}

	private static void setRotation(TileEntityFacing te, EntityLivingBase placer) {
		switch (te.getFacingForge()) {
		case UP:
		case DOWN:
			te.setRotation(getHorizontalFacing(placer));
			break;
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
		TileEntity te = world.getTileEntity(x, y, z);
		if (checkForDrop(world, x, y, z, te, world.getBlockMetadata(x, y, z)))
			if (te instanceof IRedstoneConsumer)
				((IRedstoneConsumer) te).neighborChanged();
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
		player.openGui(EnergyControl.instance, world.getBlockMetadata(x, y, z), world, x, y, z);
		return true;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return false;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int direction) {
		return isProvidingStrongPower(iblockaccess, x, y, z, direction);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess iblockaccess, int x, int y, int z, int direction) {
		TileEntity te = iblockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityThermo)
			return ((TileEntityThermo) te).getPowered() ? ((TileEntityThermo) te).getFacingForge().ordinal() != direction ? 15 : 0 : 0;
		if (te instanceof TileEntityRangeTrigger)
			return ((TileEntityRangeTrigger) te).getPowered() ? 15 : 0;
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
		return getIcon(getFacingSide((TileEntityFacing) te, side), meta);
	}
	
	private int getFacingSide(TileEntityFacing te, int side) {
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

	@Override
	public void getSubBlocks(Item id, CreativeTabs tab, List itemList) {
		for (BlockBase block : blocks.values())
			itemList.add(new ItemStack(this, 1, block.damage));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityInventory))
			return;

		TileEntityInventory inventory = (TileEntityInventory) te;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz,
						new ItemStack(stack.getItem(), stack.stackSize, stack.getItemDamage()));

				if (stack.hasTagCompound())
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				stack.stackSize = 0;
			}
		}
	}
}
