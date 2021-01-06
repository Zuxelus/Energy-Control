package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.reactor.IReactor;
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
		setTextureName(EnergyControl.MODID + ":" + "thermometer");
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

		IReactor reactor = ReactorHelper.getReactorAround(world, x, y, z);
		if (reactor == null)
			reactor = ReactorHelper.getReactor3x3(world, x, y, z);
		if (reactor != null) {
			messagePlayer(player, reactor);
			damage(stack, 1, player);
			return true;
		}
		return false;
	}

	protected void messagePlayer(EntityPlayer player, IReactor reactor) {
		player.addChatMessage(new ChatComponentTranslation("msg.ec.Thermo", reactor.getHeat()));
		//NetworkHelper.chatMessage(entityplayer, I18n.format("msg.ec.Thermo", reactor.getHeat()));
	}

	protected void damage(ItemStack stack, int i, EntityPlayer player) {
		stack.damageItem(10, player);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {
		return false;
	}
}
