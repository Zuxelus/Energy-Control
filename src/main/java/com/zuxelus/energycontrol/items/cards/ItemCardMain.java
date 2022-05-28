package com.zuxelus.energycontrol.items.cards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.EnergyControlConfig;
import com.zuxelus.energycontrol.ServerTickHandler;
import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemUpgrade;

import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.function.Supplier;

public final class ItemCardMain extends Item implements IItemCard, ITouchAction, IHasBars {
	private static final Map<Integer, ItemCardBase> CARDS = new HashMap<>();
	public static final int LOCATION_RANGE = 8;

	public ItemCardMain() {
		super();
		setMaxStackSize(1);
		setHasSubtypes(true);
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
	}

	public static boolean isCard(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IItemCard;
	}

	public void registerCards() {
		register(ItemCardEnergy::new);
		register(ItemCardCounter::new);
		register(ItemCardLiquid::new);
		register(ItemCardLiquidAdvanced::new);
		register(ItemCardText::new);
		register(ItemCardTime::new);
		register(ItemCardEnergyArray::new);
		register(ItemCardLiquidArray::new);
		register(ItemCardToggle::new);
		register(ItemCardVanilla::new);
		register(ItemCardInventory::new);
		register(ItemCardRedstone::new);
	}

	public static void register(Supplier<ItemCardBase> factory) {
		ItemCardBase item = factory.get();
		if (checkCard(item))
			CARDS.put(item.getDamage(), item);
	}

	private static boolean checkCard(ItemCardBase item) {
		if (!CARDS.containsKey(item.getDamage()))
			return true;
		if (item.getDamage() <= ItemCardType.CARD_MAX)
			EnergyControl.logger.warn(String.format("Card %s was not registered. ID %d is already used for standard card.", item.getUnlocalizedName(), item.getDamage()));
		else
			EnergyControl.logger.warn(String.format("Card %s was not registered. ID %d is already used for extended card.", item.getUnlocalizedName(), item.getDamage()));
		return false;
	}

	public static boolean containsCard(int i) {
		return CARDS.containsKey(i);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (CARDS.containsKey(damage))
			return CARDS.get(damage).getUnlocalizedName();
		return "";
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab))
			return;
		for (Map.Entry<Integer, ItemCardBase> entry : CARDS.entrySet()) {
			Integer key = entry.getKey();
			items.add(new ItemStack(this, 1, key));
		}
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		ItemCardReader reader = new ItemCardReader(stack);
		String title = reader.getTitle();
		if (title != null && !title.isEmpty())
			tooltip.add(title);
		switch (stack.getItemDamage()) {
		case ItemCardType.CARD_TEXT:
		case ItemCardType.CARD_TIME:
			return;
		case ItemCardType.CARD_ENERGY_ARRAY:
		case ItemCardType.CARD_LIQUID_ARRAY:
		case ItemCardType.CARD_GENERATOR_ARRAY:
			tooltip.add(I18n.format("msg.ec.cards", reader.getCardCount()));
			return;
		case ItemCardType.CARD_TOGGLE:
			tooltip.add(I18n.format("msg.ec.touch_card"));
		}

		BlockPos target = reader.getTarget();
		if (target != null)
			tooltip.add(String.format("x: %d, y: %d, z: %d", target.getX(), target.getY(), target.getZ()));
	}

	public static List<PanelString> getStringData(int settings, ItemCardReader reader, boolean isServer, boolean showLabels) {
		if (CARDS.containsKey(reader.getCardType())) {
			return CARDS.get(reader.getCardType()).getStringData(settings, reader, isServer, showLabels);
		}
		return null;
	}

	public static CardState updateCardNBT(ItemStack stack, World world, BlockPos pos, ICardReader reader, ItemStack upgradeStack) {
		int upgradeCountRange = 0;
		if (upgradeStack != ItemStack.EMPTY && upgradeStack.getItem() instanceof ItemUpgrade && upgradeStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
			upgradeCountRange = upgradeStack.getCount();

		boolean needUpdate = true;

		int range = LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7));

		CardState state = CardState.INVALID_CARD;
		IItemCard card = ((IItemCard) stack.getItem());
		if (!EnergyControlConfig.disableRangeCheck && card.isRemoteCard(stack)) {
			BlockPos target = reader.getTarget();
			if (target != null) {
				int dx = target.getX() - pos.getX();
				int dy = target.getY() - pos.getY();
				int dz = target.getZ() - pos.getZ();
				if (Math.abs(dx) > range || Math.abs(dy) > range || Math.abs(dz) > range) {
					needUpdate = false;
					state = CardState.OUT_OF_RANGE;
				}
			} else
				needUpdate = false;
		}

		if (needUpdate)
			state = card.update(world, reader, range, pos);
		reader.setState(state);
		return state;
	}
	
	public static Optional<ItemCardBase> getCardById(int id) {
		return Optional.ofNullable(CARDS.get(id));
	}

	// IItemCard
	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		return getCardById(reader.getCardType())
			.map(card -> card.update(world, reader, range, pos))
			.orElse(null);
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		return getCardById(reader.getCardType())
			.map(card -> card.getStringData(settings, reader, isServer, showLabels))
			.orElseGet(Collections::emptyList);
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		return getCardById(stack.getItemDamage())
			.map(ItemCardBase::getSettingsList)
			.orElse(null);
	}

	@Override
	public boolean isRemoteCard(ItemStack stack) {
		return getCardById(stack.getItemDamage())
			.map(ItemCardBase::isRemoteCard)
			.orElse(false);
	}

	// ITouchAction
	@Override
	public boolean enableTouch(ItemStack stack) {
		return getCardById(stack.getItemDamage())
			.map(card -> card instanceof ITouchAction)
			.orElse(false);
	}

	@Override
	public boolean runTouchAction(World world, ICardReader reader, ItemStack stack) {
		return getCardById(reader.getCardType())
			.filter(card -> card instanceof ITouchAction)
			.map(card -> ((ITouchAction) card).runTouchAction(world, reader, stack))
			.orElse(false);
	}

	@Override
	public void renderImage(TextureManager manager, ICardReader reader) {
		getCardById(reader.getCardType())
			.filter(card -> card instanceof ITouchAction)
			.ifPresent(card -> ((ITouchAction) card).renderImage(manager, reader));
	}

	// IHasBars
	@Override
	public boolean enableBars(ItemStack stack) {
		return getCardById(stack.getItemDamage())
			.map(card -> card instanceof IHasBars)
			.orElse(false);
	}

	@Override
	public void renderBars(TextureManager manager, double displayWidth, double displayHeight, ICardReader reader) {
		getCardById(reader.getCardType())
			.filter(card -> card instanceof IHasBars)
			.ifPresent(card -> ((IHasBars) card).renderBars(manager, displayWidth, displayHeight, reader));
	}

	public static void registerModels() {
		for (Map.Entry<Integer, ItemCardBase> entry : CARDS.entrySet()) {
			Integer key = entry.getKey();
			if (key <= ItemCardType.CARD_MAX)
				ModItems.registerItemModel(ModItems.itemCard, key, CARDS.get(key).getName());
		}
	}

	public static void registerExtendedModels() {
		for (Map.Entry<Integer, ItemCardBase> entry : CARDS.entrySet()) {
			Integer key = entry.getKey();
			if (key > ItemCardType.CARD_MAX)
				ModItems.registerExternalItemModel(ModItems.itemCard, key, CARDS.get(key).getName());
		}
	}

	public static Set<Integer> getCardIds() {
		return CARDS.keySet();
	}

	public static void sendCardToWS(List<PanelString> list, ICardReader reader) {
		if (EnergyControlConfig.wsHost.isEmpty())
			return;
		String id = reader.getId();
		JsonObject json = new JsonObject();
		json.addProperty("id", id);
		JsonArray array = new JsonArray();
		for (PanelString panelString : list) {
			JsonObject line = new JsonObject();
			if (panelString.textLeft != null) {
				line.addProperty("left", panelString.textLeft);
				line.addProperty("left_color", panelString.colorLeft);
			}
			if (panelString.textCenter != null) {
				line.addProperty("center", panelString.textCenter);
				line.addProperty("center_color", panelString.colorCenter);
			}
			if (panelString.textRight != null) {
				line.addProperty("right", panelString.textRight);
				line.addProperty("right_color", panelString.colorRight);
			}
			array.add(line);
		}
		json.add("lines", array);
		ServerTickHandler.instance.cards.put(id, json);
	}
}
