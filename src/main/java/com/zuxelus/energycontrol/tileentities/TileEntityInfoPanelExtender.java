package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityInfoPanelExtender extends TileEntityFacing implements IScreenPart {
	protected boolean init;

	protected Screen screen;
	private boolean partOfScreen;

	private int coreX;
	private int coreY;
	private int coreZ;

	public TileEntityInfoPanelExtender() {
		super();
		init = false;
		screen = null;
		partOfScreen = false;
		coreX = 0;
		coreY = 0;
		coreZ = 0;
	}

	@Override
	public void setFacing(int meta) {
		ForgeDirection newFacing = ForgeDirection.getOrientation(meta);
		if (facing == newFacing)
			return;
		facing = newFacing;
		if (init) {
			EnergyControl.instance.screenManager.unregisterScreenPart(this);
			EnergyControl.instance.screenManager.registerInfoPanelExtender(this);
		}
	}

	private void updateScreen() {
		if (partOfScreen && screen == null) {
			TileEntity core = worldObj.getTileEntity(coreX, coreY, coreZ);
			if (core instanceof TileEntityInfoPanel) {
				screen = ((TileEntityInfoPanel) core).getScreen();
				if (screen != null)
					screen.init(true, worldObj);
			}
		}
		if (worldObj.isRemote && !partOfScreen && screen != null)
			setScreen(null);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		if (!worldObj.isRemote)
			return;
		readProperties(pkt.func_148857_g());
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("partOfScreen"))
			partOfScreen = tag.getBoolean("partOfScreen");
		if (tag.hasKey("coreX")) {
			coreX = tag.getInteger("coreX");
			coreY = tag.getInteger("coreY");
			coreZ = tag.getInteger("coreZ");
		}
		if (worldObj != null) {
			updateScreen();
			if (worldObj.isRemote)
				worldObj.func_147451_t(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setBoolean("partOfScreen", partOfScreen);
		tag.setInteger("coreX", coreX);
		tag.setInteger("coreY", coreY);
		tag.setInteger("coreZ", coreZ);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	@Override
	public void invalidate() {
		if (!worldObj.isRemote)
			EnergyControl.instance.screenManager.unregisterScreenPart(this);
		super.invalidate();
	}

	@Override
	public void updateEntity() {
		if (init)
			return;

		if (!worldObj.isRemote && !partOfScreen)
			EnergyControl.instance.screenManager.registerInfoPanelExtender(this);

		updateScreen();
		init = true;
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
		if (screen != null) {
			partOfScreen = true;
			TileEntityInfoPanel core = screen.getCore(worldObj);
			if (core != null) {
				coreX = core.xCoord;
				coreY = core.yCoord;
				coreZ = core.zCoord;

				int metaCore =  worldObj.getBlockMetadata(coreX, coreY, coreZ);
				int meta =  worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
				if (meta != metaCore)
					worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metaCore, 2);
				return;
			}
		} else {
			int meta =  worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			if (meta > 5)
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta - 6, 2);
		}
		partOfScreen = false;
		coreX = 0;
		coreY = 0;
		coreZ = 0;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}

	public TileEntityInfoPanel getCore() {
		if (screen == null)
			return null;
		return screen.getCore(worldObj);
	}

	@Override
	public void updateData() { }

	@Override
	public void updateTileEntity() {
		notifyBlockUpdate();
	}

	public boolean getColored() {
		if (screen == null)
			return false;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (core == null)
			return false;
		return core.getColored();
	}

	public boolean getPowered() {
		if (screen == null)
			return false;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (core == null)
			return false;
		return core.powered;
	}

	public int getColorBackground() {
		if (screen == null)
			return 2;
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (core == null)
			return 2;
		return core.getColorBackground();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@SuppressWarnings("incomplete-switch")
	@SideOnly(Side.CLIENT)
	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			switch (getFacingForge()) {
			case SOUTH:
				return 1 * boolToInt(xCoord == scr.minX) + 2 * boolToInt(xCoord == scr.maxX) + 4 * boolToInt(yCoord == scr.minY) + 8 * boolToInt(yCoord == scr.maxY);
			case WEST:
				return 8 * boolToInt(zCoord == scr.minZ) + 4 * boolToInt(zCoord == scr.maxZ) + 1 * boolToInt(yCoord == scr.minY) + 2 * boolToInt(yCoord == scr.maxY);
			case EAST:
				return 8 * boolToInt(zCoord == scr.minZ) + 4 * boolToInt(zCoord == scr.maxZ) + 2 * boolToInt(yCoord == scr.minY) + 1 * boolToInt(yCoord == scr.maxY);
			case NORTH:
				return 1 * boolToInt(xCoord == scr.minX) + 2 * boolToInt(xCoord == scr.maxX) + 8 * boolToInt(yCoord == scr.minY) + 4 * boolToInt(yCoord == scr.maxY);
			case UP:
				return 1 * boolToInt(xCoord == scr.minX) + 2 * boolToInt(xCoord == scr.maxX) + 8 * boolToInt(zCoord == scr.minZ) + 4 * boolToInt(zCoord == scr.maxZ);
			case DOWN:
				return 1 * boolToInt(xCoord == scr.minX) + 2 * boolToInt(xCoord == scr.maxX) + 4 * boolToInt(zCoord == scr.minZ) + 8 * boolToInt(zCoord == scr.maxZ);
			}
		}
		return 15;
	}

	private int boolToInt(boolean b) {
		return b ? 1 : 0;
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockInfoPanelExtender);
	}
}