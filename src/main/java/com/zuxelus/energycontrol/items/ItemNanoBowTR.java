package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.proxy.ClientProxy;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import reborncore.common.util.ItemUtils;

public class ItemNanoBowTR extends ItemNanoBow implements IEnergyItemInfo {

	public ItemNanoBowTR() {
		setMaxDamage(0);
	}

	public boolean isRepairable() {
		return false;
	}

	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - ItemUtils.getPowerForDurabilityBar(stack);
	}

	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return (PowerSystem.getDisplayPower()).colour;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return CrossModLoader.getCrossMod(ModIDs.IC2).initCapabilities(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		/*if (!isInCreativeTab(tab))
			return;
		ItemStack charged = CrossModLoader.techReborn.getChargedStack(new ItemStack(this, 1));
		items.add(charged);
		items.add(new ItemStack(this, 1, getMaxDamage()));*/
	}

	@Override
	protected void discharge(ItemStack stack, double amount, EntityLivingBase player) {
		ForgePowerItemManager capEnergy = new ForgePowerItemManager(stack);
		capEnergy.extractEnergy((int) amount, false);
		ExternalPowerSystems.requestEnergyFromArmor(capEnergy, player);
	}

	@Override
	protected boolean canUse(ItemStack stack, double amount) {
		ForgePowerItemManager capEnergy = new ForgePowerItemManager(stack);
		return capEnergy.extractEnergy((int) amount, true) >= amount;
	}

	@Override
	protected boolean isModeSwitchKeyDown(EntityPlayer player) {
		return GameSettings.isKeyDown(ClientProxy.modeSwitchKey);
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
		NBTTagCompound nbt = ItemStackHelper.getTagCompound(stack);
		if (nbt.getInteger("maxCharge") == 0)
			nbt.setInteger("maxCharge", getDefaultMaxCharge());
		return nbt.getInteger("maxCharge");
	}

	@Override
	public double getMaxTransfer(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getTagCompound(stack);
		if (nbt.getInteger("transferLimit") == 0)
			nbt.setInteger("transferLimit", getDefaultTransferLimit());
		return nbt.getInteger("transferLimit");
	}
}
