package com.zuxelus.energycontrol.tileentities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.ContainerInfoPanel;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardToggle;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

public class TileEntityInfoPanel extends TileEntityInventory implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler, IScreenPart, ISlotItemFilter {
	public static final String NAME = "info_panel";
	public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE;
	private static final int[] COLORS_HEX = { 0x000000, 0xe93535, 0x82e306, 0x702b14, 0x1f3ce7, 0x8f1fea, 0x1fd7e9,
			0xcbcbcb, 0x222222, 0xe60675, 0x1fe723, 0xe9cc1f, 0x06aee4, 0xb006e3, 0xe7761f, 0xffffff };

	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_COLOR = 2;
	private static final byte SLOT_UPGRADE_TOUCH = 3;

	private final Map<Integer, List<PanelString>> cardData;
	protected final Map<Integer, Map<Integer, Integer>> displaySettings;
	protected Screen screen;
	public CompoundNBT screenData;
	public boolean init;
	protected int updateTicker;
	protected int dataTicker;
	protected int tickRate;

	public boolean showLabels;
	public int colorBackground;
	public int colorText;

	protected boolean colored;
	public boolean powered;

	public TileEntityInfoPanel(TileEntityType<?> type) {
		super(type);
		cardData = new HashMap<Integer, List<PanelString>>();
		displaySettings = new HashMap<Integer, Map<Integer, Integer>>(1);
		displaySettings.put(0, new HashMap<Integer, Integer>());
		tickRate = ConfigHandler.SCREEN_REFRESH_PERIOD.get();
		updateTicker = tickRate;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	public TileEntityInfoPanel() {
		this(ModTileEntityTypes.info_panel.get());
	}

	private void initData() {
		init = true;
		if (world.isRemote)
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
		Direction newFacing = Direction.byIndex(meta);
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
		if (!world.isRemote && showLabels != newShowLabels)
			notifyBlockUpdate();
		showLabels = newShowLabels;
	}

	public int getTickRate() {
		return tickRate;
	}

	public void setTickRate(int newValue) {
		if (!world.isRemote && tickRate != newValue)
			notifyBlockUpdate();
		tickRate = newValue;
	}

	public boolean getColored() {
		return colored;
	}

	public void setColored(boolean newColored) {
		if (!world.isRemote && colored != newColored)
			notifyBlockUpdate();
		colored = newColored;
	}

	public int getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(int c) {
		if (!world.isRemote && colorBackground != c)
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
		if (!world.isRemote && colorText != c)
			notifyBlockUpdate();
		colorText = c;
	}

	public boolean getPowered() {
		return powered;
	}

	protected void calcPowered() { // server
		boolean newPowered = world.isBlockPowered(pos);
		if (newPowered != powered) {
			powered = newPowered;
			if (screen != null)
				screen.turnPower(powered, world);
		}
	}

	public void setScreenData(CompoundNBT nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && world.isRemote)
			screen.destroy(true, world);
		if (screenData != null) {
			screen = EnergyControl.INSTANCE.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
	}

	@Override
	public void onClientMessageReceived(CompoundNBT tag) { }

	@Override
	public void onServerMessageReceived(CompoundNBT tag) {
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
				ItemStack itemStack = getStackInSlot(tag.getInt("slot"));
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
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		colored = isColoredEval();
		tag.putBoolean("colored", colored);
		return new SUpdateTileEntityPacket(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		colored = isColoredEval();
		tag.putBoolean("colored", colored);
		return tag;
	}

	protected void deserializeDisplaySettings(CompoundNBT tag) {
		deserializeSlotSettings(tag, "dSettings", SLOT_CARD);
	}

	protected void deserializeSlotSettings(CompoundNBT tag, String tagName, int slot) {
		if (!(tag.contains(tagName)))
			return;
		ListNBT settingsList = tag.getList(tagName, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < settingsList.size(); i++) {
			CompoundNBT compound = settingsList.getCompound(i);
			try {
				getDisplaySettingsForSlot(slot).put(compound.getInt("key"), compound.getInt("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.LOGGER.warn("Ivalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
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
				setScreenData((CompoundNBT) tag.get("screenData"));
			else
				screenData = (CompoundNBT) tag.get("screenData");
		} else
			screenData = null;
		deserializeDisplaySettings(tag);
		if (tag.contains("powered") && world.isRemote) {
			boolean newPowered = tag.getBoolean("powered");
			if (powered != newPowered) {
				powered = newPowered; 
				world.getChunkProvider().getLightManager().checkBlock(pos);
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		readProperties(tag);
	}

	protected void serializeDisplaySettings(CompoundNBT tag) {
		tag.put("dSettings", serializeSlotSettings(SLOT_CARD));
	}

	protected ListNBT serializeSlotSettings(int slot) {
		ListNBT settingsList = new ListNBT();
		for (Map.Entry<Integer, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("key", item.getKey());
			tag.putInt("value", item.getValue());
			settingsList.add(tag);
		}
		return settingsList;
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
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
	public CompoundNBT write(CompoundNBT tag) {
		return writeProperties(super.write(tag));
	}

	@Override
	public void remove() {
		if (!world.isRemote)
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
		super.remove();
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
		if (!world.isRemote) {
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
	public void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
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
	public NonNullList<ItemStack> getCards() {
		NonNullList<ItemStack> data = NonNullList.create();
		data.add(getStackInSlot(SLOT_CARD));
		return data;
	}

	public List<PanelString> getPanelStringList(boolean isServer, boolean showLabels) {
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
		List<String> list = NonNullList.create();
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
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
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
		if (!(item instanceof ItemCardMain))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		((ItemCardMain) item).updateCardNBT(world, pos, reader, stack);
		reader.updateClient(card, this, slot);
	}

	public boolean isColoredEval() {
		ItemStack stack = getStackInSlot(SLOT_UPGRADE_COLOR);
		return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_color.get());
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!world.isRemote) {
			setColored(isColoredEval());
			if (powered) {
				ItemStack itemStack = getStackInSlot(getSlotUpgradeRange());
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
		ItemStack card = getStackInSlot(slot);
		if (card.isEmpty()) {
			return 0;
		}
		return getDisplaySettingsByCard(card);
	}

	public int getDisplaySettingsByCard(ItemStack card) {
		int slot = getCardSlot(card);
		if (card.isEmpty())
			return 0;

		if (!displaySettings.containsKey(slot))
			return DISPLAY_DEFAULT;

		ItemCardMain item = (ItemCardMain) card.getItem();
		if (displaySettings.get(slot).containsKey(item.getCardId(card)))
			return displaySettings.get(slot).get(item.getCardId(card));

		return DISPLAY_DEFAULT;
	}

	public void setDisplaySettings(int slot, int settings) {
		if (!isCardSlot(slot))
			return;
		ItemStack stack = getStackInSlot(slot);
		if (stack.isEmpty())
			return;
		if (!(stack.getItem() instanceof ItemCardMain))
			return;

		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<Integer, Integer>());
		displaySettings.get(slot).put(((ItemCardMain) stack.getItem()).getCardId(stack), settings);
		if (!world.isRemote)
			notifyBlockUpdate();
	}

	// ------- Inventory ------- 
	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return stack.getItem() instanceof ItemCardMain;
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
		if (world.isRemote)
			return;

		if (screen == null) {
			screenData = null;
		} else
			screenData = screen.toTag();
		notifyBlockUpdate();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (screen == null)
			return new AxisAlignedBB(pos.add(0, 0, 0), pos.add(1, 1, 1));
		return new AxisAlignedBB(new BlockPos(screen.minX, screen.minY, screen.minZ), new BlockPos(screen.maxX + 1, screen.maxY + 1, screen.maxZ + 1));
	}

	/*@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}*/

	@OnlyIn(Dist.CLIENT)
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

	public boolean runTouchAction(ItemStack stack, BlockPos pos, Vector3d hit) {
		if (world.isRemote)
			return false;
		ItemStack card = getStackInSlot(SLOT_CARD);
		if (card.isEmpty() || !(card.getItem() instanceof ITouchAction))
			return false;
		if (isTouchCard() && getStackInSlot(SLOT_UPGRADE_TOUCH).isEmpty())
			return false;
		switch (facing) { // TODO
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
		}
		((ItemCardMain) card.getItem()).runTouchAction(this, card, stack, SLOT_CARD);
		return true;
	}

	public boolean isTouchCard() {
		ItemStack stack = getStackInSlot(SLOT_CARD);
		return !stack.isEmpty() && stack.getItem() instanceof ItemCardToggle;
	}

	public void renderImage(TextureManager manager, Matrix4f matrix4f) {
		ItemStack stack = getStackInSlot(SLOT_CARD);
//		if (!stack.isEmpty() && ItemCardMain.getCardId(stack) == ItemCardType.CARD_TOGGLE)
//			ItemCardMain.renderImage(manager, stack); // TODO
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerInfoPanel(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.info_panel.get().getTranslationKey());
	}
}
