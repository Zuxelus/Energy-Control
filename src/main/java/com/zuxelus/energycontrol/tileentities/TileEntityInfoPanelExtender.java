package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityInfoPanelExtender extends TileEntityFacing implements ITickable, IScreenPart {
	protected boolean init;

	private Screen screen;
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

	private void updateScreen() {
		if (partOfScreen && screen == null) {
			TileEntity core = worldObj.getTileEntity(new BlockPos(coreX, coreY, coreZ));
			if (core != null && core instanceof TileEntityInfoPanel) {
				screen = ((TileEntityInfoPanel) core).getScreen();
				if (screen != null)
					screen.init(true, worldObj);
			}
		}		
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		return new SPacketUpdateTileEntity(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		return tag;
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
				worldObj.checkLight(pos);
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!worldObj.isRemote)
			EnergyControl.instance.screenManager.unregisterScreenPart(this);
	}

	@Override
	public void update() {
		if (init)
			return;
		
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && !partOfScreen)
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
				coreX = core.getPos().getX();
				coreY = core.getPos().getY();
				coreZ = core.getPos().getZ();
				return;
			}
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

	@Override
	public void updateData() { }

	@Override
	public void notifyBlockUpdate() {
		IBlockState iblockstate = worldObj.getBlockState(pos);
		Block block = iblockstate.getBlock();
		worldObj.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	public boolean getPowered() {
		if (screen == null)
			return false;		
		TileEntityInfoPanel core = screen.getCore(worldObj);
		if (core == null)
			return false;
		return core.powered;
	}
}