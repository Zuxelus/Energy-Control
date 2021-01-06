package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

public class ItemDigitalThermometer extends ItemThermometer implements IElectricItem {
	protected final static int CAPACITY = 12000;
	protected final static int TRANSFER_LIMIT = 250;

	public ItemDigitalThermometer() {
		super();
		setMaxDamage(13);
		setHasSubtypes(true);
		setTextureName(EnergyControl.MODID + ":" + "thermometer_digital");
	}

	@Override
	protected boolean canTakeDamage(ItemStack itemstack, int i) {
		i *= 50;
		return ElectricItem.manager.discharge(itemstack, i, Integer.MAX_VALUE, true, false, true) == i;
	}

	@Override
	protected void messagePlayer(EntityPlayer player, IReactor reactor) {
		int maxHeat = reactor.getMaxHeat();
		player.addChatMessage(new ChatComponentTranslation("msg.ec.ThermoDigital", reactor.getHeat(), maxHeat * 50 / 100, maxHeat * 85 / 100));
		//NetworkHelper.chatMessage(entityplayer, I18n.format("msg.ec.ThermoDigital", heat, maxHeat * 50 / 100, maxHeat * 85 / 100));
	}

	@Override
	protected void damage(ItemStack stack, int i, EntityPlayer player) {
		ElectricItem.manager.use(stack, 50 * i, player);
	}

	// IElectricItem
	@Override
	public boolean canProvideEnergy(ItemStack stack) {
		return false;
	}

	@Override
	public Item getChargedItem(ItemStack itemStack) {
		return this;
	}

	@Override
	public Item getEmptyItem(ItemStack itemStack) {
		return this;
	}

	@Override
	public double getMaxCharge(ItemStack stack) {
		return CAPACITY;
	}

	@Override
	public int getTier(ItemStack stack) {
		return 1;
	}

	@Override
	public double getTransferLimit(ItemStack stack) {
		return TRANSFER_LIMIT;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		items.add(CrossModLoader.ic2.getChargedStack(new ItemStack(this, 1)));
		items.add(new ItemStack(this, 1, getMaxDamage()));
	}
}