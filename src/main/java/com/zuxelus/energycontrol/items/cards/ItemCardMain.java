package com.zuxelus.energycontrol.items.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.kits.ItemKitAppEng;

import ic2.api.recipe.Recipes;
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
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
	}
	
	public final void registerCards() {
		register(new ItemCardEnergy());
		register(new ItemCardCounter());
		register(new ItemCardLiquid());
		register(new ItemCardGenerator());
		register(new ItemCardGeneratorKinetic());
		register(new ItemCardGeneratorHeat());
		register(new ItemCardReactor());
		register(new ItemCardReactor5x5());
		register(new ItemCardLiquidAdvanced());
		register(new ItemCardText());
		register(new ItemCardTime());
		register(new ItemCardEnergyArray());
		register(new ItemCardLiquidArray());
		register(new ItemCardGeneratorArray());
		register(new ItemCardToggle());
		if (CrossModLoader.buildCraft.modLoaded())
			register(new ItemCardEngine());
		if (CrossModLoader.draconicEvolution.modLoaded) {
			register(new ItemCardEnergyDraconic());
			register(new ItemCardReactorDraconic());
		}
		if (Loader.isModLoaded("appliedenergistics2"))
			register(new ItemCardAppEng());
	}

	private static void register(IItemCard item) {
		if (checkCard(item))
			cards.put(item.getDamage(), item);
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
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
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

	public static List<PanelString> getStringData(int settings, ItemCardReader reader, boolean showLabels) {
		if (cards.containsKey(reader.getCardType())) {
			return cards.get(reader.getCardType()).getStringData(settings, reader, showLabels);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static List<PanelSetting> getSettingsList(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (cards.containsKey(damage))
			return cards.get(damage).getSettingsList(stack);
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

	public static ICardGui getSettingsScreen(ItemCardReader reader) {
		if (reader.getCardType() != ItemCardType.CARD_TEXT)
			return null;
		return cards.get(ItemCardType.CARD_TEXT).getSettingsScreen(reader);
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

	public static void runTouchAction(World world, ItemStack stack) {
		if (stack.getItem() instanceof ItemCardMain && cards.containsKey(stack.getItemDamage())) {
			IItemCard card = cards.get(stack.getItemDamage());
			if (card instanceof ITouchAction)
				((ITouchAction) card).runTouchAction(world, new ItemCardReader(stack));
		}
	}

	public static void renderImage(TextureManager manager, ItemStack stack) {
		if (stack.getItem() instanceof ItemCardMain && cards.containsKey(stack.getItemDamage())) {
			IItemCard card = cards.get(stack.getItemDamage());
			if (card instanceof ITouchAction)
				((ITouchAction) card).renderImage(manager, new ItemCardReader(stack));
		}
	}

	public static void registerModels() {
		for (Map.Entry<Integer, IItemCard> entry : cards.entrySet()) {
			Integer key = entry.getKey();
			if (key <= ItemCardType.CARD_MAX)
				ItemHelper.registerItemModel(ItemHelper.itemCard, key, cards.get(key).getName());
		}
	}

	public static void registerExtendedModels() {
		for (Map.Entry<Integer, IItemCard> entry : cards.entrySet()) {
			Integer key = entry.getKey();
			if (key > ItemCardType.CARD_MAX)
				ItemHelper.registerExternalItemModel(ItemHelper.itemCard, key, cards.get(key).getName());
		}
	}

	public static void registerRecipes() {
		for (Map.Entry<Integer, IItemCard> entry : cards.entrySet()) {
			Integer key = entry.getKey();
			Object[] recipe = entry.getValue().getRecipe();
			if (recipe != null)
				Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCard, 1, key), recipe);
		}
	}
}
