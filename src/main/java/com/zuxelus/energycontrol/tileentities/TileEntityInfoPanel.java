package com.zuxelus.energycontrol.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ISlotItemFilter;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityInfoPanel extends TileEntityInventory
		implements ITickable, ITilePacketHandler, IScreenPart, ISlotItemFilter {
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
		cardData = new HashMap<Integer, List<PanelString>>();
		displaySettings = new HashMap<Integer, Map<Integer, Integer>>(1);
		displaySettings.put(0, new HashMap<Integer, Integer>());
		tickRate = EnergyControl.config.screenRefreshPeriod - 1;
		updateTicker = tickRate;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	private void initData() {
		init = true;
		if (worldObj.isRemote)
			return;

		if (screenData == null) {
			EnergyControl.instance.screenManager.registerInfoPanel(this);
		} else {
			screen = EnergyControl.instance.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, worldObj);
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
		if (!worldObj.isRemote && showLabels != newShowLabels)
			notifyBlockUpdate();
		showLabels = newShowLabels;
	}

	public boolean getColored() {
		return colored;
	}

	public void setColored(boolean newColored) {
		if (!worldObj.isRemote && colored != newColored)
			notifyBlockUpdate();
		colored = newColored;
	}

	public int getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(int c) {
		if (!worldObj.isRemote && colorBackground != c)
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
		if (!worldObj.isRemote && colorText != c)
			notifyBlockUpdate();
		colorText = c;
	}

	public boolean getPowered() {
		return powered;
	}

	protected void calcPowered() { // server
		boolean newPowered = worldObj.isBlockPowered(pos);
		if (newPowered != powered) {
			powered = newPowered;
			if (screen != null)
				screen.turnPower(powered, worldObj);
		}
	}

	public void setScreenData(NBTTagCompound nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && worldObj.isRemote)
			screen.destroy(true, worldObj);
		if (screenData != null) {
			screen = EnergyControl.instance.screenManager.loadScreen(this);
			if (screen != null)
				screen.init(true, worldObj);
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
		case 2:
			if (tag.hasKey("value")) {
				int value = tag.getInteger("value");
				setColorBackground(value >> 4);
				setColorText(value & 0xf);
			}
			break;
		case 3:
			if (tag.hasKey("value"))
				setShowLabels(tag.getInteger("value") == 1);
			break;
		case 4:
			if (tag.hasKey("slot") && tag.hasKey("title")) {
				ItemStack itemStack = getStackInSlot(tag.getInteger("slot"));
				if (itemStack != null && itemStack.getItem() instanceof ItemCardMain) {
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
					resetCardData();
				}
			}
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		calcPowered();
		tag.setBoolean("powered", powered);
		colored = isColoredEval();
		tag.setBoolean("colored", colored);
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
		calcPowered();
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
				getDisplaySettingsForSlot(slot).put(compound.getInteger("key"), compound.getInteger("value"));
			} catch (IllegalArgumentException e) {
				EnergyControl.logger.warn("Ivalid display settings for Information Panel");
			}
		}
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("showLabels"))
			showLabels = tag.getBoolean("showLabels");

		if (tag.hasKey("colorBackground")) {
			colorText = tag.getInteger("colorText");
			colorBackground = tag.getInteger("colorBackground");
		}

		if (tag.hasKey("colored"))
			setColored(tag.getBoolean("colored"));

		if (tag.hasKey("screenData")) {
			if (worldObj != null)
				setScreenData((NBTTagCompound) tag.getTag("screenData"));
			else
				screenData = (NBTTagCompound) tag.getTag("screenData");
		} else
			screenData = null;
		deserializeDisplaySettings(tag);
		if (tag.hasKey("powered") && worldObj.isRemote) {
			boolean newPowered = tag.getBoolean("powered");
			if (powered != newPowered) {
				powered = newPowered; 
				worldObj.checkLight(pos);
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
		for (Map.Entry<Integer, Integer> item : getDisplaySettingsForSlot(slot).entrySet()) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("key", item.getKey());
			compound.setInteger("value", item.getValue());
			settingsList.appendTag(compound);
		}
		return settingsList;
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
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
		if (!worldObj.isRemote)
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
		if (!worldObj.isRemote) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate;
			markDirty();
		}
	}

	@Override
	public void notifyBlockUpdate() {
		IBlockState iblockstate = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	public void resetCardData() {
		cardData.clear();
	}

	public List<PanelString> getCardData(int settings, ItemStack cardStack, ItemCardReader reader, boolean showLabels) {
		int slot = getCardSlot(cardStack);
		List<PanelString> data = cardData.get(slot);
		if (data == null) {
			data = ItemCardMain.getStringData(settings, reader, showLabels);
			cardData.put(slot, data);
		}
		return data;
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	// ------- Settings --------
	public List<ItemStack> getCards() {
		List<ItemStack> data = new ArrayList<ItemStack>(1);
		data.add(getStackInSlot(SLOT_CARD));
		return data;
	}

	public List<PanelString> getPanelStringList(boolean showLabels) {
		List<ItemStack> cards = getCards();
		boolean anyCardFound = false;
		List<PanelString> joinedData = new LinkedList<PanelString>();
		for (ItemStack card : cards) {
			if (card == null)
				continue;
			int settings = getDisplaySettingsByCard(card);
			if (settings == 0)
				continue;
			ItemCardReader reader = new ItemCardReader(card);
			CardState state = reader.getState();
			List<PanelString> data;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				data = reader.getStateMessage(state);
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
		if (card == null)
			return 0;

		int slot = 0;
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.equals(card)) {
				slot = i;
				break;
			}
		}
		return slot;
	}

	private void processCard(ItemStack card, int slot, ItemStack stack) {
		if (card == null)
			return;

		Item item = card.getItem();
		if (!(item instanceof ItemCardMain))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		ItemCardMain.updateCardNBT(worldObj, pos, reader, stack);
		reader.updateClient(this, slot);
	}

	protected boolean isColoredEval() {
		ItemStack itemStack = getStackInSlot(SLOT_UPGRADE_COLOR);
		return itemStack != null && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_COLOR;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!worldObj.isRemote && powered) {
			setColored(isColoredEval());
			ItemStack itemStack = getStackInSlot(getSlotUpgradeRange());
			for (ItemStack card : getCards())
				processCard(card, getCardSlot(card), itemStack);
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
		if (card == null) {
			return 0;
		}
		return getDisplaySettingsByCard(card);
	}

	public int getDisplaySettingsByCard(ItemStack card) {
		int slot = getCardSlot(card);
		if (card == null)
			return 0;

		if (!displaySettings.containsKey(slot))
			return DISPLAY_DEFAULT;

		if (displaySettings.get(slot).containsKey(card.getItemDamage()))
			return displaySettings.get(slot).get(card.getItemDamage());

		return DISPLAY_DEFAULT;
	}

	public void setDisplaySettings(int slot, int settings) {
		if (!isCardSlot(slot))
			return;
		ItemStack stack = getStackInSlot(slot);
		if (stack == null)
			return;
		if (!(stack.getItem() instanceof ItemCardMain))
			return;

		int cardType = stack.getItemDamage();
		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<Integer, Integer>());
		displaySettings.get(slot).put(cardType, settings);
		if (!worldObj.isRemote)
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
		if (worldObj.isRemote)
			return;

		if (screen == null) {
			screenData = null;
		} else
			screenData = screen.toTag();
		notifyBlockUpdate();
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

	public boolean runTouchAction(BlockPos pos, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote)
			return false;
		ItemStack stack = getStackInSlot(SLOT_CARD);
		if (getStackInSlot(SLOT_UPGRADE_TOUCH) == null || stack == null || stack.getItemDamage() != ItemCardType.CARD_TOGGLE)
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
		ItemCardMain.runTouchAction(worldObj, stack);
		return true;
	}

	public boolean isTouchCard() {
		ItemStack stack = getStackInSlot(SLOT_CARD);
		return stack != null && stack.getItemDamage() == ItemCardType.CARD_TOGGLE;
	}

	public void renderImage(TextureManager manager) {
		ItemStack stack = getStackInSlot(SLOT_CARD);
		if (stack != null && stack.getItemDamage() == ItemCardType.CARD_TOGGLE)
			ItemCardMain.renderImage(manager, stack);
	}
}
