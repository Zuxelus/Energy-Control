package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Optional.Interface(modid = ModIDs.IC2, iface = "ic2.api.item.IElectricItem")
public class ItemDigitalThermometer extends ItemThermometer implements IElectricItem {
	protected final static int CAPACITY = 12000;
	protected final static int TRANSFER_LIMIT = 250;

	public ItemDigitalThermometer() {
		super();
		setMaxDamage(13);
		setHasSubtypes(true);
	}

	@Override
	protected boolean canTakeDamage(ItemStack itemstack, int i) {
		i *= 50;
		return ElectricItem.manager.discharge(itemstack, i, Integer.MAX_VALUE, true, false, true) == i;
	}

	@Override
	protected void messagePlayer(EntityPlayer player, IReactor reactor) {
		int heat = reactor.getHeat();
		int maxHeat = reactor.getMaxHeat();
		player.sendMessage(new TextComponentTranslation("msg.ec.ThermoDigital", heat, maxHeat * 50 / 100, maxHeat * 85 / 100));
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

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab))
			return;
		items.add(CrossModLoader.getCrossMod(ModIDs.IC2).getChargedStack(new ItemStack(this, 1)));
		items.add(new ItemStack(this, 1, getMaxDamage()));
	}
}
