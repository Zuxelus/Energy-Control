package com.zuxelus.energycontrol.tileentities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.EnergyControlConfig;
import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.blocks.HoloPanelExtender;
import com.zuxelus.energycontrol.blocks.InfoPanelExtender;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.utils.StringUtils;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityInfoPanel extends TileEntityInventory implements ITickable, ITilePacketHandler, IScreenPart, ISlotItemFilter {
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
	public NBTTagCompound screenData;
	public boolean init;
	protected int updateTicker;
	protected int dataTicker;
	protected int tickRate;

	public boolean showLabels;
	public int colorBackground;
	public int colorText;

	protected boolean colored;
	public boolean powered;

	public TileEntityInfoPanel() {
		super("tile." + NAME + ".name");
		cardData = new HashMap<>();
		displaySettings = new HashMap<>(1);
		displaySettings.put(0, new HashMap<>());
		tickRate = EnergyControlConfig.infoPanelRefreshPeriod;
		updateTicker = tickRate - 1;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	private void initData() {
		init = true;
		if (world.isRemote)
			return;

		updateBlockState(world.getBlockState(pos));
		if (screenData == null) {
			EnergyControl.instance.screenManager.registerInfoPanel(this);
		} else {
			screen = EnergyControl.instance.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
		notifyBlockUpdate();
	}

	@Override
	public void setFacing(int meta) {
		EnumFacing newFacing = EnumFacing.getFront(meta);
		if (facing == newFacing)
			return;
		facing = newFacing;
		if (init) {
			EnergyControl.instance.screenManager.unregisterScreenPart(this);
			EnergyControl.instance.screenManager.registerInfoPanel(this);
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

	public void setColorText(int c) {
		if (!world.isRemote && colorText != c)
			notifyBlockUpdate();
		colorText = c;
	}

	public boolean getPowered() {
		return powered;
	}

	public void setPowered(boolean value) {
		powered = value;
	}

	public void setScreenData(NBTTagCompound nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && world.isRemote)
			screen.destroy(true, world);
		if (screenData != null) {
			screen = EnergyControl.instance.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, world);
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) { }

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("slot") && tag.hasKey("value"))
				setDisplaySettings(tag.getInteger("slot"), tag.getInteger("value"));
			break;
		case 3:
			if (tag.hasKey("value"))
				setShowLabels(tag.getInteger("value") == 1);
			break;
		case 4:
			if (tag.hasKey("slot") && tag.hasKey("title")) {
				ItemStack stack = getStackInSlot(tag.getInteger("slot"));
				if (ItemCardMain.isCard(stack)) {
					new ItemCardReader(stack).setTitle(tag.getString("title"));
					resetCardData();
				}
			}
		case 5:
			if (tag.hasKey("value"))
				setTickRate(tag.getInteger("value"));
			break;
		case 6:
			if (tag.hasKey("value"))
				setColorBackground(tag.getInteger("value"));
			break;
		case 7:
			if (tag.hasKey("value"))
				setColorText(tag.getInteger("value"));
			break;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.setBoolean("powered", powered);
		colored = isColoredEval();
		tag.setBoolean("colored", colored);
		return tag;
	}

	protected void deserializeDisplaySettings(NBTTagCompound tag) {
		deserializeSlotSettings(tag, "dSettings", SLOT_CARD);
	}

	protected void deserializeSlotSettings(NBTTagCompound tag, String tagName, int slot) {
		if (!(tag.hasKey(tagName)))
			return;
		NBTTagList settingsList = tag.getTagList(tagName, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < settingsList.tagCount(); i++) {
			NBTTagCompound compound = settingsList.getCompoundTagAt(i);
			try {
				getDisplaySettingsForSlot(slot).put(compound.getString("key"), compound.getInteger("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.logger.warn("Invalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("tickRate"))
			tickRate = tag.getInteger("tickRate");
		if (tag.hasKey("showLabels"))
			showLabels = tag.getBoolean("showLabels");
		if (tag.hasKey("colorText"))
			colorText = tag.getInteger("colorText");
		if (tag.hasKey("colorBackground"))
			colorBackground = tag.getInteger("colorBackground");
		if (tag.hasKey("colored"))
			setColored(tag.getBoolean("colored"));

		if (tag.hasKey("screenData")) {
			if (world != null)
				setScreenData((NBTTagCompound) tag.getTag("screenData"));
			else
				screenData = (NBTTagCompound) tag.getTag("screenData");
		} else
			screenData = null;
		deserializeDisplaySettings(tag);
		if (tag.hasKey("powered") && world.isRemote) {
			boolean newPowered = tag.getBoolean("powered");
			if (powered != newPowered) {
				powered = newPowered; 
				world.checkLight(pos);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	protected void serializeDisplaySettings(NBTTagCompound tag) {
		tag.setTag("dSettings", serializeSlotSettings(SLOT_CARD));
	}

	protected NBTTagList serializeSlotSettings(int slot) {
		NBTTagList settingsList = new NBTTagList();
		for (Map.Entry<String, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("key", item.getKey());
			tag.setInteger("value", item.getValue());
			settingsList.appendTag(tag);
		}
		return settingsList;
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setInteger("tickRate",tickRate);
		tag.setBoolean("showLabels", getShowLabels());
		tag.setInteger("colorBackground", colorBackground);
		tag.setInteger("colorText", colorText);
		serializeDisplaySettings(tag);

		if (screen != null) {
			screenData = screen.toTag();
			tag.setTag("screenData", screenData);
		}
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void invalidate() {
		if (!world.isRemote)
			EnergyControl.instance.screenManager.unregisterScreenPart(this);
		super.invalidate();
	}

	@Override
	public void update() {
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
	public void updateTileEntity() {
		notifyBlockUpdate();
	}

	public void resetCardData() {
		cardData.clear();
	}

	public List<PanelString> getCardData(int settings, ItemStack cardStack, ItemCardReader reader, boolean isServer, boolean showLabels) {
		int slot = getCardSlot(cardStack);
		List<PanelString> data = cardData.get(slot);
		if (data == null) {
			data = ItemCardMain.getStringData(settings, reader, isServer, showLabels);
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
				data = getCardData(settings, card, reader, isServer, showLabels);
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
		if (ItemCardMain.isCard(card)) {
			ItemCardReader reader = new ItemCardReader(card);
			ItemCardMain.updateCardNBT(card, world, pos, reader, stack);
			ItemCardMain.sendCardToWS(getPanelStringList(true, getShowLabels()), reader);
			reader.updateClient(card, this, slot);
		}
	}

	public boolean isColoredEval() {
		ItemStack stack = getStackInSlot(SLOT_UPGRADE_COLOR);
		return !stack.isEmpty() && stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_COLOR;
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

	public Map<String, Integer> getDisplaySettingsForSlot(int slot) {
		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<>());
		return displaySettings.get(slot);
	}

	public int getDisplaySettingsForCardInSlot(int slot) {
		ItemStack card = getStackInSlot(slot);
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
				if (StringUtils.getItemId(card).equals(entry.getKey()))
					return entry.getValue();
			}
		}

		return DISPLAY_DEFAULT;
	}

	public void setDisplaySettings(int slot, int settings) {
		if (!isCardSlot(slot))
			return;
		ItemStack stack = getStackInSlot(slot);
		if (!ItemCardMain.isCard(stack))
			return;

		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<>());
		displaySettings.get(slot).put(StringUtils.getItemId(stack), settings);
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
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
		case SLOT_UPGRADE_COLOR:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_COLOR;
		case SLOT_UPGRADE_TOUCH:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_TOUCH;
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

	public void updateBlockState(IBlockState state) {
		boolean flag = world.isBlockPowered(pos);
		powered = flag;
		if (flag == state.getValue(FacingBlockActive.ACTIVE))
			return;

		if (flag)
			world.scheduleUpdate(pos, state.getBlock(), 4);
		else {
			world.setBlockState(pos, state.cycleProperty(FacingBlockActive.ACTIVE), 2);
			updateExtenders(true);
		}
	}

	public void updateExtenders(Boolean active) {
		if (screen == null)
			return;

		for (int x = screen.minX; x <= screen.maxX; x++)
			for (int y = screen.minY; y <= screen.maxY; y++)
				for (int z = screen.minZ; z <= screen.maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					IBlockState state = world.getBlockState(pos);
					if (state.getBlock() instanceof InfoPanelExtender || state.getBlock() instanceof HoloPanelExtender)
						world.setBlockState(pos, state.withProperty(FacingBlockActive.ACTIVE, active), 2);
				}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (screen == null)
			return new AxisAlignedBB(pos.add(0, 0, 0), pos.add(1, 1, 1));
		return new AxisAlignedBB(new BlockPos(screen.minX, screen.minY, screen.minZ), new BlockPos(screen.maxX + 1, screen.maxY + 1, screen.maxZ + 1));
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@SideOnly(Side.CLIENT)
	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getPos();
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

	public boolean runTouchAction(ItemStack stack, BlockPos pos, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return false;
		ItemStack card = getStackInSlot(SLOT_CARD);
		runTouchAction(this, card, stack, SLOT_CARD, true);
		return true;
	}

	public boolean isTouchCard() {
		return isTouchCard(getStackInSlot(SLOT_CARD));
	}

	public boolean isTouchCard(ItemStack stack) {
		Item item = stack.getItem(); // raise error in 1.10 and less
		return !stack.isEmpty() && item instanceof ITouchAction && ((ITouchAction) item).enableTouch(stack);
	}

	public boolean hasBars() {
		return hasBars(getStackInSlot(SLOT_CARD));
	}

	public boolean hasBars(ItemStack stack) {
		Item item = stack.getItem(); // raise error in 1.10 and less
		return !stack.isEmpty() && item instanceof IHasBars && ((IHasBars) item).enableBars(stack) && (getDisplaySettingsForCardInSlot(SLOT_CARD) & 1024) > 0;
	}

	public void renderImage(TextureManager manager, double displayWidth, double displayHeight) {
		ItemStack stack = getStackInSlot(SLOT_CARD);
		Item card = stack.getItem();
		if (isTouchCard())
			((ITouchAction) card).renderImage(manager, new ItemCardReader(stack));
		if (hasBars())
			((IHasBars) card).renderBars(manager, displayWidth, displayHeight, new ItemCardReader(stack));
	}

	protected void runTouchAction(TileEntityInfoPanel panel, ItemStack cardStack, ItemStack stack, int slot, boolean needsTouchUpgrade) {
		if (isTouchCard(cardStack) && (!needsTouchUpgrade || !getStackInSlot(SLOT_UPGRADE_TOUCH).isEmpty())) {
			ICardReader reader = new ItemCardReader(cardStack);
			if (((ITouchAction) cardStack.getItem()).runTouchAction(panel.getWorld(), reader, stack))
				reader.updateClient(cardStack, panel, slot);
		}
	}
}
