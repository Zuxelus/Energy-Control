package com.zuxelus.energycontrol.tileentities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.blocks.HoloPanelExtender;
import com.zuxelus.energycontrol.blocks.InfoPanelExtender;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.ContainerInfoPanel;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityInfoPanel extends TileEntityInventory implements MenuProvider, ITilePacketHandler, IScreenPart, ISlotItemFilter {
	public static final String NAME = "info_panel";
	public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE - 1024;

	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_COLOR = 2;
	private static final byte SLOT_UPGRADE_TOUCH = 3;

	private final Map<Integer, List<PanelString>> cardData;
	protected final Map<Integer, Map<String, Integer>> displaySettings;
	protected Screen screen;
	public CompoundTag screenData;
	public boolean init;
	protected int updateTicker;
	protected int dataTicker;
	protected int tickRate;

	public boolean showLabels;
	public int colorBackground;
	public int colorText;

	protected boolean colored;
	public boolean powered;

	public TileEntityInfoPanel(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		cardData = new HashMap<>();
		displaySettings = new HashMap<>(1);
		displaySettings.put(0, new HashMap<>());
		tickRate = ConfigHandler.SCREEN_REFRESH_PERIOD.get();
		updateTicker = tickRate - 1;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	public TileEntityInfoPanel(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.info_panel.get(), pos, state);
	}

	private void initData() {
		init = true;
		if (level.isClientSide)
			return;

		if (screenData == null) {
			EnergyControl.INSTANCE.screenManager.registerInfoPanel(this);
		} else {
			screen = EnergyControl.INSTANCE.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, level);
		}
		notifyBlockUpdate();
	}

	@Override
	public void setFacing(int meta) {
		Direction newFacing = Direction.from3DDataValue(meta);
		if (facing == newFacing)
			return;
		facing = newFacing;
		if (init) {
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
			EnergyControl.INSTANCE.screenManager.registerInfoPanel(this);
		}
	}

	public boolean getShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean newShowLabels) {
		if (!level.isClientSide && showLabels != newShowLabels)
			notifyBlockUpdate();
		showLabels = newShowLabels;
	}

	public int getTickRate() {
		return tickRate;
	}

	public void setTickRate(int newValue) {
		if (!level.isClientSide && tickRate != newValue)
			notifyBlockUpdate();
		tickRate = newValue;
	}

	public boolean getColored() {
		return colored;
	}

	public void setColored(boolean newColored) {
		if (!level.isClientSide && colored != newColored)
			notifyBlockUpdate();
		colored = newColored;
	}

	public int getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(int c) {
		if (!level.isClientSide && colorBackground != c)
			notifyBlockUpdate();
		colorBackground = c;
	}

	public int getColorText() {
		return colorText;
	}

	public void setColorText(int c) {
		if (!level.isClientSide && colorText != c)
			notifyBlockUpdate();
		colorText = c;
	}

	public boolean getPowered() {
		return powered;
	}

	protected void calcPowered() { // server
		boolean newPowered = level.hasNeighborSignal(worldPosition);
		if (newPowered != powered) {
			powered = newPowered;
			if (screen != null)
				screen.turnPower(powered, level);
		}
	}

	public void setScreenData(CompoundTag nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && level.isClientSide)
			screen.destroy(true, level);
		if (screenData != null) {
			screen = EnergyControl.INSTANCE.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, level);
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("slot") && tag.contains("value"))
				setDisplaySettings(tag.getInt("slot"), tag.getInt("value"));
			break;
		case 3:
			if (tag.contains("value"))
				setShowLabels(tag.getInt("value") == 1);
			break;
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getItem(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain) {
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
					resetCardData();
				}
			}
		case 5:
			if (tag.contains("value"))
				setTickRate(tag.getInt("value"));
			break;
		case 6:
			if (tag.contains("value"))
				setColorBackground(tag.getInt("value"));
			break;
		case 7:
			if (tag.contains("value"))
				setColorText(tag.getInt("value"));
			break;
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		colored = isColoredEval();
		tag.putBoolean("colored", colored);
		return tag;
	}

	protected void deserializeDisplaySettings(CompoundTag tag) {
		deserializeSlotSettings(tag, "dSettings", SLOT_CARD);
	}

	protected void deserializeSlotSettings(CompoundTag tag, String tagName, int slot) {
		if (!(tag.contains(tagName)))
			return;
		ListTag settingsList = tag.getList(tagName, Tag.TAG_COMPOUND);
		for (int i = 0; i < settingsList.size(); i++) {
			CompoundTag compound = settingsList.getCompound(i);
			try {
				getDisplaySettingsForSlot(slot).put(compound.getString("key"), compound.getInt("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.LOGGER.warn("Invalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(CompoundTag tag) {
		super.readProperties(tag);
		if (tag.contains("tickRate"))
			tickRate = tag.getInt("tickRate");
		if (tag.contains("showLabels"))
			showLabels = tag.getBoolean("showLabels");
		if (tag.contains("colorText"))
			colorText = tag.getInt("colorText");
		if (tag.contains("colorBackground"))
			colorBackground = tag.getInt("colorBackground");
		if (tag.contains("colored"))
			setColored(tag.getBoolean("colored"));

		if (tag.contains("screenData")) {
			if (level != null)
				setScreenData((CompoundTag) tag.get("screenData"));
			else
				screenData = (CompoundTag) tag.get("screenData");
		} else
			screenData = null;
		deserializeDisplaySettings(tag);
		if (tag.contains("powered") && level.isClientSide) {
			boolean newPowered = tag.getBoolean("powered");
			if (powered != newPowered) {
				powered = newPowered; 
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			}
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		readProperties(tag);
	}

	protected void serializeDisplaySettings(CompoundTag tag) {
		tag.put("dSettings", serializeSlotSettings(SLOT_CARD));
	}

	protected ListTag serializeSlotSettings(int slot) {
		ListTag settingsList = new ListTag();
		for (Map.Entry<String, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString("key", item.getKey());
			tag.putInt("value", item.getValue());
			settingsList.add(tag);
		}
		return settingsList;
	}

	@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
		tag = super.writeProperties(tag);
		tag.putInt("tickRate",tickRate);
		tag.putBoolean("showLabels", getShowLabels());
		tag.putInt("colorBackground", colorBackground);
		tag.putInt("colorText", colorText);
		serializeDisplaySettings(tag);

		if (screen != null) {
			screenData = screen.toTag();
			tag.put("screenData", screenData);
		}
		return tag;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeProperties(tag);
	}

	@Override
	public void setRemoved() {
		if (!level.isClientSide)
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
		super.setRemoved();
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityInfoPanel))
			return;
		TileEntityInfoPanel te = (TileEntityInfoPanel) be;
		te.tick();
	}

	protected void tick() {
		if (!init)
			initData();
		if (!powered)
			return;
		dataTicker--;
		if (dataTicker <= 0) {
			resetCardData();
			dataTicker = 4;
		}
		if (!level.isClientSide) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate - 1;
			if (hasCards())
				setChanged();
		}
	}

	private boolean hasCards() {
		for (ItemStack card : getCards())
			if (!card.isEmpty())
				return true;
		return false;
	}

	@Override
	public void updateTileEntity() {
		notifyBlockUpdate();
	}

	public void resetCardData() {
		cardData.clear();
	}

	public List<PanelString> getCardData(Level world, int settings, ItemStack cardStack, ItemCardReader reader, boolean isServer, boolean showLabels) {
		int slot = getCardSlot(cardStack);
		List<PanelString> data = cardData.get(slot);
		if (data == null) {
			data = reader.getStringData(world, settings, isServer, showLabels);
			cardData.put(slot, data);
		}
		return data;
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	// ------- Settings --------
	public NonNullList<ItemStack> getCards() {
		NonNullList<ItemStack> data = NonNullList.create();
		data.add(getItem(SLOT_CARD));
		return data;
	}

	public List<PanelString> getPanelStringList(boolean isServer, boolean showLabels) {
		List<ItemStack> cards = getCards();
		boolean anyCardFound = false;
		List<PanelString> joinedData = new LinkedList<>();
		for (ItemStack card : cards) {
			if (card.isEmpty())
				continue;
			int settings = getDisplaySettingsByCard(card);
			if (settings == 0)
				continue;
			ItemCardReader reader = new ItemCardReader(card);
			CardState state = reader.getState();
			List<PanelString> data;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				data = ItemCardReader.getStateMessage(state);
			else
				data = getCardData(level, settings, card, reader, isServer, showLabels);
			if (data == null)
				continue;
			joinedData.addAll(data);
			anyCardFound = true;
		}
		if (anyCardFound)
			return joinedData;
		return null;
	}

	public List<String> getPanelStringList(boolean isRaw) {
		List<PanelString> joinedData = getPanelStringList(true, false);
		List<String> list = NonNullList.create();
		if (joinedData == null || joinedData.size() == 0)
			return list;

		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				list.add(formatString(panelString.textLeft, isRaw));
			if (panelString.textCenter != null)
				list.add(formatString(panelString.textCenter, isRaw));
			if (panelString.textRight != null)
				list.add(formatString(panelString.textRight, isRaw));
		}
		return list;
	}

	private String formatString(String text, boolean isRaw) {
		return isRaw ? text : text.replaceAll("\\u00a7[1-9,a-f]", "");
	}

	public int getCardSlot(ItemStack card) {
		if (card.isEmpty())
			return 0;

		int slot = 0;
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
			if (!stack.isEmpty() && stack.equals(card)) {
				slot = i;
				break;
			}
		}
		return slot;
	}

	private void processCard(ItemStack card, int slot, ItemStack stack) {
		if (ItemCardMain.isCard(card)) {
			ItemCardReader reader = new ItemCardReader(card);
			((ItemCardMain) card.getItem()).updateCardNBT(level, worldPosition, reader, stack);
			ItemCardMain.sendCardToWS(getPanelStringList(true, getShowLabels()), reader);
			reader.updateClient(card, this, slot);
		}
	}

	public boolean isColoredEval() {
		ItemStack stack = getItem(SLOT_UPGRADE_COLOR);
		return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_color.get());
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (!level.isClientSide) {
			setColored(isColoredEval());
			if (powered) {
				ItemStack itemStack = getItem(getSlotUpgradeRange());
				for (ItemStack card : getCards())
					processCard(card, getCardSlot(card), itemStack);
			}
		}
	}

	public byte getSlotUpgradeRange() {
		return SLOT_UPGRADE_RANGE;
	}

	public boolean isCardSlot(int slot) {
		return slot == SLOT_CARD;
	}

	public Map<String, Integer> getDisplaySettingsForSlot(int slot) {
		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<>());
		return displaySettings.get(slot);
	}

	public int getDisplaySettingsForCardInSlot(int slot) {
		ItemStack card = getItem(slot);
		if (card.isEmpty()) {
			return 0;
		}
		return getDisplaySettingsByCard(card);
	}

	public int getDisplaySettingsByCard(ItemStack card) {
		int slot = getCardSlot(card);
		if (card.isEmpty())
			return 0;

		if (displaySettings.containsKey(slot)) {
			for (Map.Entry<String, Integer> entry : displaySettings.get(slot).entrySet()) {
				if (card.getDescriptionId().equals(entry.getKey()))
					return entry.getValue();
			}
		}

		return DISPLAY_DEFAULT;
	}

	public void setDisplaySettings(int slot, int settings) {
		if (!isCardSlot(slot))
			return;
		ItemStack stack = getItem(slot);
		if (!ItemCardMain.isCard(stack))
			return;

		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<>());
		displaySettings.get(slot).put(stack.getDescriptionId(), settings);
		if (!level.isClientSide)
			notifyBlockUpdate();
	}

	// ------- Inventory ------- 
	@Override
	public int getContainerSize() {
		return 4;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem().equals(ModItems.upgrade_range.get());
		case SLOT_UPGRADE_COLOR:
			return stack.getItem().equals(ModItems.upgrade_color.get());
		case SLOT_UPGRADE_TOUCH:
			return stack.getItem().equals(ModItems.upgrade_touch.get());
		default:
			return false;
		}
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}

	@Override
	public void updateData() {
		if (level.isClientSide)
			return;

		if (screen == null) {
			screenData = null;
		} else
			screenData = screen.toTag();
		notifyBlockUpdate();
	}

	public void updateExtenders(Level world, Boolean active) {
		if (screen == null)
			return;

		for (int x = screen.minX; x <= screen.maxX; x++)
			for (int y = screen.minY; y <= screen.maxY; y++)
				for (int z = screen.minZ; z <= screen.maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = world.getBlockState(pos);
					if (state.getBlock() instanceof InfoPanelExtender || state.getBlock() instanceof HoloPanelExtender)
						world.setBlock(pos, state.setValue(FacingBlockActive.ACTIVE, active), 2);
				}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		if (screen == null)
			return new AABB(worldPosition.offset(0, 0, 0), worldPosition.offset(1, 1, 1));
		return new AABB(new BlockPos(screen.minX, screen.minY, screen.minZ), new BlockPos(screen.maxX + 1, screen.maxY + 1, screen.maxZ + 1));
	}

	@OnlyIn(Dist.CLIENT)
	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getBlockPos();
			switch (getFacing()) {
			case SOUTH:
				return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getY() == scr.minY) + 8 * boolToInt(pos.getY() == scr.maxY);
			case WEST:
				return 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ) + boolToInt(pos.getY() == scr.minY) + 2 * boolToInt(pos.getY() == scr.maxY);
			case EAST:
				return 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ) + 2 * boolToInt(pos.getY() == scr.minY) + boolToInt(pos.getY() == scr.maxY);
			case NORTH:
				return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case UP:
				return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
			case DOWN:
				return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
			}
		}
		return 15;
	}

	private int boolToInt(boolean b) {
		return b ? 1 : 0;
	}

	public boolean runTouchAction(ItemStack stack, BlockPos pos, Vec3 hit) {
		if (level.isClientSide)
			return false;
		ItemStack card = getItem(SLOT_CARD);
		runTouchAction(this, card, stack, SLOT_CARD, true);
		return true;
	}

	public boolean isTouchCard() {
		return isTouchCard(getItem(SLOT_CARD));
	}

	public boolean isTouchCard(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && item instanceof ITouchAction && ((ITouchAction) item).enableTouch();
	}

	public boolean hasBars() {
		return hasBars(getItem(SLOT_CARD));
	}

	public boolean hasBars(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && item instanceof IHasBars && ((IHasBars) item).enableBars(stack) && (getDisplaySettingsForCardInSlot(SLOT_CARD) & 1024) > 0;
	}

	public void renderImage(float displayWidth, float displayHeight, PoseStack matrixStack) {
		ItemStack stack = getItem(SLOT_CARD);
		Item card = stack.getItem();
		if (isTouchCard())
			((ITouchAction) card).renderImage(new ItemCardReader(stack), matrixStack);
		if (hasBars())
			((IHasBars) card).renderBars(displayWidth, displayHeight, new ItemCardReader(stack), matrixStack);
	}

	protected void runTouchAction(TileEntityInfoPanel panel, ItemStack cardStack, ItemStack stack, int slot, boolean needsTouchUpgrade) {
		if (isTouchCard(cardStack) && (!needsTouchUpgrade || !getItem(SLOT_UPGRADE_TOUCH).isEmpty())) {
			ICardReader reader = new ItemCardReader(cardStack);
			if (((ITouchAction) cardStack.getItem()).runTouchAction(panel.getLevel(), reader, stack))
				reader.updateClient(cardStack, panel, slot);
		}
	}

	// MenuProvider
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerInfoPanel(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.info_panel.get().getDescriptionId());
	}
}
