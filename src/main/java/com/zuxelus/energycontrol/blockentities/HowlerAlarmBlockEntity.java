package com.zuxelus.energycontrol.blockentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.BlockEntitySound;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class HowlerAlarmBlockEntity extends BlockEntity implements Tickable, ITilePacketHandler {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final String SOUND_PREFIX = "energycontrol:alarm-";

	public int range;
	public boolean powered;

	public String soundName;
	private String prevSoundName;

	protected int updateTicker;
	protected int tickRate;
	private BlockEntitySound sound;

	public HowlerAlarmBlockEntity() {
		this(ModItems.HOWLER_ALARM_BLOCK_ENTITY);
	}

	public HowlerAlarmBlockEntity(BlockEntityType<?> type) {
		super(type);
		tickRate = 60;
		updateTicker = 0;
		powered = false;
		soundName = prevSoundName = DEFAULT_SOUND_NAME;
		range = ConfigHandler.howlerAlarmRange;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int r) {
		if (!world.isClient && range != r)
			notifyBlockUpdate();
		range = r;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String name) {
		soundName = name;
		if (!world.isClient && !prevSoundName.equals(soundName))
			notifyBlockUpdate();
		if (world.isClient) {
			if (EnergyControl.INSTANCE.availableAlarms != null && !EnergyControl.INSTANCE.availableAlarms.contains(soundName)) {
				EnergyControl.LOGGER.info(String.format("Can't set sound '%s' at %d,%d,%d, using default", soundName, pos.getX(), pos.getY(), pos.getZ()));
				soundName = DEFAULT_SOUND_NAME;
			}
		}
		prevSoundName = soundName;
	}

	public boolean getPowered() {
		return powered;
	}

	public void updatePowered(boolean isPowered) {
		if (world.isClient && isPowered != powered) {
			powered = isPowered;
			checkStatus();
		}
	}

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("string"))
				setSoundName(tag.getString("string"));
			break;
		case 2:
			if (tag.contains("value"))
				setRange(tag.getInt("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(getPos(), 0, toInitialChunkDataTag());
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getCompoundTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
		tag = writeProperties(tag);
		powered = world.isReceivingRedstonePower(pos);
		tag.putBoolean("powered", powered);
		return tag;
	}

	//@Override
	protected void readProperties(CompoundTag tag) {
		//super.readProperties(tag);
		if (tag.contains("soundName"))
			soundName = prevSoundName = tag.getString("soundName");
		if (tag.contains("range"))
			range = tag.getInt("range");
		if (tag.contains("powered"))
			updatePowered(tag.getBoolean("powered"));
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		readProperties(tag);
	}

	//@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
		//tag = super.writeProperties(tag);
		tag.putString("soundName", soundName);
		tag.putInt("range", range);
		return tag;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		return writeProperties(super.toTag(tag));
	}

	@Override
	public void markRemoved() {
		if (world.isClient && sound != null)
			sound.stopAlarm();
		super.markRemoved();
	}

	@Override
	public void tick() {
		if (world.isClient) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate;
			checkStatus();
		}
	}

	protected void checkStatus() {
		if (sound == null)
			sound = new BlockEntitySound();
		if (powered && !sound.isPlaying())
			sound.playAlarm(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SOUND_PREFIX + soundName, range);
		if (!powered && sound.isPlaying())
			sound.stopAlarm();
	}

	private void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		world.updateListeners(pos, iblockstate, iblockstate, 2);
	}
}