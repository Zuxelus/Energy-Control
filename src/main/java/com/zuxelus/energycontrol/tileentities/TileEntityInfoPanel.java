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
import com.zuxelus.energycontrol.renderers.RotationOffset;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.client.model.data.ModelData;

public class TileEntityInfoPanel extends TileEntityInventory implements MenuProvider, ITilePacketHandler, IScreenPart, ISlotItemFilter {
	public static final String NAME = "info_panel";
	public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE - 1024;
	public static final int GREEN = -16724992; // 00CC00
	public static final int BLACK = 0xFF000000;

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
	protected boolean powered;
	private boolean unloaded;

	public TileEntityInfoPanel(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		cardData = new HashMap<>();
		displaySettings = new HashMap<>(1);
		displaySettings.put(0, new HashMap<>());
		tickRate = ConfigHandler.SCREEN_REFRESH_PERIOD.get();
		updateTicker = tickRate - 1;
		dataTicker = 4;
		showLabels = true;
		colorBackground = GREEN;
		colorText = BLACK;
		colored = false;
	}

	public TileEntityInfoPanel(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.info_panel.get(), pos, state);
	}

	private void initData() {
		init = true;
		if (level.isClientSide())
			return;

		if (screenData == null) {
			EnergyControl.screenManager.registerInfoPanel(this);
		} else {
			screen = EnergyControl.screenManager.loadScreen(this);
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
			EnergyControl.screenManager.unregisterScreenPart(this);
			EnergyControl.screenManager.registerInfoPanel(this);
		}
	}

	public boolean getShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean newShowLabels) {
		if (level != null && !level.isClientSide() && showLabels != newShowLabels)
			notifyBlockUpdate();
		showLabels = newShowLabels;
	}

	public int getTickRate() {
		return tickRate;
	}

	public void setTickRate(int newValue) {
		if (level != null && !level.isClientSide() && tickRate != newValue)
			notifyBlockUpdate();
		tickRate = newValue;
	}

	public boolean getColored() {
		return colored;
	}

	public void setColored(boolean newColored) {
		if (level != null && !level.isClientSide() && colored != newColored)
			notifyBlockUpdate();
		boolean changed = colored != newColored;
		colored = newColored;
		if (changed)
			refreshScreenModel();
	}

	public int getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(int c) {
		if (level != null && !level.isClientSide() && colorBackground != c)
			notifyBlockUpdate();
		boolean changed = colorBackground != c;
		colorBackground = c;
		if (changed)
			refreshScreenModel();
	}

	public int getColorText() {
		return colorText;
	}

	public void setColorText(int c) {
		if (level != null && !level.isClientSide() && colorText != c)
			notifyBlockUpdate();
		colorText = c;
	}

	public boolean getPowered() {
		return powered;
	}

	public void setPowered(boolean value) {
		powered = value;
	}

	protected void calcPowered() { // server
		setPowered(level.hasNeighborSignal(worldPosition));
	}

	public void setScreenData(CompoundTag nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && level.isClientSide())
			screen.destroy(true, level);
		if (screenData != null) {
			screen = EnergyControl.screenManager.loadScreen(this);
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
				if (ItemCardMain.isCard(itemStack)) {
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
	protected void writeUpdateData(CompoundTag tag, HolderLookup.Provider registries) {
		calcPowered();
		tag.putBoolean("powered", powered);
		colored = isColoredEval();
		tag.putBoolean("colored", colored);
	}

	protected void deserializeDisplaySettings(CompoundTag tag) {
		deserializeSlotSettings(tag, "dSettings", SLOT_CARD);
	}

	protected void deserializeSlotSettings(CompoundTag tag, String tagName, int slot) {
		ListTag list = tag.getList(tagName, Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag compound = list.getCompound(i);
			try {
				getDisplaySettingsForSlot(slot).put(compound.getString("key"), compound.getInt("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.LOGGER.warn("Invalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(CompoundTag tag, HolderLookup.Provider registries) {
		super.readProperties(tag, registries);
		tickRate = (tag.contains("tickRate") ? tag.getInt("tickRate") : tickRate);
		showLabels = (tag.contains("showLabels") ? tag.getBoolean("showLabels") : showLabels);
		colorText = (tag.contains("colorText") ? tag.getInt("colorText") : colorText);
		colorBackground = (tag.contains("colorBackground") ? tag.getInt("colorBackground") : colorBackground);
		setColored((tag.contains("colored") ? tag.getBoolean("colored") : colored));
		CompoundTag newScreenData = tag.contains("screenData", Tag.TAG_COMPOUND) ? tag.getCompound("screenData") : null;
		if (newScreenData != null) {
			if (level != null)
				setScreenData(newScreenData);
			else
				screenData = newScreenData;
		} else
			screenData = null;
		deserializeDisplaySettings(tag);
		if (level != null && level.isClientSide()) {
			boolean newPowered = (tag.contains("powered") ? tag.getBoolean("powered") : powered);
			if (newPowered != powered) {
				setPowered(newPowered); // update power on client
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			}
		}
		refreshScreenModel();
	}

	// the panel body is a baked model (PanelModel), it reads this when the chunk mesh is built
	@Override
	public ModelData getModelData() {
		return ModelData.of(PanelRenderData.PROPERTY, new PanelRenderData(findTexture(), getColored() ? colorBackground : getDefaultBackground(), getPowered(), getRotation(), getRenderOffset()));
	}

	protected int getDefaultBackground() {
		return GREEN;
	}

	protected RotationOffset getRenderOffset() {
		return null;
	}

	// rebuilds the meshes of all blocks of the screen, e.g. after a color or power change
	protected void refreshScreenModel() {
		if (level == null || !level.isClientSide())
			return;
		if (screen == null) {
			refreshModel(level, worldPosition);
			return;
		}
		for (int x = screen.minX; x <= screen.maxX; x++)
			for (int y = screen.minY; y <= screen.maxY; y++)
				for (int z = screen.minZ; z <= screen.maxZ; z++)
					refreshModel(level, new BlockPos(x, y, z));
	}

	public static void refreshModel(Level level, BlockPos pos) {
		if (level == null || !level.isClientSide())
			return;
		BlockEntity be = level.getBlockEntity(pos);
		if (be != null)
			be.requestModelDataUpdate(); // forge only
		BlockState state = level.getBlockState(pos);
		level.sendBlockUpdated(pos, state, state, 8);
	}

	protected void serializeDisplaySettings(CompoundTag tag) {
		serializeSlotSettings(tag, "dSettings", SLOT_CARD);
	}

	protected void serializeSlotSettings(CompoundTag tag, String tagName, int slot) {
		ListTag settingsList = new ListTag();
		for (Map.Entry<String, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			CompoundTag child = new CompoundTag();
			child.putString("key", item.getKey());
			child.putInt("value", item.getValue());
			settingsList.add(child);
		}
		tag.put(tagName, settingsList);
	}

	@Override
	protected void writeProperties(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeProperties(tag, registries);
		tag.putInt("tickRate",tickRate);
		tag.putBoolean("showLabels", getShowLabels());
		tag.putInt("colorBackground", colorBackground);
		tag.putInt("colorText", colorText);
		serializeDisplaySettings(tag);

		if (screen != null) {
			screenData = screen.toTag();
			tag.put("screenData", screenData);
		}
	}

	// setRemoved() is called both when the block is broken or replaced and on chunk unload, only the latter is announced
	@Override
	public void onChunkUnloaded() {
		unloaded = true;
		super.onChunkUnloaded();
	}

	@Override
	public void setRemoved() {
		if (!level.isClientSide()) {
			// on chunk unload only forget the screen: changing blocks or reading neighbours here loads chunks again and stalls saving
			if (!unloaded)
				EnergyControl.screenManager.unregisterScreenPart(this);
			else
				EnergyControl.screenManager.unloadScreenPart(this);
		} else if (!level.getBlockState(worldPosition).is(getBlockState().getBlock())) {
			// the core was broken or replaced: the other blocks of the screen were baked with its thickness, rotation and colors
			refreshScreenModel();
		}
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
		if (!level.isClientSide()) {
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

	public List<PanelString> getCardData(Level level, int settings, ItemStack cardStack, ItemCardReader reader, boolean isServer, boolean showLabels) {
		int slot = getCardSlot(cardStack);
		List<PanelString> data = cardData.get(slot);
		if (data == null) {
			data = reader.getStringData(level, settings, isServer, showLabels);
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

	/*public List<String> getPanelStringList(boolean isRaw) {
		List<PanelString> joinedData = getPanelStringList(true, false);
		List<String> list = NonNullList.create();
		if (joinedData == null || joinedData.isEmpty())
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
	}*/

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
			ItemCardMain.updateCardNBT((IItemCard) card.getItem(), level, worldPosition, reader, stack);
			if (hasWebUpgrade())
				ItemCardMain.sendCardToWS(getPanelStringList(true, getShowLabels()), reader);
			reader.updateClient(card, this, slot);
		}
	}

	// shares the slot with the touch screen upgrade
	public boolean hasWebUpgrade() {
		return getItem(SLOT_UPGRADE_TOUCH).getItem().equals(ModItems.upgrade_web.get());
	}

	public boolean isColoredEval() {
		ItemStack stack = getItem(SLOT_UPGRADE_COLOR);
		return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_color.get());
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (!level.isClientSide()) {
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
		if (card.isEmpty())
			return 0;
		return getDisplaySettingsByCard(card);
	}

	public int getDisplaySettingsByCard(ItemStack card) {
		int slot = getCardSlot(card);
		if (card.isEmpty())
			return 0;

		if (displaySettings.containsKey(slot)) {
			for (Map.Entry<String, Integer> entry : displaySettings.get(slot).entrySet()) {
				if (card.getItem().getDescriptionId().equals(entry.getKey()))
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
		displaySettings.get(slot).put(stack.getItem().getDescriptionId(), settings);
		if (!level.isClientSide())
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
		return switch (index) {
		case SLOT_CARD -> ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE -> stack.getItem().equals(ModItems.upgrade_range.get());
		case SLOT_UPGRADE_COLOR -> stack.getItem().equals(ModItems.upgrade_color.get());
		case SLOT_UPGRADE_TOUCH -> stack.getItem().equals(ModItems.upgrade_touch.get()) || stack.getItem().equals(ModItems.upgrade_web.get());
		default -> false;
		};
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
		refreshModel(level, worldPosition);
	}

	@Override
	public Screen getScreen() {
		return screen;
	}

	@Override
	public void updateData() { // server
		if (level.isClientSide())
			return;

		if (screen == null) {
			screenData = null;
		} else
			screenData = screen.toTag();
		notifyBlockUpdate();
	}

	public void updateExtenders(Level level, Boolean active) { // server
		// the block just switched on or off, so update the power now instead of waiting for the next update packet
		setPowered(active);
		if (screen == null)
			return;

		for (int x = screen.minX; x <= screen.maxX; x++)
			for (int y = screen.minY; y <= screen.maxY; y++)
				for (int z = screen.minZ; z <= screen.maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					if (state.getBlock() instanceof InfoPanelExtender || state.getBlock() instanceof HoloPanelExtender)
						level.setBlock(pos, state.setValue(FacingBlockActive.ACTIVE, active), 2);
				}
	}

	public AABB getRenderBoundingBox() { // forge only
		if (screen == null)
			return new AABB(worldPosition);
		return new AABB(screen.minX, screen.minY, screen.minZ, screen.maxX + 1, screen.maxY + 1, screen.maxZ + 1);
	}

	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getBlockPos();
			switch (getFacing()) {
			case UP:
				switch (getRotation()) {
				case NORTH:
					return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
				case SOUTH:
					return 2 * boolToInt(pos.getX() == scr.minX) + 1 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
				case WEST:
					return 8 * boolToInt(pos.getX() == scr.minX) + 4 * boolToInt(pos.getX() == scr.maxX) + 2 * boolToInt(pos.getZ() == scr.minZ) + 1 * boolToInt(pos.getZ() == scr.maxZ);
				case EAST:
					return 4 * boolToInt(pos.getX() == scr.minX) + 8 * boolToInt(pos.getX() == scr.maxX) + 1 * boolToInt(pos.getZ() == scr.minZ) + 2 * boolToInt(pos.getZ() == scr.maxZ);
				default:
					break;
				}
				break;
			case DOWN:
				switch (getRotation()) {
				case NORTH:
					return 2 * boolToInt(pos.getX() == scr.minX) + 1 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
				case SOUTH:
					return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
				case WEST:
					return 8 * boolToInt(pos.getX() == scr.minX) + 4 * boolToInt(pos.getX() == scr.maxX) + 1 * boolToInt(pos.getZ() == scr.minZ) + 2 * boolToInt(pos.getZ() == scr.maxZ);
				case EAST:
					return 4 * boolToInt(pos.getX() == scr.minX) + 8 * boolToInt(pos.getX() == scr.maxX) + 2 * boolToInt(pos.getZ() == scr.minZ) + 1 * boolToInt(pos.getZ() == scr.maxZ);
				default:
					break;
				}
				break;
			case NORTH:
				return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case SOUTH:
				return 2 * boolToInt(pos.getX() == scr.minX) + 1 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case WEST:
				return 2 * boolToInt(pos.getZ() == scr.minZ) + 1 * boolToInt(pos.getZ() == scr.maxZ) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case EAST:
				return 1 * boolToInt(pos.getZ() == scr.minZ) + 2 * boolToInt(pos.getZ() == scr.maxZ) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			}
		}
		return 15;
	}

	private int boolToInt(boolean b) {
		return b ? 1 : 0;
	}

	public boolean runTouchAction(ItemStack stack, BlockPos pos, Vec3 hit) {
		if (level.isClientSide())
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
		// no bar while the target is missing: the card still holds the last values it read
		return !stack.isEmpty() && item instanceof IHasBars && ((IHasBars) item).enableBars(stack) && (getDisplaySettingsForCardInSlot(SLOT_CARD) & 1024) > 0
				&& new ItemCardReader(stack).getState() == CardState.OK;
	}

	public void renderImage(float displayWidth, float displayHeight, PoseStack matrixStack, MultiBufferSource buffer) {
		ItemStack stack = getItem(SLOT_CARD);
		Item card = stack.getItem();
		if (isTouchCard())
			((ITouchAction) card).renderImage(new ItemCardReader(stack), matrixStack, buffer);
		if (hasBars())
			((IHasBars) card).renderBars(displayWidth, displayHeight, new ItemCardReader(stack), matrixStack, buffer);
	}

	protected void runTouchAction(TileEntityInfoPanel panel, ItemStack cardStack, ItemStack stack, int slot, boolean needsTouchUpgrade) {
		if (isTouchCard(cardStack) && (!needsTouchUpgrade || getItem(SLOT_UPGRADE_TOUCH).getItem().equals(ModItems.upgrade_touch.get()))) {
			ICardReader reader = new ItemCardReader(cardStack);
			if (((ITouchAction) cardStack.getItem()).runTouchAction(panel.getLevel(), reader, stack))
				reader.updateClient(cardStack, panel, slot);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerInfoPanel(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.info_panel.get().getDescriptionId());
	}
}
