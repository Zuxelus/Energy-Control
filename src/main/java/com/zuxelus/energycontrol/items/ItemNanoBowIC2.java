package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.util.Keys;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNanoBowIC2 extends ItemNanoBow implements IElectricItem {

	public ItemNanoBowIC2() {
		super();
		setMaxDamage(27);
		setNoRepair();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		IElectricItem item = (IElectricItem) stack.getItem();
		if (!nbt.hasKey("loaded")) {
			if (nbt.getInteger("tier") == 0)
				nbt.setInteger("tier", item.getTier(stack));
			if (nbt.getInteger("transferLimit") == 0)
				nbt.setDouble("transferLimit", item.getTransferLimit(stack));
			if (nbt.getInteger("maxCharge") == 0)
				nbt.setDouble("maxCharge", item.getMaxCharge(stack));
			nbt.setBoolean("loaded", true);
		}
		if (nbt.getInteger("transferLimit") != item.getTransferLimit(stack))
			tooltip.add(String.format(I18n.format("info.transferspeed"), nbt.getDouble("transferLimit")));
		if (nbt.getInteger("tier") != item.getTier(stack))
			tooltip.add(String.format(I18n.format("info.chargingtier"), nbt.getInteger("tier")));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab))
			return;
		ItemStack charged = CrossModLoader.ic2.getChargedStack(new ItemStack(this, 1));
		items.add(charged);
		items.add(new ItemStack(this, 1, getMaxDamage()));
	}

	@Override
	protected void discharge(ItemStack stack, double amount, EntityLivingBase player) {
		ElectricItem.manager.use(stack, amount, player);
	}

	@Override
	protected boolean canUse(ItemStack stack, double amount) {
		return ElectricItem.manager.canUse(stack, amount);
	}

	@Override
	protected boolean isModeSwitchKeyDown(EntityPlayer player) {
		return Keys.instance.isModeSwitchKeyDown(player);
	}

	// IElectricItem
	@Override
	public boolean canProvideEnergy(ItemStack stack) {
		return false;
	}

	@Override
	public double getMaxCharge(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		if (nbt.getInteger("maxCharge") == 0)
			nbt.setInteger("maxCharge", getDefaultMaxCharge());
		return nbt.getInteger("maxCharge");
	}

	@Override
	public int getTier(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		if (nbt.getInteger("tier") == 0)
			nbt.setInteger("tier", getDefaultTier());
		return nbt.getInteger("tier");
	}

	@Override
	public double getTransferLimit(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		if (nbt.getInteger("transferLimit") == 0)
			nbt.setInteger("transferLimit", getDefaultTransferLimit());
		return nbt.getInteger("transferLimit");
	}
}
