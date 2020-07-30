package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.entities.EntityTechArrow;
import com.zuxelus.energycontrol.network.NetworkHelper;

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
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemNanoBow extends ItemBow /* , IItemUpgradeable */ {
	static final int NORMAL = 1;
	static final int RAPID = 2;
	static final int SPREAD = 3;
	static final int SNIPER = 4;
	static final int FLAME = 5;
	static final int EXPLOSIVE = 6;
	static final int[] CHARGE = { 300, 150, 400, 1000, 200, 800 };

	public ItemNanoBow() {
		super();
		setCreativeTab(EnergyControl.creativeTab);
		setFull3D();
	}

	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
		if (!(entity instanceof EntityPlayer))
			return;

		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInteger("bowMode");
		
		EntityPlayer player = (EntityPlayer) entity;
		int charge = getMaxItemUseDuration(stack) - timeLeft;
		charge = ForgeEventFactory.onArrowLoose(stack, world, player, charge, true);
		if (charge < 0)
			return;

		if (mode == RAPID)
			charge = charge * 2;
		float f = getArrowVelocity(charge);
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

			switch (mode) {
			case NORMAL:
			case RAPID:
			case SNIPER:
			case FLAME:
			case EXPLOSIVE:
				discharge(stack, CHARGE[mode - 1], player);
				world.spawnEntity(arrow);
				break;
			case SPREAD:
				discharge(stack, 350, player);
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
		ActionResult<ItemStack> result = ForgeEventFactory.onArrowNock(stack, world, player, hand, true);
		if (result != null)
			return result;

		NBTTagCompound nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInteger("bowMode");
		if (!world.isRemote && isModeSwitchKeyDown(player) && nbt.getByte("toggleTimer") == 0) {
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
			NetworkHelper.chatMessage(player, "info.modeenabled", 1, mode - 1);
			return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
		}

		if (canUse(stack, CHARGE[mode - 1])) {
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
			if ((j >= 10) && (canUse(stack, CHARGE[RAPID - 1])))
				player.stopActiveHand();
		}
	}

	protected abstract void discharge(ItemStack stack, double amount, EntityLivingBase entity);
	
	protected abstract boolean canUse(ItemStack stack, double amount);
	
	protected abstract boolean isModeSwitchKeyDown(EntityPlayer player);

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.UNCOMMON;
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
