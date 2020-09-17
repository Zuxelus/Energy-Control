package com.zuxelus.energycontrol.blocks;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.IBlockHorizontal;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntitySeedAnalyzer;
import com.zuxelus.energycontrol.tileentities.TileEntitySeedLibrary;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSeedManager extends BlockContainer {
	private static final int[][] facingAndSideToSpriteOffset = 
		{ { 0, 5, 1, 1, 1, 1 }, { 0, 5, 1, 1, 1, 1 }, { 0, 5, 1, 2, 1, 1 },
		  { 0, 5, 2, 1, 1, 1 }, { 0, 5, 1, 1, 1, 2 }, { 0, 5, 1, 1, 2, 1 } };
	public IIcon[] analyzer = new IIcon[7];
	public IIcon[] library = new IIcon[6];

	public BlockSeedManager() {
		super(Material.iron);
		setBlockName("seedManager");
		setStepSound(Block.soundTypeMetal);
		setHardness(6.0F);
		setResistance(10.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch (metadata) {
		case 0:
			return new TileEntitySeedAnalyzer();
		case 1:
			return new TileEntitySeedLibrary();
		default:
			return super.createTileEntity(world, metadata);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		analyzer[0] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_bottom");
		analyzer[1] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_side");
		analyzer[2] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_front_off");
		analyzer[3] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_front_on");
		analyzer[4] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_front_anim");
		analyzer[5] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_top_off");
		analyzer[6] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_analyzer_top_on");
		library[0] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_bottom");
		library[1] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_side");
		library[2] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_top_off");
		library[3] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_top_on");
		library[4] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_front_off");
		library[5] = iconRegister.registerIcon(EnergyControl.MODID + ":seed_manager/seed_library_front_on");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		switch (metadata) {
		case 0:
			switch (side) {
			case 0: // bottom
				return analyzer[0];
			case 1: // top
				return analyzer[5];
			case 2:
				return analyzer[1];
			case 3: // front
				return analyzer[2];
			case 4:
				return analyzer[1];
			case 5:
				return analyzer[1];
			}
		case 1:
			switch (side) {
			case 0:
				return library[0];
			case 1:
				return library[2];
			case 2:
				return library[1];
			case 3:
				return library[4];
			case 4:
				return library[1];
			case 5:
				return library[1];
			}
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side) {
		TileEntity te = blockaccess.getTileEntity(x, y, z);
		int meta = blockaccess.getBlockMetadata(x, y, z);
		if (te instanceof TileEntitySeedAnalyzer) {
			int face = ((TileEntityFacing)te).getFacingForge().ordinal();
			int metaSide = getTextureSubIndex(((TileEntityFacing)te).getFacingForge().getOpposite().ordinal(), side);
			if (metaSide == 2) {
				if (((TileEntitySeedAnalyzer) te).getActive())
					return analyzer[4];
				if (((TileEntitySeedAnalyzer) te).getEnergy() != 0)
					return analyzer[3];
				return analyzer[2];
			}
			if (metaSide == 5 && ((TileEntitySeedAnalyzer) te).getActive())
				return analyzer[6];
			return analyzer[metaSide];
		}
		if (te instanceof TileEntitySeedLibrary) {
			int metaSide = getTextureSubIndex(((TileEntityFacing) te).getFacingForge().getOpposite().ordinal(), side);
			if (metaSide == 5) { // top
				if (((TileEntitySeedLibrary) te).getEnergy() != 0)
					return library[3];
				return library[2];
			}
			if (metaSide == 2) { // front
				if (((TileEntitySeedLibrary) te).getEnergy() != 0)
					return library[5];
				return library[4];
			}
			return library[0];
		}
		return getIcon(side, meta);
	}

	public static final int getTextureSubIndex(int facing, int side) {
		return facingAndSideToSpriteOffset[facing][side];
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem()))
			return true;
		player.openGui(EnergyControl.instance, world.getBlockMetadata(x, y, z) + 20, world, x, y, z);
		return true;
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IBlockHorizontal)
			((TileEntityFacing) te).setFacing(getHorizontalFacing(player));
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

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		BlockMain.dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
}
