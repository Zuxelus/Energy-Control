package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.utils.TileEntitySound;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityHowlerAlarm extends BlockEntityFacing implements ITilePacketHandler {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final String SOUND_PREFIX = "energycontrol:alarm-";

	public int range;
	public boolean powered;

	public String soundName;
	private String prevSoundName;

	protected int updateTicker;
	protected int tickRate;
	private TileEntitySound sound;

	public TileEntityHowlerAlarm(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		tickRate = 20;
		updateTicker = 0;
		powered = false;
		soundName = prevSoundName = DEFAULT_SOUND_NAME;
		range = ConfigHandler.howlerAlarmRange;
	}

	public TileEntityHowlerAlarm(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.howler_alarm, pos, state);
	}

	public int getRange() {
		return range;
	}

	public void setRange(int r) {
		if (!level.isClientSide() && range != r)
			notifyBlockUpdate();
		range = r;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String name) {
		soundName = name;
		if (!level.isClientSide() && !prevSoundName.equals(soundName))
			notifyBlockUpdate();
		if (level.isClientSide()) {
			if (EnergyControl.INSTANCE.availableAlarms != null && !EnergyControl.INSTANCE.availableAlarms.contains(soundName)) {
				EnergyControl.LOGGER.info(String.format("Can't set sound '%s' at %d,%d,%d, using default", soundName, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
				soundName = DEFAULT_SOUND_NAME;
			}
		}
		prevSoundName = soundName;
	}

	public boolean getPowered() {
		return powered;
	}

	public void updatePowered(boolean isPowered) {
		if (level.isClientSide() && isPowered != powered) {
			powered = isPowered;
			checkStatus();
		}
	}

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getIntOr("type", 0)) {
		case 1:
			if (tag.contains("string"))
				setSoundName(tag.getStringOr("string", ""));
			break;
		case 2:
			if (tag.contains("value"))
				setRange(tag.getIntOr("value", 0));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(ClientboundBlockEntityDataPacket pkt) {
		readProperties(pkt.getTag(), level.registryAccess());
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = writeProperties(registries);
		powered = level.hasNeighborSignal(worldPosition);
		tag.putBoolean("powered", powered);
		return tag;
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		if (tag.contains("soundName"))
			soundName = prevSoundName = tag.getStringOr("soundName", "");
		if (tag.contains("range"))
			range = tag.getIntOr("range", 0);
		if (tag.contains("powered"))
			updatePowered(tag.getBooleanOr("powered", false));
	}

	@Override
	protected void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		readProperties(tag);
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putString("soundName", soundName);
		tag.putInt("range", range);
	}

	@Override
	protected void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);
		writeProperties(tag);
	}

	@Override
	public void setRemoved() {
		if (level.isClientSide() && sound != null)
			sound.stopAlarm();
		super.setRemoved();
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityHowlerAlarm))
			return;
		TileEntityHowlerAlarm te = (TileEntityHowlerAlarm) be;
		te.tick();
	}

	protected void tick() {
		if (level.isClientSide())
			checkStatus();
	}

	protected void checkStatus() {
		if (sound == null)
			sound = new TileEntitySound();
		if (!sound.isPlaying())
			updateTicker--;
		if (!powered && sound.isPlaying()) {
			sound.stopAlarm();
			updateTicker = tickRate;
		}
		if (powered && !sound.isPlaying() && updateTicker < 0) {
			sound.playAlarm(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, SOUND_PREFIX + soundName, range);
			updateTicker = tickRate;
		}
	}
}