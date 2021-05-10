package com.zuxelus.energycontrol.items.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

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

public final class ItemCardMain extends Item {
	public static final int LOCATION_RANGE = 8;
	
	private static Map<Integer, IItemCard> cards = new HashMap<Integer, IItemCard>();

	public ItemCardMain() {
		super();
		setMaxStackSize(1);
		setHasSubtypes(true);
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
	}
	
	public final void registerCards() {
		register("ItemCardEnergy");
		register("ItemCardCounter");
		register("ItemCardLiquid");
		register("ItemCardGenerator");
		if (Loader.isModLoaded("ic2")) {
			register("ItemCardGeneratorKinetic");
			register("ItemCardGeneratorHeat");
			register("ItemCardReactor");
			register("ItemCardReactor5x5");
		}
		register("ItemCardLiquidAdvanced");
		register("ItemCardText");
		register("ItemCardTime");
		register("ItemCardEnergyArray");
		register("ItemCardLiquidArray");
		register("ItemCardGeneratorArray");
		register("ItemCardToggle");
		register("ItemCardVanilla");
		register("ItemCardInventory");
		register("ItemCardRedstone");
		if (Loader.isModLoaded("buildcraftcore"))
			register("ItemCardEngine");
		if (Loader.isModLoaded("draconicevolution"))
			register("ItemCardReactorDraconic");
		if (Loader.isModLoaded("appliedenergistics2")) {
			register("ItemCardAppEng");
			register("ItemCardAppEngInv");
		}
		if (Loader.isModLoaded("galacticraftcore") && Loader.isModLoaded("galacticraftplanets"))
			register("ItemCardGalacticraft");
		if (Loader.isModLoaded("bigreactors"))
			register("ItemCardBigReactors");
		if (Loader.isModLoaded("nuclearcraft"))
			register("ItemCardNuclearCraft");
		if (Loader.isModLoaded("mekanismgenerators"))
			register("ItemCardMekanism");
		if (Loader.isModLoaded("thermalexpansion"))
			register("ItemCardThermalExpansion");
	}

	private static void register(String className) {
		try {
			Class<?> clz = Class.forName("com.zuxelus.energycontrol.items.cards." + className);
			if (clz == null)
				return;
			IItemCard item =  (IItemCard) clz.newInstance();
			if (checkCard(item))
				cards.put(item.getDamage(), item);
		} catch (Exception e) {
			EnergyControl.logger.warn(String.format("Class %s not found", className));
		}
	}

	private static boolean checkCard(IItemCard item) {
		if (!cards.containsKey(item.getDamage()))
			return true;
		if (item.getDamage() <= ItemCardType.CARD_MAX)
			EnergyControl.logger.warn(String.format("Card %s was not registered. ID %d is already used for standard card.", item.getUnlocalizedName(), item.getDamage()));
		else
			EnergyControl.logger.warn(String.format("Card %s was not registered. ID %d is already used for extended card.", item.getUnlocalizedName(), item.getDamage()));
		return false;
	}

	public static void registerCard(IItemCard item) {
		if (checkCard(item)) {
			if (item.getDamage() <= ItemCardType.CARD_MAX) {
				EnergyControl.logger.warn(String.format("Card %s was not registered. Card ID should be bigger than %d", item.getUnlocalizedName(), ItemCardType.CARD_MAX));
				return;
			}
			cards.put(item.getDamage(), item);
		}
	}

	public static boolean containsCard(int i) {
		return cards.containsKey(i) ? true : false;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (cards.containsKey(damage))
			return cards.get(damage).getUnlocalizedName();
		return "";
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab))
			return;
		for (Map.Entry<Integer, IItemCard> entry : cards.entrySet()) {
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
		if (cards.containsKey(reader.getCardType())) {
			return cards.get(reader.getCardType()).getStringData(settings, reader, isServer, showLabels);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static List<PanelSetting> getSettingsList(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (cards.containsKey(damage))
			return cards.get(damage).getSettingsList();
		return null;
	}

	public static CardState updateCardNBT(World world, BlockPos pos, ICardReader reader, ItemStack upgradeStack) {
		int upgradeCountRange = 0;
		if (upgradeStack != ItemStack.EMPTY && upgradeStack.getItem() instanceof ItemUpgrade && upgradeStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
			upgradeCountRange = upgradeStack.getCount();

		boolean needUpdate = true;
		int range = LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7));

		CardState state = CardState.INVALID_CARD;
		if (isRemoteCard(reader.getCardType())) {
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
			state = update(world, reader, range, pos);
		reader.setState(state);
		return state;
	}

	private static CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		if (cards.containsKey(reader.getCardType()))
			return cards.get(reader.getCardType()).update(world, reader, range, pos);
		return null;
	}

	public static boolean isRemoteCard(int damage) {
		if (cards.containsKey(damage))
			return cards.get(damage).isRemoteCard();
		return false;
	}

	public static int getKitFromCard(int damage) {
		if (cards.containsKey(damage))
			return cards.get(damage).getKitFromCard();
		return -1;
	}

	public static void runTouchAction(TileEntityInfoPanel panel, ItemStack cardStack, ItemStack stack, int slot) {
		if (cardStack.getItem() instanceof ItemCardMain && cards.containsKey(cardStack.getItemDamage())) {
			IItemCard card = cards.get(cardStack.getItemDamage());
			if (card instanceof ITouchAction) {
				ICardReader reader = new ItemCardReader(cardStack);
				if (((ITouchAction) card).runTouchAction(panel.getWorld(), reader, stack))
					reader.updateClient(cardStack, panel, slot);
			}
		}
	}

	public static void renderImage(TextureManager manager, double displayWidth, double displayHeight, ItemStack stack) {
		if (stack.getItem() instanceof ItemCardMain && cards.containsKey(stack.getItemDamage())) {
			IItemCard card = cards.get(stack.getItemDamage());
			if (card instanceof ITouchAction)
				((ITouchAction) card).renderImage(manager, new ItemCardReader(stack));
			if (card instanceof IHasBars)
				((IHasBars) card).renderBars(manager, displayWidth, displayHeight, new ItemCardReader(stack));
		}
	}

	public static void registerModels() {
		for (Map.Entry<Integer, IItemCard> entry : cards.entrySet()) {
			Integer key = entry.getKey();
			if (key <= ItemCardType.CARD_MAX)
				ModItems.registerItemModel(ModItems.itemCard, key, cards.get(key).getName());
		}
	}

	public static void registerExtendedModels() {
		for (Map.Entry<Integer, IItemCard> entry : cards.entrySet()) {
			Integer key = entry.getKey();
			if (key > ItemCardType.CARD_MAX)
				ModItems.registerExternalItemModel(ModItems.itemCard, key, cards.get(key).getName());
		}
	}
}
