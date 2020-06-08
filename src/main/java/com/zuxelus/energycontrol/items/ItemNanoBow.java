package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.entities.EntityTechArrow;
import com.zuxelus.energycontrol.network.NetworkHelper;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.util.Keys;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNanoBow extends ItemBow implements IElectricItem /* , IItemUpgradeable */ {
	static final int NORMAL = 1;
	static final int RAPID = 2;
	static final int SPREAD = 3;
	static final int SNIPER = 4;
	static final int FLAME = 5;
	static final int EXPLOSIVE = 6;
	static String[] modeName = { I18n.format("info.normal"), I18n.format("info.rapidfire"), I18n.format("info.spread"),
			I18n.format("info.sniper"), I18n.format("info.flame"), I18n.format("info.explosive") };
	static final int[] CHARGE = { 300, 150, 400, 1000, 200, 800 };

	public ItemNanoBow() {
		super();
		setCreativeTab(EnergyControl.creativeTab);
		setMaxDamage(27);
		setNoRepair();
		setFull3D();
	}

	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
		if (!(entity instanceof EntityPlayer))
			return;

		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInteger("bowMode");
		
		EntityPlayer player = (EntityPlayer) entity;
		int i = this.getMaxItemUseDuration(stack) - timeLeft;
		i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, i, true);
		if (i < 0)
			return;

		if (mode == RAPID)
			i = i * 2;
		float f = getArrowVelocity(i);
		if (f < 0.1)
			return;

		if (!world.isRemote) {
			EntityTechArrow arrow = new EntityTechArrow(world, player);
			arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

			if (f == 1.5F)
				arrow.setIsCritical(true);

			int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
			if (j > 0)
				arrow.setDamage(arrow.getDamage() + j * 0.5D + 0.5D);
			if (mode == NORMAL && arrow.getIsCritical())
				j += 3;
			else if (mode == RAPID && arrow.getIsCritical())
				j += 1;
			else if (mode == SNIPER && arrow.getIsCritical())
				j += 8;
			if (j > 0)
				arrow.setDamage(arrow.getDamage() + j * 0.5D + 0.5D);
			/*
			 * if (IC2CA.nanoBowBoost > 0) arrow.setDamage(arrow.getDamage() +
			 * IC2CA.nanoBowBoost * 0.5D + 0.5D);
			 */

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
			if (mode == NORMAL && arrow.getIsCritical())
				k += 1;
			else if (mode == SNIPER && arrow.getIsCritical())
				k += 5;
			if (k > 0)
				arrow.setKnockbackStrength(k);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
				arrow.setFire(100);
			if (mode == FLAME && arrow.getIsCritical())
				arrow.setFire(2000);
			if (mode == EXPLOSIVE && arrow.getIsCritical())
				arrow.setExplosive(true);

			arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
			world.spawnEntity(arrow);

			switch (mode) {
			case NORMAL:
			case RAPID:
			case SNIPER:
			case FLAME:
			case EXPLOSIVE:
				ElectricItem.manager.use(stack, CHARGE[mode - 1], player);
				world.spawnEntity(arrow);
				break;
			case SPREAD:
				ElectricItem.manager.use(stack, 350, player);
				world.spawnEntity(arrow);
				if (arrow.getIsCritical()) {
					EntityTechArrow arrow2 = new EntityTechArrow(world, player);
					arrow2.shoot(player, player.rotationPitch + 2.0F, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);
					arrow2.setIsCritical(true);
					arrow2.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
					EntityTechArrow arrow3 = new EntityTechArrow(world, player);
					arrow3.shoot(player, player.rotationPitch - 2.0F, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);
					arrow3.setIsCritical(true);
					arrow3.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
					EntityTechArrow arrow4 = new EntityTechArrow(world, player);
					arrow4.shoot(player, player.rotationPitch, player.rotationYaw + 2.0F, 0.0F, f * 3.0F, 1.0F);
					arrow4.setIsCritical(true);
					arrow4.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
					EntityTechArrow arrow5 = new EntityTechArrow(world, player);
					arrow5.shoot(player, player.rotationPitch, player.rotationYaw - 2.0F, 0.0F, f * 3.0F, 1.0F);
					arrow5.setIsCritical(true);
					arrow5.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
					world.spawnEntity(arrow2);
					world.spawnEntity(arrow3);
					world.spawnEntity(arrow4);
					world.spawnEntity(arrow5);
				}
				break;
			}
		}
		world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
				SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
		player.addStat(StatList.getObjectUseStats(this));
	}

	public static float getArrowVelocity(int charge) {
		float f = charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		return f > 1.5F ? 1.5F : f;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		switch (nbt.getInteger("bowMode")) {
		case SNIPER:
		case EXPLOSIVE:
			return 144000;
		case RAPID:
			return 18000;
		default:
			return 72000;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInteger("bowMode");
		if (!world.isRemote && Keys.instance.isModeSwitchKeyDown(player) && nbt.getByte("toggleTimer") == 0) {
			byte toggle = 10;
			nbt.setByte("toggleTimer", toggle);

			mode++;
			/*if (mode == RAPID && !IC2CA.rapidFireMode)
				mode++;
			if (mode == SPREAD && !IC2CA.spreadMode)
				mode++;
			if (mode == SNIPER && !IC2CA.sniperMode)
				mode++;
			if (mode == FLAME && !IC2CA.flameMode)
				mode++;
			if (mode == EXPLOSIVE && !IC2CA.explosiveMode)
				mode++;*/
			if (mode > EXPLOSIVE) {
				mode -= EXPLOSIVE;
			}
			nbt.setInteger("bowMode", mode);
			NetworkHelper.chatMessage(player, I18n.format("info.modeenabled", modeName[(mode - 1)]));
			return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
		}

		if (ElectricItem.manager.canUse(stack, CHARGE[mode - 1])) {
			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult(EnumActionResult.FAIL, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		byte toggle = nbt.getByte("toggleTimer");
		if (toggle > 0)
			nbt.setByte("toggleTimer", --toggle);
		int mode = nbt.getInteger("bowMode");
		if (mode == 0)
			nbt.setInteger("bowMode", 1);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInteger("bowMode");
		if (mode == RAPID) {
			int j = getMaxItemUseDuration(stack) - count;
			if ((j >= 10) && (ElectricItem.manager.canUse(stack, CHARGE[RAPID - 1])))
				player.stopActiveHand();
		}
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
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab))
			return;
		ItemStack charged = new ItemStack(this, 1);
		ElectricItem.manager.charge(charged, 2147483647, 2147483647, true, false);
		items.add(charged);
		items.add(new ItemStack(this, 1, getMaxDamage()));
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

	// IItemUpgradeable
	// @Override
	public int getDefaultMaxCharge() {
		return 40000;
	}

	// @Override
	public int getDefaultTier() {
		return 2;
	}

	// @Override
	public int getDefaultTransferLimit() {
		return 128;
	}
}
