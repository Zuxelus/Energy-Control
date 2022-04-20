package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardEnderIO;
import com.zuxelus.energycontrol.items.cards.ItemCardPneumaticCraft;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber
public class ItemKitMain extends Item implements IItemKit {
	private static final Map<Integer, ItemKitBase> KITS = new HashMap<>();

	public ItemKitMain() {
		super();
		setMaxStackSize(16);
		setHasSubtypes(true);
		setMaxDamage(0);
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
	}

	public static ItemStack getKitById(int id) {
		return KITS.containsKey(id) ? new ItemStack(ModItems.itemKit, 1, id) : ItemStack.EMPTY;
	}

	public final void registerKits() {
		register(ItemKitEnergy::new);
		register(ItemKitCounter::new);
		register(ItemKitLiquid::new);
		if (Loader.isModLoaded(ModIDs.IC2)) {
			register(ItemKitGenerator::new);
			register(ItemKitReactor::new);
		}
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
		if (Loader.isModLoaded(ModIDs.GREGTECH))
			register(ItemKitGregTech::new);
		if (Loader.isModLoaded(ModIDs.BIG_REACTORS))
			register(ItemKitBigReactors::new);
		if (Loader.isModLoaded(ModIDs.ENDER_IO))
			register(ItemKitEnderIO::new);
		if (Loader.isModLoaded(ModIDs.HBM))
			register(ItemKitHBM::new);
		if (Loader.isModLoaded(ModIDs.MEKANISM))
			register(ItemKitMekanism::new);
		if (Loader.isModLoaded(ModIDs.NUCLEAR_CRAFT))
			register(ItemKitNuclearCraft::new);
		if (Loader.isModLoaded(ModIDs.PNEUMATICCRAFT))
			register(ItemKitPneumaticCraft::new);
		if (Loader.isModLoaded(ModIDs.THERMAL_EXPANSION))
			register(ItemKitThermalExpansion::new);
	}

	private static void register(Supplier<ItemKitBase> factory) {
		ItemKitBase item = factory.get();
		if (checkKit(item))
			KITS.put(item.getDamage(), item);
	}

	private static boolean checkKit(ItemKitBase item) {
		if (!KITS.containsKey(item.getDamage()))
			return true;
		if (item.getDamage() <= ItemCardType.KIT_MAX)
			EnergyControl.logger.warn(String.format("Kit %s was not registered. ID %d is already used for standard kit.", item.getUnlocalizedName(), item.getDamage()));
		else
			EnergyControl.logger.warn(String.format("Kit %s was not registered. ID %d is already used for extended kit.", item.getUnlocalizedName(), item.getDamage()));
		return false;
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
		for (Map.Entry<Integer, ItemKitBase> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			items.add(new ItemStack(this, 1, key));
		}
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		int damage = stack.getItemDamage();
		if (KITS.containsKey(damage))
			return KITS.get(damage).getSensorCard(stack, player, world, pos, side);
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();
		if (!player.world.isRemote && !stack.isEmpty()) {
			Item item = stack.getItem();
			if (item instanceof IItemKit) {
				ItemStack card = ((IItemKit) item).getSensorCard(stack, player, player.world, event.getPos(), event.getFace());
				if (!card.isEmpty()) {
					stack.shrink(1);

					EntityItem dropItem = new EntityItem(player.world, player.posX, player.posY, player.posZ, card);
					dropItem.setPickupDelay(0);
					player.world.spawnEntity(dropItem);

					event.setCanceled(true);
					event.setCancellationResult(EnumActionResult.SUCCESS);
				}
			}
		}
	}

	public static void registerModels() {
		for (Map.Entry<Integer, ItemKitBase> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			if (key <= ItemCardType.KIT_MAX)
				ModItems.registerItemModel(ModItems.itemKit, key, KITS.get(key).getName());
		}
	}

	public static void registerExtendedModels() {
		for (Map.Entry<Integer, ItemKitBase> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			if (key > ItemCardType.KIT_MAX)
				ModItems.registerExternalItemModel(ModItems.itemKit, key, KITS.get(key).getName());
		}
	}
}
