package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemThermometer extends Item {

	public ItemThermometer() {
		super();
		setMaxDamage(102);
		setMaxStackSize(1);
		setCreativeTab(EnergyControl.creativeTab);
		setTextureName(EnergyControl.MODID + ":" + "thermometer"); // 1.7.10
	}

	protected boolean canTakeDamage(ItemStack itemstack, int i) {
		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!(player instanceof EntityPlayerMP))
			return false;

		if (stack == null)
			return false;
		if (!canTakeDamage(stack, 2))
			return false;

		int heat = CrossModLoader.getHeat(world, x, y, z);
		if (heat > -1) {
			player.addChatMessage(new ChatComponentTranslation("msg.ec.Thermo", heat));
			damage(stack, 1, player);
			return true;
		}
		return false;
	}

	protected void damage(ItemStack stack, int i, EntityPlayer player) {
		stack.damageItem(10, player);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {
		return false;
	}
}
