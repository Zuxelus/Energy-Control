package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.network.NetworkHelper;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDigitalThermometer extends ItemThermometer implements IElectricItem {

	public int tier;
	public int ratio;
	public int transfer;

	public ItemDigitalThermometer(int k, int l, int i1) {
		super();
		setMaxDamage(13);
		setHasSubtypes(true);
		tier = k;
		ratio = l;
		transfer = i1;
	}

	@Override
	protected boolean canTakeDamage(ItemStack itemstack, int i) {
		i *= 50;
		return ElectricItem.manager.discharge(itemstack, i, Integer.MAX_VALUE, true, false, true) == i;
	}

	@Override
	protected void messagePlayer(EntityPlayer entityplayer, IReactor reactor) {
		int heat = reactor.getHeat();
		int maxHeat = reactor.getMaxHeat();
		NetworkHelper.chatMessage(entityplayer, I18n.format("msg.ec.ThermoDigital", heat, maxHeat * 50 / 100, maxHeat * 85 / 100));
	}

	@Override
	protected void damage(ItemStack itemstack, int i, EntityPlayer entityplayer) {
		ElectricItem.rawManager.use(itemstack, 50 * i, entityplayer);
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return 12000;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return tier;
	}

	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return 250;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> itemList) {
		ItemStack itemstack = new ItemStack(this, 1);
		ElectricItem.manager.charge(itemstack, 0x7fffffff, 0x7fffffff, true, false);
		itemList.add(itemstack);
		itemList.add(new ItemStack(this, 1, getMaxDamage()));
	}
}