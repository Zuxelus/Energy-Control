package com.zuxelus.energycontrol.items.kits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.ItemHelper;

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

public class ItemKitMain extends Item {
	public static Map<Integer, ItemKitBase> kits;
	
	public ItemKitMain() {
		super();
		setMaxStackSize(1);
		setCreativeTab(EnergyControl.creativeTab);
		kits = new HashMap<Integer, ItemKitBase>();
		register(new ItemKitEnergy());
		register(new ItemKitCounter());
		register(new ItemKitLiquid());
		register(new ItemKitGenerator());
		register(new ItemKitReactor());
	}
	
	public void register(ItemKitBase item) {
		kits.put(item.getDamage(), item);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (kits.containsKey(damage))
			return kits.get(damage).getUnlocalizedName();
		return "";
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i <= ItemHelper.KIT_MAX; i++)
			if (kits.containsKey(i))
				list.add(new ItemStack(this, 1, i));
	}
	
	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (player == null || !(player instanceof EntityPlayerMP))
			return EnumActionResult.PASS;
		
		ItemStack sensorLocationCard = getItemKitBase(stack.getItemDamage()).getSensorCard(stack, player, world, pos);
		if (sensorLocationCard == null)
			return EnumActionResult.PASS;
		
		player.inventory.mainInventory[player.inventory.currentItem] = sensorLocationCard;
		return EnumActionResult.SUCCESS;
	}	
	
	public ItemKitBase getItemKitBase(int metadata) {
		if (kits.containsKey(metadata))
			return kits.get(metadata);
		return null;
	}
}
