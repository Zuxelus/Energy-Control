package com.zuxelus.energycontrol.tileentities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.*;
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TileEntityInfoPanel extends TileEntityInventory implements ExtendedScreenHandlerFactory, ITilePacketHandler, IScreenPart, ISlotItemFilter {
	public static final String NAME = "info_panel";
	public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE - 1024;
	private static final int[] COLORS_HEX = { 0x000000, 0xe93535, 0x82e306, 0x702b14, 0x1f3ce7, 0x8f1fea, 0x1fd7e9,
			0xcbcbcb, 0x222222, 0xe60675, 0x1fe723, 0xe9cc1f, 0x06aee4, 0xb006e3, 0xe7761f, 0xffffff };

	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_COLOR = 2;
	private static final byte SLOT_UPGRADE_TOUCH = 3;

	private final Map<Integer, List<PanelString>> cardData;
	protected final Map<Integer, Map<String, Integer>> displaySettings;
	protected Screen screen;
	public NbtCompound screenData;
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
		tickRate = ConfigHandler.screenRefreshPeriod;
		updateTicker = tickRate - 1;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	public TileEntityInfoPanel(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.info_panel, pos, state);
	}

	private void initData() {
		init = true;
		if (world.isClient)
			return;

		if (screenData == null) {
			EnergyControl.INSTANCE.screenManager.registerInfoPanel(this);
		} else {
			screen = EnergyControl.INSTANCE.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
		notifyBlockUpdate();
	}

	@Override
	public void setFacing(int meta) {
		Direction newFacing = Direction.byId(meta);
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
		if (!world.isClient && showLabels != newShowLabels)
			notifyBlockUpdate();
		showLabels = newShowLabels;
	}

	public int getTickRate() {
		return tickRate;
	}

	public void setTickRate(int newValue) {
		if (!world.isClient && tickRate != newValue)
			notifyBlockUpdate();
		tickRate = newValue;
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

	public void setScreenData(NbtCompound nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && world.isClient)
			screen.destroy(true, world);
		if (screenData != null) {
			screen = EnergyControl.INSTANCE.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
	}

	@Override
	public void onClientMessageReceived(NbtCompound tag) { }

	@Override
	public void onServerMessageReceived(NbtCompound tag) {
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
				ItemStack itemStack = getStack(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain) {
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
					resetCardData();
				}
			}
		case 5:
			if (tag.contains("value"))
				setTickRate(tag.getInt("value"));
			break;
		}
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound tag = super.toInitialChunkDataNbt();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		colored = isColoredEval();
		tag.putBoolean("colored", colored);
		return tag;
	}

	protected void deserializeDisplaySettings(NbtCompound tag) {
		deserializeSlotSettings(tag, "dSettings", SLOT_CARD);
	}

	protected void deserializeSlotSettings(NbtCompound tag, String tagName, int slot) {
		if (!(tag.contains(tagName)))
			return;
		NbtList settingsList = tag.getList(tagName, NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < settingsList.size(); i++) {
			NbtCompound compound = settingsList.getCompound(i);
			try {
				getDisplaySettingsForSlot(slot).put(compound.getString("key"), compound.getInt("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.LOGGER.warn("Invalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
		if (tag.contains("tickRate"))
			tickRate = tag.getInt("tickRate");
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
				setScreenData((NbtCompound) tag.get("screenData"));
			else
				screenData = (NbtCompound) tag.get("screenData");
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
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		readProperties(tag);
	}

	protected void serializeDisplaySettings(NbtCompound tag) {
		tag.put("dSettings", serializeSlotSettings(SLOT_CARD));
	}

	protected NbtList serializeSlotSettings(int slot) {
		NbtList settingsList = new NbtList();
		for (Map.Entry<String, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			NbtCompound tag = new NbtCompound();
			tag.putString("key", item.getKey());
			tag.putInt("value", item.getValue());
			settingsList.add(tag);
		}
		return settingsList;
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
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
	protected void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		writeProperties(tag);
	}

	@Override
	public void markRemoved() {
		if (!world.isClient)
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
		super.markRemoved();
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
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
		if (!world.isClient) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate - 1;
			if (hasCards())
				markDirty();
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

	public List<PanelString> getCardData(World world, int settings, ItemStack cardStack, ItemCardReader reader, boolean isServer, boolean showLabels) {
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
	public DefaultedList<ItemStack> getCards() {
		DefaultedList<ItemStack> data = DefaultedList.of();
		data.add(getStack(SLOT_CARD));
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
				data = getCardData(world, settings, card, reader, isServer, showLabels);
			if (data == null)
				continue;
			joinedData.addAll(data);
			anyCardFound = true;
		}
		if (anyCardFound)
			return joinedData;
		return null;
	}

	public List<String> getPanelStringList() {
		List<PanelString> joinedData = getPanelStringList(true, false);
		List<String> list = DefaultedList.of();
		if (joinedData == null || joinedData.size() == 0)
			return list;

		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				list.add(panelString.textLeft);
			if (panelString.textCenter != null)
				list.add(panelString.textCenter);
			if (panelString.textRight != null)
				list.add(panelString.textRight);
		}
		return list;
	}

	public int getCardSlot(ItemStack card) {
		if (card.isEmpty())
			return 0;

		int slot = 0;
		for (int i = 0; i < size(); i++) {
			ItemStack stack = getStack(i);
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
			((ItemCardMain) card.getItem()).updateCardNBT(world, pos, reader, stack);
			reader.updateClient(card, this, slot);
		}
	}

	public boolean isColoredEval() {
		ItemStack stack = getStack(SLOT_UPGRADE_COLOR);
		return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_color);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!world.isClient) {
			setColored(isColoredEval());
			if (powered) {
				ItemStack itemStack = getStack(getSlotUpgradeRange());
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
		ItemStack card = getStack(slot);
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
				if (card.getTranslationKey().equals(entry.getKey()))
					return entry.getValue();
			}
		}

		return DISPLAY_DEFAULT;
	}

	public void setDisplaySettings(int slot, int settings) {
		if (!isCardSlot(slot))
			return;
		ItemStack stack = getStack(slot);
		if (!ItemCardMain.isCard(stack))
			return;

		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<>());
		displaySettings.get(slot).put(stack.getTranslationKey(), settings);
		if (!world.isClient)
			notifyBlockUpdate();
	}

	// ------- Inventory ------- 
	@Override
	public int size() {
		return 4;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem().equals(ModItems.upgrade_range);
		case SLOT_UPGRADE_COLOR:
			return stack.getItem().equals(ModItems.upgrade_color);
		case SLOT_UPGRADE_TOUCH:
			return stack.getItem().equals(ModItems.upgrade_touch);
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

	public void updateExtenders(World world, Boolean active) {
		if (screen == null)
			return;

		for (int x = screen.minX; x <= screen.maxX; x++)
			for (int y = screen.minY; y <= screen.maxY; y++)
				for (int z = screen.minZ; z <= screen.maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = world.getBlockState(pos);
					if (state.getBlock() instanceof InfoPanelExtender)
						world.setBlockState(pos, state.with(FacingBlockActive.ACTIVE, active), 2);
				}
	}

	/*@Override
	@Environment(EnvType.CLIENT)
	public AABB getRenderBoundingBox() {
		if (screen == null)
			return new AABB(pos.offset(0, 0, 0), pos.offset(1, 1, 1));
		return new AABB(new BlockPos(screen.minX, screen.minY, screen.minZ), new BlockPos(screen.maxX + 1, screen.maxY + 1, screen.maxZ + 1));
	}*/

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

	public boolean runTouchAction(ItemStack stack, BlockPos pos, Vec3d hit) {
		if (world.isClient)
			return false;
		ItemStack card = getStack(SLOT_CARD);
		runTouchAction(this, card, stack, SLOT_CARD, true);
		return true;
	}

	public boolean isTouchCard() {
		return isTouchCard(getStack(SLOT_CARD));
	}

	public boolean isTouchCard(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && item instanceof ITouchAction && ((ITouchAction) item).enableTouch();
	}

	public boolean hasBars() {
		return hasBars(getStack(SLOT_CARD));
	}

	public boolean hasBars(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && item instanceof IHasBars && ((IHasBars) item).enableBars(stack) && (getDisplaySettingsForCardInSlot(SLOT_CARD) & 1024) > 0;
	}

	public void renderImage(float displayWidth, float displayHeight, MatrixStack matrixStack) {
		ItemStack stack = getStack(SLOT_CARD);
		Item card = stack.getItem();
		if (isTouchCard())
			((ITouchAction) card).renderImage(new ItemCardReader(stack), matrixStack);
		if (hasBars())
			((IHasBars) card).renderBars(displayWidth, displayHeight, new ItemCardReader(stack), matrixStack);
	}

	protected void runTouchAction(TileEntityInfoPanel panel, ItemStack cardStack, ItemStack stack, int slot, boolean needsTouchUpgrade) {
		if (isTouchCard(cardStack) && (!needsTouchUpgrade || !getStack(SLOT_UPGRADE_TOUCH).isEmpty())) {
			ICardReader reader = new ItemCardReader(cardStack);
			if (((ITouchAction) cardStack.getItem()).runTouchAction(panel.getWorld(), reader, stack))
				reader.updateClient(cardStack, panel, slot);
		}
	}

	// NamedScreenHandlerFactory
	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerInfoPanel(windowId, inventory, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.info_panel.getTranslationKey());
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}
}
