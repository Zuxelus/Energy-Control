package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;

public class TileEntityHowlerAlarm extends TileEntityFacing implements ITickable, IRedstoneConsumer, ITilePacketHandler {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final float BASE_SOUND_RANGE = 16F;
	private static final String SOUND_PREFIX = "energycontrol:alarm-";

	public int range;
	public boolean powered;

	public String soundName;
	private String prevSoundName;

	protected int updateTicker;
	protected int tickRate;
	private TileEntitySound sound;

	public TileEntityHowlerAlarm() {
		tickRate = 60;
		updateTicker = 0;
		powered = false;
		soundName = prevSoundName = DEFAULT_SOUND_NAME;
		range = EnergyControl.config.howlerAlarmRange;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int r) {
		if (!worldObj.isRemote && range != r)
			notifyBlockUpdate();
		range = r;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String name) {
		soundName = name;
		if (!worldObj.isRemote && !prevSoundName.equals(soundName))
			notifyBlockUpdate();
		if (worldObj.isRemote) {
			if (EnergyControl.instance.availableAlarms != null && !EnergyControl.instance.availableAlarms.contains(soundName)) {
				EnergyControl.logger.info(String.format("Can't set sound '%s' at %d,%d,%d, using default", soundName, pos.getX(), pos.getY(), pos.getZ()));
				soundName = DEFAULT_SOUND_NAME;
			}
		}
		prevSoundName = soundName;
	}

	public boolean getPowered() {
		return powered;
	}

	public void updatePowered(boolean isPowered) {
		if (worldObj.isRemote) {
			powered = isPowered;
			checkStatus();
		}
	}

	private float getNormalizedRange() {
		if (worldObj.isRemote)
			return Math.min(range, EnergyControl.config.SMPMaxAlarmRange) / BASE_SOUND_RANGE;
		return range / BASE_SOUND_RANGE;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("string"))
				setSoundName(tag.getString("string"));
			break;
		case 2:
			if (tag.hasKey("value"))
				setRange(tag.getInteger("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) { }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		powered = worldObj.isBlockPowered(pos);
		tag.setBoolean("powered", powered);
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
		powered = worldObj.isBlockPowered(pos); 
		tag.setBoolean("powered", powered);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("soundName"))
			soundName = prevSoundName = tag.getString("soundName");
		if (tag.hasKey("range"))
			range = tag.getInteger("range");
		if (tag.hasKey("powered"))
			updatePowered(tag.getBoolean("powered"));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setString("soundName", soundName);
		tag.setInteger("range", range);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void invalidate() {
		if (worldObj.isRemote)
			sound.stopAlarm();
		super.invalidate();
	}

	@Override
	public void update() {
		if (worldObj.isRemote) {
			if (updateTicker-- > 0)
					return;
			updateTicker = tickRate;
			checkStatus();
		}
	}

	protected void checkStatus() {
		if (sound == null)
			sound = new TileEntitySound();
		if (powered && !sound.isPlaying())
			sound.playAlarm(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SOUND_PREFIX + soundName, getNormalizedRange(), true);
		
		if (!powered && sound.isPlaying())
			sound.stopAlarm();
	}	

	private void notifyBlockUpdate() {
		IBlockState iblockstate = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}
	
	@Override
	public void neighborChanged() {
		if (!worldObj.isRemote)
			notifyBlockUpdate();
	}
}