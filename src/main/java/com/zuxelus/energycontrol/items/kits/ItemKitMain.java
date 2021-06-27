package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemKitMain extends Item {
	private static final Map<Integer, IItemKit> KITS = new HashMap<>();

	public ItemKitMain() {
		super();
		setMaxStackSize(16);
		setHasSubtypes(true);
		setMaxDamage(0);
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
	}

	public final void registerKits() {
		register(ItemKitEnergy::new);
		register(ItemKitCounter::new);
		register(ItemKitLiquid::new);
		register(ItemKitGenerator::new);
		if (Loader.isModLoaded(ModIDs.IC2))
			register(ItemKitReactor::new);
		register(ItemKitLiquidAdvanced::new);
		register(ItemKitToggle::new);
		register(ItemKitVanilla::new);
		register(ItemKitInventory::new);
		register(ItemKitRedstone::new);
		if (Loader.isModLoaded(ModIDs.DRACONIC_EVOLUTION))
			register(ItemKitDraconic::new);
		if (Loader.isModLoaded(ModIDs.APPLIED_ENERGISTICS))
			register(ItemKitAppEng::new);
		if (Loader.isModLoaded(ModIDs.GALACTICRAFT_CORE) && Loader.isModLoaded(ModIDs.GALACTICRAFT_PLANETS))
			register(ItemKitGalacticraft::new);
		if (Loader.isModLoaded(ModIDs.BIG_REACTORS))
			register(ItemKitBigReactors::new);
		if (Loader.isModLoaded(ModIDs.NUCLEAR_CRAFT))
			register(ItemKitNuclearCraft::new);
		if (Loader.isModLoaded(ModIDs.MEKANISM_GENERATORS))
			register(ItemKitMekanism::new);
		if (Loader.isModLoaded(ModIDs.THERMAL_EXPANSION))
			register(ItemKitThermalExpansion::new);
	}

	private static void register(Supplier<ItemKitBase> factory) {
		ItemKitBase item = factory.get();
		if (checkKit(item))
			KITS.put(item.getDamage(), item);
	}

	private static boolean checkKit(IItemKit item) {
		if (!KITS.containsKey(item.getDamage()))
			return true;
		if (item.getDamage() <= ItemCardType.KIT_MAX)
			EnergyControl.logger.warn(String.format("Kit %s was not registered. ID %d is already used for standard kit.", item.getUnlocalizedName(), item.getDamage()));
		else
			EnergyControl.logger.warn(String.format("Kit %s was not registered. ID %d is already used for extended kit.", item.getUnlocalizedName(), item.getDamage()));
		return false;
	}

	public static void registerKit(IItemKit item) {
		if (checkKit(item)) {
			if (item.getDamage() <= ItemCardType.KIT_MAX) {
				EnergyControl.logger.warn(String.format("Kit %s was not registered. Kit ID should be bigger than %d", item.getUnlocalizedName(), ItemCardType.CARD_MAX));
				return;
			}
			KITS.put(item.getDamage(), item);
		}
	}

	public static boolean containsKit(int i) {
		return KITS.containsKey(i);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (KITS.containsKey(damage))
			return KITS.get(damage).getUnlocalizedName();
		return "";
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab))
			return;
		for (Map.Entry<Integer, IItemKit> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			items.add(new ItemStack(this, 1, key));
		}
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) { 	
		if (!(player instanceof EntityPlayerMP))
			return EnumActionResult.PASS;

		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty())
			return EnumActionResult.PASS;
		IItemKit kit = getItemKitBase(stack.getItemDamage());
		if (kit == null)
			return EnumActionResult.PASS;
		ItemStack card = kit.getSensorCard(stack, ModItems.itemCard, player, world, pos, side);
		if (card.isEmpty())
			return EnumActionResult.PASS;

		stack.shrink(1);
		EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, card);
		dropItem.setPickupDelay(0);
		world.spawnEntity(dropItem);
		return EnumActionResult.SUCCESS;
	}

	public IItemKit getItemKitBase(int metadata) {
		if (KITS.containsKey(metadata))
			return KITS.get(metadata);
		return null;
	}

	public static void registerModels() {
		for (Map.Entry<Integer, IItemKit> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			if (key <= ItemCardType.KIT_MAX)
				ModItems.registerItemModel(ModItems.itemKit, key, KITS.get(key).getName());
		}
	}

	public static void registerExtendedModels() {
		for (Map.Entry<Integer, IItemKit> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			if (key > ItemCardType.KIT_MAX)
				ModItems.registerExternalItemModel(ModItems.itemKit, key, KITS.get(key).getName());
		}
	}
}
