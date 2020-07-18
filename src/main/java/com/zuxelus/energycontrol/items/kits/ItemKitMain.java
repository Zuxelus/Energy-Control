package com.zuxelus.energycontrol.items.kits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemKitMain extends Item {
	private static Map<Integer, IItemKit> kits = new HashMap<Integer, IItemKit>();

	public ItemKitMain() {
		super();
		setMaxStackSize(16);
		setHasSubtypes(true);
		setMaxDamage(0);
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
	}

	public final void registerKits() {
		register(new ItemKitEnergy());
		register(new ItemKitCounter());
		register(new ItemKitLiquid());
		register(new ItemKitGenerator());
		if (Loader.isModLoaded("IC2"))
			register(new ItemKitReactor());
		register(new ItemKitLiquidAdvanced());
		register(new ItemKitToggle());
		if (Loader.isModLoaded("draconicevolution"))
			register(new ItemKitDraconic());
		if (Loader.isModLoaded("appliedenergistics2"))
			register(new ItemKitAppEng());
		if (Loader.isModLoaded("galacticraftcore") && Loader.isModLoaded("galacticraftplanets"))
			register(new ItemKitGalacticraft());
		if (Loader.isModLoaded("bigreactors"))
			register(new ItemKitBigReactors());
	}

	private void register(ItemKitBase item) {
		if (checkKit(item))
			kits.put(item.getDamage(), item);
	}

	private static boolean checkKit(IItemKit item) {
		if (!kits.containsKey(item.getDamage()))
			return true;
		if (item.getDamage() <= ItemCardType.KIT_MAX)
			EnergyControl.logger.warn(String.format("Kit %s was not registered. ID %d is already used for standard kit.", item.getUnlocalizedName(), item.getDamage()));
		else
			EnergyControl.logger.warn(String.format("Kit %s was not registered. ID %d is already used for extended kit.", item.getUnlocalizedName(), item.getDamage()));
		return false;
	}

	public static final void registerKit(IItemKit item) {
		if (checkKit(item)) {
			if (item.getDamage() <= ItemCardType.KIT_MAX) {
				EnergyControl.logger.warn(String.format("Kit %s was not registered. Kit ID should be bigger than %d", item.getUnlocalizedName(), ItemCardType.CARD_MAX));
				return;
			}
			kits.put(item.getDamage(), item);
		}
	}

	public static final boolean containsKit(int i) {
		return kits.containsKey(i) ? true : false;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (kits.containsKey(damage))
			return kits.get(damage).getUnlocalizedName();
		return "";
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		for (Map.Entry<Integer, IItemKit> entry : kits.entrySet()) {
			Integer key = entry.getKey();
			items.add(new ItemStack(this, 1, key));
		}
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (player == null || !(player instanceof EntityPlayerMP))
			return EnumActionResult.PASS;
		
		if (stack == null || stack.stackSize != 1)
			return EnumActionResult.PASS;
		ItemStack sensorLocationCard = getItemKitBase(stack.getItemDamage()).getSensorCard(stack, ItemHelper.itemCard, player, world, pos);
		if (sensorLocationCard == null)
			return EnumActionResult.PASS;
		
		player.replaceItemInInventory(player.inventory.currentItem, sensorLocationCard);
		return EnumActionResult.SUCCESS;
	}	

	public IItemKit getItemKitBase(int metadata) {
		if (kits.containsKey(metadata))
			return kits.get(metadata);
		return null;
	}

	public static final void registerModels() {
		for (Map.Entry<Integer, IItemKit> entry : kits.entrySet()) {
			Integer key = entry.getKey();
			if (key <= ItemCardType.KIT_MAX)
				ItemHelper.registerItemModel(ItemHelper.itemKit, key, kits.get(key).getName());
		}
	}

	public static final void registerExtendedModels() {
		for (Map.Entry<Integer, IItemKit> entry : kits.entrySet()) {
			Integer key = entry.getKey();
			if (key > ItemCardType.KIT_MAX)
				ItemHelper.registerExternalItemModel(ItemHelper.itemKit, key, kits.get(key).getName());
		}
	}

	public static final void registerRecipes() {
		for (Map.Entry<Integer, IItemKit> entry : kits.entrySet()) {
			Integer key = entry.getKey();
			Object[] recipe = entry.getValue().getRecipe();
			if (recipe != null)
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, key), recipe));
		}
	}
}
