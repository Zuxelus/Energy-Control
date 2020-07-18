package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.ClientProxy;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import reborncore.api.item.IColoredDamageBar;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PoweredItem;

public class ItemNanoBowTR extends ItemNanoBow implements IEnergyInterfaceItem, IColoredDamageBar {

	public ItemNanoBowTR() {
		setMaxDamage(0);
	}

	public boolean isRepairable() {
		return false;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		double charge = (PoweredItem.getEnergy(stack) / getMaxPower(stack));
		return 1 - charge;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false; // Needs to be false so the custom one renders
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
		ItemStack charged = CrossModLoader.techReborn.getChargedStack(new ItemStack(this, 1));
		items.add(charged);
		items.add(new ItemStack(this, 1, getMaxDamage()));
	}

	@Override
	protected void discharge(ItemStack stack, double amount, EntityLivingBase player) {
		PoweredItem.useEnergy(amount, stack);
	}

	@Override
	protected boolean canUse(ItemStack stack, double amount) {
		return PoweredItem.canUseEnergy(amount, stack);
	}

	@Override
	protected boolean isModeSwitchKeyDown(EntityPlayer player) {
		return GameSettings.isKeyDown(ClientProxy.modeSwitchKey);
	}

	// IColoredDamageBar
	@Override
	public boolean showRGBDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public int getRGBDurabilityForBar(ItemStack stack) {
		return PowerSystem.getDisplayPower().colour;
	}

	// IEnergyItemInfo
	@Override
	public boolean canAcceptEnergy(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canProvideEnergy(ItemStack stack) {
		return false;
	}

	@Override
	public double getMaxPower(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		if (nbt.getInteger("maxCharge") == 0)
			nbt.setInteger("maxCharge", getDefaultMaxCharge());
		return nbt.getInteger("maxCharge");
	}

	@Override
	public double getMaxTransfer(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		if (nbt.getInteger("transferLimit") == 0)
			nbt.setInteger("transferLimit", getDefaultTransferLimit());
		return nbt.getInteger("transferLimit");
	}

	@Override
	public int getStackTier(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		if (nbt.getInteger("tier") == 0)
			nbt.setInteger("tier", getDefaultTier());
		return nbt.getInteger("tier");
	}

	@Override
	public double addEnergy(double energy, ItemStack stack) {
		return addEnergy(energy, false, stack);
	}

	@Override
	public double addEnergy(double energy, boolean simulate, ItemStack stack) {
		double energyReceived = Math.min(getMaxPower(stack) - energy, Math.min(getMaxPower(stack), energy));
		if (!simulate)
			setEnergy(energy + energyReceived, stack);
		return energyReceived;
	}

	@Override
	public boolean canAddEnergy(double energy, ItemStack stack) {
		return (getEnergy(stack) + energy <= getMaxPower(stack));
	}

	@Override
	public boolean canUseEnergy(double input, ItemStack stack) {
		return (input <= getEnergy(stack));
	}

	@Override
	public double getEnergy(ItemStack stack) {
		NBTTagCompound tagCompound = getOrCreateNbtData(stack);
		if (tagCompound.hasKey("charge"))
			return tagCompound.getDouble("charge");
		return 0;
	}

	@Override
	public void setEnergy(double energy, ItemStack stack) {
		NBTTagCompound tagCompound = getOrCreateNbtData(stack);
		tagCompound.setDouble("charge", energy);

		if (getEnergy(stack) > getMaxPower(stack))
			setEnergy(getMaxPower(stack), stack);
		else if (getEnergy(stack) < 0)
			setEnergy(0, stack);
	}

	private NBTTagCompound getOrCreateNbtData(ItemStack itemStack) {
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if (tagCompound == null) {
			tagCompound = new NBTTagCompound();
			itemStack.setTagCompound(tagCompound);
		}

		return tagCompound;
	}

	@Override
	public double useEnergy(double energy, ItemStack stack) {
		return useEnergy(energy, false, stack);
	}

	@Override
	public double useEnergy(double extract, boolean simulate, ItemStack stack) {
		double energyExtracted = Math.min(extract, Math.min(getMaxTransfer(stack), extract));
		if (!simulate)
			setEnergy(getEnergy(stack) - energyExtracted, stack);
		return energyExtracted;
	}
}
