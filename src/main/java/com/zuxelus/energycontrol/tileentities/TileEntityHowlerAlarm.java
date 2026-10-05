package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.utils.TileEntitySound;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.util.Tickable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityHowlerAlarm extends BlockEntityFacing implements Tickable, ITilePacketHandler {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final String SOUND_PREFIX = "energycontrol:alarm-";

	public int range;
	public boolean powered;

	public String soundName;
	private String prevSoundName;

	protected int updateTicker;
	protected int tickRate;
	private TileEntitySound sound;

	public TileEntityHowlerAlarm(BlockEntityType<?> type) {
		super(type);
		tickRate = 20;
		updateTicker = 0;
		powered = false;
		soundName = prevSoundName = DEFAULT_SOUND_NAME;
		range = ConfigHandler.howlerAlarmRange;
	}

	public TileEntityHowlerAlarm() {
		this(ModTileEntityTypes.howler_alarm);
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
	public void onServerMessageReceived(NbtCompound tag) {
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
	public void onClientMessageReceived(NbtCompound tag) { }

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(pos, 127, toInitialChunkDataNbt()); // unused vanilla type id, handled by ClientPlayNetworkHandlerMixin
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound tag = super.toInitialChunkDataNbt();
		tag = writeProperties(tag);
		powered = world.isReceivingRedstonePower(pos);
		tag.putBoolean("powered", powered);
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
		if (tag.contains("soundName"))
			soundName = prevSoundName = tag.getString("soundName");
		if (tag.contains("range"))
			range = tag.getInt("range");
		if (tag.contains("powered"))
			updatePowered(tag.getBoolean("powered"));
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		readProperties(tag);
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
		tag = super.writeProperties(tag);
		tag.putString("soundName", soundName);
		tag.putInt("range", range);
		return tag;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		return writeProperties(super.writeNbt(tag));
	}

	@Override
	public void markRemoved() {
		if (world.isClient && sound != null)
			sound.stopAlarm();
		super.markRemoved();
	}

	@Override
	public void tick() {
		if (world.isClient)
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
			sound.playAlarm(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SOUND_PREFIX + soundName, range);
			updateTicker = tickRate;
		}
	}
}