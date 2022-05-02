package com.zuxelus.energycontrol.items.kits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

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

	public final void registerKits() {
		register(ItemKitEnergy::new);
		register(ItemKitCounter::new);
		register(ItemKitLiquid::new);
		register(ItemKitLiquidAdvanced::new);
		register(ItemKitToggle::new);
		register(ItemKitVanilla::new);
		register(ItemKitInventory::new);
		register(ItemKitRedstone::new);
		/*if (Loader.isModLoaded("DraconicEvolution"))
			register("ItemKitDraconic");
		if (Loader.isModLoaded("appliedenergistics2"))
			register("ItemKitAppEng");
		if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars"))
			register("ItemKitGalacticraft");
		if (Loader.isModLoaded("BigReactors"))
			register("ItemKitBigReactors");
		if (Loader.isModLoaded("gregtech"))
			register(new ItemKitGregTech());
		if (Loader.isModLoaded("nuclearcraft"))
			register("ItemKitNuclearCraft");
		if (Loader.isModLoaded("mekanismgenerators"))
			register("ItemKitMekanism");
		if (Loader.isModLoaded("thermalexpansion"))
			register("ItemKitThermalExpansion");*/
	}

	public static void register(Supplier<ItemKitBase> factory) {
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for (Map.Entry<Integer, ItemKitBase> entry : KITS.entrySet()) {
			ItemKitBase value = entry.getValue();
			value.registerIcon(iconRegister);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if (KITS.containsKey(damage))
			return KITS.get(damage).getIcon();
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		for (Map.Entry<Integer, ItemKitBase> entry : KITS.entrySet()) {
			Integer key = entry.getKey();
			items.add(new ItemStack(this, 1, key));
		}
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		int damage = stack.getItemDamage();
		if (KITS.containsKey(damage))
			return KITS.get(damage).getSensorCard(stack, player, world, x, y, z, side);
		return null;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (stack == null || player == null || !(player instanceof EntityPlayerMP))
			return false;

		if (!player.worldObj.isRemote && stack != null) {
			Item item = stack.getItem();
			if (item instanceof IItemKit) {
				ItemStack card = ((IItemKit) item).getSensorCard(stack, player, player.worldObj, x, y, z, side);
				if (card != null && card.stackSize > 0) {
					stack.stackSize -= 1;

					EntityItem dropItem = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, card);
					dropItem.delayBeforeCanPickup = 0;
					player.worldObj.spawnEntityInWorld(dropItem);
					return true;
				}
			}
		}
		return false;
	}
}
