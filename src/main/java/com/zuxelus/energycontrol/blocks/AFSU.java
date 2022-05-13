package com.zuxelus.energycontrol.blocks;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AFSU extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityAFSU();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		if (side == meta % 6)
			return icons[1];
		if (side == 1)
			return icons[2];
		return icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"afsu/side");
		icons[1] = registerIcon(ir,"afsu/face");
		icons[2] = registerIcon(ir,"afsu/top");
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_AFSU;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return;
		double energy = tag.getDouble("energy");
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityAFSU) || energy == 0)
			return;
		((TileEntityAFSU) te).setEnergy(energy);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote)
			world.markBlockForUpdate(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		drops.add(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("mfsu").copy());
		return drops;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityAFSU))
			return 0;
		return ((TileEntityAFSU) te).getPowered() ? 15 : 0;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List items) {
		items.add(getStackwithEnergy(0));
		items.add(getStackwithEnergy(TileEntityAFSU.CAPACITY));
	}

	public static ItemStack getStackwithEnergy(double energy) { // 1.7.10, public static for IWrenchable
		ItemStack stack = new ItemStack(ModItems.blockAfsu);
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble("energy", energy);
		return stack;
	}
}
