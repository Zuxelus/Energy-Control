package com.zuxelus.energycontrol.items.kits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
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
		register(new ItemKitReactor());
		register(new ItemKitLiquidAdvanced());
		register(new ItemKitToggle());
		register(new ItemKitVanilla());
		register(new ItemKitInventory());
		register(new ItemKitRedstone());
		if (Loader.isModLoaded("DraconicEvolution"))
			register(new ItemKitDraconic());
		if (Loader.isModLoaded("appliedenergistics2"))
			register(new ItemKitAppEng());
		if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars"))
			register(new ItemKitGalacticraft());
		if (Loader.isModLoaded("BigReactors"))
			register(new ItemKitBigReactors());
		if (Loader.isModLoaded("gregtech"))
			register(new ItemKitGregTech());
		if (Loader.isModLoaded("nuclearcraft"))
			register(new ItemKitNuclearCraft());
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for (Map.Entry<Integer, IItemKit> entry : kits.entrySet()) {
			IItemKit value = entry.getValue();
			value.registerIcon(iconRegister);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if (kits.containsKey(damage))
			return kits.get(damage).getIcon();
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List items) {
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
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (stack == null || player == null || !(player instanceof EntityPlayerMP))
			return false;
		ItemStack card = getItemKitBase(stack.getItemDamage()).getSensorCard(stack, ModItems.itemCard, player, world, x, y, z);
		if (card == null)
			return false;

		if (stack.stackSize == 1)
			player.inventory.mainInventory[player.inventory.currentItem] = card;
		else {
			--stack.stackSize;
			EntityItem item = new EntityItem(world, player.posX, player.posY, player.posZ, card);
			item.delayBeforeCanPickup = 0;
			world.spawnEntityInWorld(item);
		}
		return true;
	}

	public IItemKit getItemKitBase(int metadata) {
		if (kits.containsKey(metadata))
			return kits.get(metadata);
		return null;
	}

	public static final void registerRecipes() {
		for (Map.Entry<Integer, IItemKit> entry : kits.entrySet()) {
			Integer key = entry.getKey();
			Object[] recipe = entry.getValue().getRecipe();
			if (recipe != null)
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, key), recipe));
		}
	}
}
