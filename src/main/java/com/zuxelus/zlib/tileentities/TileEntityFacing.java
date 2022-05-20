package com.zuxelus.zlib.tileentities;

import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.zlib.blocks.FacingHorizontal;

import cpw.mods.fml.common.Optional;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Optional.Interface(modid = ModIDs.IC2, iface = "ic2.api.tile.IWrenchable")
public abstract class TileEntityFacing extends TileEntity implements IWrenchable {
	protected ForgeDirection facing;
	protected ForgeDirection rotation;

	public ForgeDirection getFacingForge() {
		return facing;
	}

	public void setFacing(int meta) {
		facing = ForgeDirection.getOrientation(meta);
	}

	public void setFacing(ForgeDirection meta) {
		facing = meta;
	}

	protected boolean hasRotation() {
		return false;
	}

	public ForgeDirection getRotation() {
		return rotation;
	}

	public void setRotation(int meta) {
		rotation = ForgeDirection.getOrientation(meta);
	}

	public void setRotation(ForgeDirection meta) {
		rotation = meta;
	}

	protected void readProperties(NBTTagCompound tag) {
		ForgeDirection oldFacing = facing;
		if (tag.hasKey("facing"))
			facing = ForgeDirection.getOrientation(tag.getInteger("facing"));
		else
			facing = ForgeDirection.NORTH;
		if (hasRotation()) {
			if (tag.hasKey("rotation"))
				rotation = ForgeDirection.getOrientation(tag.getInteger("rotation"));
			else
				rotation = ForgeDirection.NORTH;
		}
	}

	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag.setInteger("facing", facing.ordinal());
		if (hasRotation() && rotation != null)
			tag.setInteger("rotation", rotation.ordinal());
		return tag;
	}

	protected void notifyBlockUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	// IWrenchable, 1.7.10
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer player, int side) {
		return facing.ordinal() != side;
	}

	@Override
	public short getFacing() {
		return (short) facing.ordinal();
	}

	@Override
	public void setFacing(short facing) {
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (block instanceof FacingHorizontal && (facing == 0 || facing == 1))
			return;

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		meta = meta - meta % 6 + facing;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
		setFacing((int) facing);
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer player) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1;
	}
}
