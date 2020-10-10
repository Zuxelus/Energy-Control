package com.zuxelus.energycontrol.blockentities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.InfoPanelContainer;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.UpgradeColorItem;
import com.zuxelus.energycontrol.items.UpgradeRangeItem;
import com.zuxelus.energycontrol.items.UpgradeTouchItem;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InfoPanelBlockEntity extends InventoryBlockEntity implements Tickable, ITilePacketHandler, IScreenPart, IFacingBlock {
	public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE;
	private static final int[] COLORS_HEX = { 0x000000, 0xe93535, 0x82e306, 0x702b14, 0x1f3ce7, 0x8f1fea, 0x1fd7e9,
			0xcbcbcb, 0x222222, 0xe60675, 0x1fe723, 0xe9cc1f, 0x06aee4, 0xb006e3, 0xe7761f, 0xffffff };

	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_COLOR = 2;
	private static final byte SLOT_UPGRADE_TOUCH = 3;
	
	private FacingBlockEntity facingBE = new FacingBlockEntity(true);

	private final Map<Integer, List<PanelString>> cardData;
	protected final Map<Integer, Map<Integer, Integer>> displaySettings;
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

	public InfoPanelBlockEntity() {
		super(ModItems.INFO_PANEL_BLOCK_ENTITY, "container.info_panel", 4);
		cardData = new HashMap<Integer, List<PanelString>>();
		displaySettings = new HashMap<Integer, Map<Integer, Integer>>(1);
		displaySettings.put(0, new HashMap<Integer, Integer>());
		tickRate = ConfigHandler.infoPanelRefreshPeriod - 1;
		updateTicker = tickRate;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	private void initData() {
		init = true;
		if (world.isClient)
			return;

		if (screenData == null) {
			EnergyControl.screenManager.registerInfoPanel(this);
		} else {
			screen = EnergyControl.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
		notifyBlockUpdate();
	}

	public boolean getShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean newShowLabels) {
		if (!world.isClient && showLabels != newShowLabels)
			notifyBlockUpdate();
		showLabels = newShowLabels;
	}

	public boolean getColored() {
		return colored;
	}

	public void setColored(boolean newColored) {
		if (!world.isClient && colored != newColored)
			notifyBlockUpdate();
		colored = newColored;
	}

	public int getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(int c) {
		if (!world.isClient && colorBackground != c)
			notifyBlockUpdate();
		colorBackground = c;
	}

	public int getColorText() {
		return colorText;
	}

	public int getColorTextHex() {
		return COLORS_HEX[colorText];
	}

	public void setColorText(int c) {
		if (!world.isClient && colorText != c)
			notifyBlockUpdate();
		colorText = c;
	}

	public boolean getPowered() {
		return powered;
	}

	protected void calcPowered() { // server
		boolean newPowered = world.isReceivingRedstonePower(pos);
		if (newPowered != powered) {
			powered = newPowered;
			if (screen != null)
				screen.turnPower(powered, world);
		}
	}

	public void setScreenData(CompoundTag nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && world.isClient)
			screen.destroy(true, world);
		if (screenData != null) {
			screen = EnergyControl.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
	}

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("slot") && tag.contains("value"))
				setDisplaySettings(tag.getInt("slot"), tag.getInt("value"));
			break;
		case 2:
			if (tag.contains("value")) {
				int value = tag.getInt("value");
				setColorBackground(value >> 4);
				setColorText(value & 0xf);
			}
			break;
		case 3:
			if (tag.contains("value"))
				setShowLabels(tag.getInt("value") == 1);
			break;
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getInvStack(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof MainCardItem) {
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
					resetCardData();
				}
			}
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		colored = isColoredEval();
		tag.putBoolean("colored", colored);
		return new BlockEntityUpdateS2CPacket(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getCompoundTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
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
		ListTag settingsList = tag.getList(tagName, 10);
		for (int i = 0; i < settingsList.size(); i++) {
			CompoundTag compound = settingsList.getCompound(i);
			try {
				getDisplaySettingsForSlot(slot).put(compound.getInt("key"), compound.getInt("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.LOGGER.warn("Ivalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(CompoundTag tag) {
		super.readProperties(tag);
		facingBE.readProperties(tag);
		if (tag.contains("showLabels"))
			showLabels = tag.getBoolean("showLabels");

		if (tag.contains("colorBackground")) {
			colorText = tag.getInt("colorText");
			colorBackground = tag.getInt("colorBackground");
		}

		if (tag.contains("colored"))
			setColored(tag.getBoolean("colored"));

		if (tag.contains("screenData")) {
			if (world != null)
				setScreenData((CompoundTag) tag.get("screenData"));
			else
				screenData = (CompoundTag) tag.get("screenData");
		} else
			screenData = null;
		deserializeDisplaySettings(tag);
		if (tag.contains("powered") && world.isClient) {
			boolean newPowered = tag.getBoolean("powered");
			if (powered != newPowered) {
				powered = newPowered; 
				world.getChunkManager().getLightingProvider().checkBlock(pos);
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		readProperties(tag);
	}

	protected void serializeDisplaySettings(CompoundTag tag) {
		tag.put("dSettings", serializeSlotSettings(SLOT_CARD));
	}

	protected ListTag serializeSlotSettings(int slot) {
		ListTag settingsList = new ListTag();
		for (Map.Entry<Integer, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			CompoundTag compound = new CompoundTag();
			compound.putInt("key", item.getKey());
			compound.putInt("value", item.getValue());
			settingsList.add(compound);
		}
		return settingsList;
	}

	@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
		tag = super.writeProperties(tag);
		tag = facingBE.writeProperties(tag);
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
	public CompoundTag toTag(CompoundTag tag) {
		return writeProperties(super.toTag(tag));
	}

	@Override
	public void markRemoved() {
		if (!world.isClient)
			EnergyControl.screenManager.unregisterScreenPart(this);
		super.markRemoved();
	}

	@Override
	public void tick() {
		if (!init)
			initData();
		if (!powered)
			return;
		dataTicker--;
		if (dataTicker <= 0) {
			resetCardData();
			dataTicker = 4;
		}
		if (!world.isClient) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate;
			markDirty();
		}
	}

	@Override
	public void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		world.updateListeners(pos, iblockstate, iblockstate, 2);
	}

	public void resetCardData() {
		cardData.clear();
	}

	public List<PanelString> getCardData(int settings, ItemStack cardStack, ItemCardReader reader, boolean showLabels) {
		int slot = getCardSlot(cardStack);
		List<PanelString> data = cardData.get(slot);
		if (data == null) {
			data = ((MainCardItem) cardStack.getItem()).getStringData(world, settings, reader, showLabels);
			cardData.put(slot, data);
		}
		return data;
	}

	// ------- Settings --------
	public DefaultedList<ItemStack> getCards() {
		DefaultedList<ItemStack> data = DefaultedList.of();
		data.add(getInvStack(SLOT_CARD));
		return data;
	}

	public List<PanelString> getPanelStringList(boolean showLabels) {
		List<ItemStack> cards = getCards();
		boolean anyCardFound = false;
		List<PanelString> joinedData = new LinkedList<PanelString>();
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
				data = getCardData(settings, card, reader, showLabels);
			if (data == null)
				continue;
			joinedData.addAll(data);
			anyCardFound = true;
		}
		if (anyCardFound)
			return joinedData;
		return null;
	}

	public int getCardSlot(ItemStack card) {
		if (card.isEmpty())
			return 0;

		int slot = 0;
		for (int i = 0; i < getInvSize(); i++) {
			ItemStack stack = getInvStack(i);
			if (!stack.isEmpty() && stack.equals(card)) {
				slot = i;
				break;
			}
		}
		return slot;
	}

	private void processCard(ItemStack card, int slot, ItemStack stack) {
		if (card.isEmpty())
			return;

		Item item = card.getItem();
		if (!(item instanceof MainCardItem))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		((MainCardItem) item).updateCardNBT(world, pos, reader, stack);
		reader.updateClient(this, slot);
	}

	protected boolean isColoredEval() {
		ItemStack itemStack = getInvStack(SLOT_UPGRADE_COLOR);
		return !itemStack.isEmpty() && itemStack.getItem() instanceof UpgradeColorItem;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!world.isClient) {
			setColored(isColoredEval());
			if (powered) {
				ItemStack itemStack = getInvStack(getSlotUpgradeRange());
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

	public Map<Integer, Integer> getDisplaySettingsForSlot(int slot) {
		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<Integer, Integer>());
		return displaySettings.get(slot);
	}

	public int getDisplaySettingsForCardInSlot(int slot) {
		ItemStack card = getInvStack(slot);
		if (card.isEmpty()) {
			return 0;
		}
		return getDisplaySettingsByCard(card);
	}

	public int getDisplaySettingsByCard(ItemStack card) {
		int slot = getCardSlot(card);
		if (!(card.getItem() instanceof MainCardItem))
			return 0;

		if (!displaySettings.containsKey(slot))
			return DISPLAY_DEFAULT;

		int cardType = ((MainCardItem) card.getItem()).getCardType();
		if (displaySettings.get(slot).containsKey(cardType))
			return displaySettings.get(slot).get(cardType);

		return DISPLAY_DEFAULT;
	}

	public void setDisplaySettings(int slot, int settings) {
		if (!isCardSlot(slot))
			return;
		ItemStack stack = getInvStack(slot);
		if (stack.isEmpty())
			return;
		if (!(stack.getItem() instanceof MainCardItem))
			return;

		int cardType = ((MainCardItem) stack.getItem()).getCardType();
		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<Integer, Integer>());
		displaySettings.get(slot).put(cardType, settings);
		if (!world.isClient)
			notifyBlockUpdate();
	}

	// ------- Inventory ------- 
	@Override
	protected Container createContainer(int syncId, PlayerInventory playerInventory) {
		return new InfoPanelContainer(syncId, playerInventory, this);
	}

	@Override
	public boolean canInsert(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return stack.getItem() instanceof MainCardItem;
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof UpgradeRangeItem;
		case SLOT_UPGRADE_COLOR:
			return stack.getItem() instanceof UpgradeColorItem;
		case SLOT_UPGRADE_TOUCH:
			return stack.getItem() instanceof UpgradeTouchItem;
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
		if (world.isClient)
			return;

		if (screen == null) {
			screenData = null;
		} else
			screenData = screen.toTag();
		notifyBlockUpdate();
	}
	
	// IFacingBlock
	@Override
	public Direction getFacing() {
		return facingBE.getFacing();
	}

	@Override
	public void setFacing(Direction newFacing) {
		if (facingBE.getFacing() == newFacing)
			return;
		facingBE.setFacing(newFacing);
		if (init) {
			EnergyControl.screenManager.unregisterScreenPart(this);
			EnergyControl.screenManager.registerInfoPanel(this);
		}
	}

	@Override
	public Direction getRotation() {
		return facingBE.getRotation();
	}

	@Override
	public void setRotation(Direction meta) {
		facingBE.setRotation(meta);
	}

	@Environment(EnvType.CLIENT)
	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getPos();
			switch (getFacing()) {
			case SOUTH:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getY() == scr.minY) + 8 * boolToInt(pos.getY() == scr.maxY);
			case WEST:
				return 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ) + 1 * boolToInt(pos.getY() == scr.minY) + 2 * boolToInt(pos.getY() == scr.maxY);
			case EAST:
				return 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ) + 2 * boolToInt(pos.getY() == scr.minY) + 1 * boolToInt(pos.getY() == scr.maxY);
			case NORTH:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case UP:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
			case DOWN:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
			}
		}
		return 15;
	}

	private int boolToInt(boolean b) {
		return b ? 1 : 0;
	}

	public boolean runTouchAction(BlockPos pos, BlockHitResult hit) {
		if (world.isClient)
			return false;
		ItemStack stack = getInvStack(SLOT_CARD);
		if (getInvStack(SLOT_UPGRADE_TOUCH).isEmpty() || stack.isEmpty() || !(stack.getItem() instanceof ITouchAction))
			return false;
		/*switch (facing) { // TODO
		case DOWN:
			break;
		case EAST:
			break;
		case NORTH:
			break;
		case SOUTH:
			break;
		case UP:
			break;
		case WEST:
			break;
		default:
			break;
		}*/
		((ITouchAction) stack.getItem()).runTouchAction(world, new ItemCardReader(stack));
		return true;
	}

	public boolean isTouchCard() {
		ItemStack stack = getInvStack(SLOT_CARD);
		return !stack.isEmpty() && stack.getItem() == ModItems.TOGGLE_ITEM_CARD;
	}

	@Environment(EnvType.CLIENT)
	public void renderImage(TextureManager manager, Matrix4f matrix4f) {
		ItemStack stack = getInvStack(SLOT_CARD);
		if (stack.getItem() instanceof ITouchAction)
			((ITouchAction) stack.getItem()).renderImage(manager, matrix4f, new ItemCardReader(stack));
	}
}
