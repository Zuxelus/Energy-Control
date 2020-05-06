package com.zuxelus.energycontrol.tileentities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ISlotItemFilter;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityInfoPanel extends TileEntityInventory implements ITickable, ITilePacketHandler, IScreenPart, IRedstoneConsumer, ISlotItemFilter {
	public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE;
	private static final int[] COLORS_HEX = { 0x000000, 0xe93535, 0x82e306, 0x702b14, 0x1f3ce7, 0x8f1fea, 0x1fd7e9,
			0xcbcbcb, 0x222222, 0xe60675, 0x1fe723, 0xe9cc1f, 0x06aee4, 0xb006e3, 0xe7761f, 0xffffff };

	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_COLOR = 2;

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
		super("tile.info_panel.name");
		cardData = new HashMap<Integer, List<PanelString>>();
		displaySettings = new HashMap<Integer, Map<Integer, Integer>>(1);
		displaySettings.put(0, new HashMap<Integer, Integer>());
		tickRate = EnergyControl.config.screenRefreshPeriod;
		updateTicker = tickRate;
		dataTicker = 4;
		showLabels = true;
		colorBackground = 2;
		colored = false;
	}

	private void initData() {
		init = true;
		if (world.isRemote)
			return;

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

	public void setScreenData(NBTTagCompound nbtTagCompound) {
		screenData = nbtTagCompound;
		if (screen != null && FMLCommonHandler.instance().getEffectiveSide().isClient())
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
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain) {
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
		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
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
			updateTicker = tickRate;
			markDirty();
		}
	}

	@Override
	public void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}
	
	public void resetCardData() {
		cardData.clear();
	}
	
	public List<PanelString> getCardData(int settings, ItemStack cardStack, ItemCardReader reader) {
		int slot = getCardSlot(cardStack);
		List<PanelString> data = cardData.get(slot);
		if (data == null) {
			data = ItemCardMain.getStringData(settings, reader, getShowLabels());
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
		ItemCardMain.updateCardNBT(world, pos, reader, stack);
		reader.updateClient(this, slot);
	}
	
	protected boolean isColoredEval() {
		ItemStack itemStack = getStackInSlot(SLOT_UPGRADE_COLOR);
		return !itemStack.isEmpty() && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_COLOR;
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		if (!world.isRemote) {
			setColored(isColoredEval());
			ItemStack itemStack = getStackInSlot(getSlotUpgradeRange());
			for (ItemStack card : getCards())
				processCard(card, getCardSlot(card), itemStack);
		}
	}

	protected byte getSlotUpgradeRange() {
		return SLOT_UPGRADE_RANGE;
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
		
		if (displaySettings.get(slot).containsKey(card.getItemDamage()))
			return displaySettings.get(slot).get(card.getItemDamage());
		
		return DISPLAY_DEFAULT;
	}
	
	public void setDisplaySettings(int slot, int settings) {
		if (slot != SLOT_CARD)
			return;	
		ItemStack stack = getStackInSlot(slot);
		if (stack.isEmpty())
			return;
		if (!(stack.getItem() instanceof ItemCardMain))
			return;
		
		int cardType = stack.getItemDamage();
		if (!displaySettings.containsKey(slot))
			displaySettings.put(slot, new HashMap<Integer, Integer>());
		displaySettings.get(slot).put(cardType, settings);
		if (!world.isRemote)
			notifyBlockUpdate();
	}

	// ------- Inventory ------- 
	@Override
	public int getSizeInventory() {
		return 3;
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
	public void neighborChanged() {
		if (!world.isRemote)
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
}
