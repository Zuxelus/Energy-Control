package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.entities.TechArrowEntity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class NanoBowItem extends BowItem {
	static final int NORMAL = 1;
	static final int RAPID = 2;
	static final int SPREAD = 3;
	static final int SNIPER = 4;
	static final int FLAME = 5;
	static final int EXPLOSIVE = 6;
	static final int[] CHARGE = { 300, 150, 400, 1000, 200, 800 };
	static final String[] MODE = { "normal", "rapidfire", "spread", "sniper", "flame", "explosive" };

	public NanoBowItem() {
		super(new Item.Settings().group(EnergyControl.ITEM_GROUP));
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
		if (!(entity instanceof PlayerEntity))
			return;

		CompoundTag nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInt("bowMode");
		
		PlayerEntity player = (PlayerEntity) entity;
		int charge = getMaxUseTime(stack) - remainingUseTicks;
		if (charge < 0)
			return;

		if (mode == RAPID)
			charge = charge * 2;
		float f = getPullProgress(charge);
		if (f < 0.1)
			return;

		if (!world.isClient) {
			TechArrowEntity arrow = new TechArrowEntity(world, player);
			arrow.setProperties(player, player.pitch, player.yaw, 0.0F, f * 3.0F, 1.0F);

			if (f == 1.5F)
				arrow.setCritical(true);

			int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
			if (j > 0)
				arrow.setDamage(arrow.getDamage() + j * 0.5D + 0.5D);
			if (mode == NORMAL && arrow.isCritical())
				j += 3;
			else if (mode == RAPID && arrow.isCritical())
				j += 1;
			else if (mode == SNIPER && arrow.isCritical())
				j += 8;
			if (j > 0)
				arrow.setDamage(arrow.getDamage() + j * 0.5D + 0.5D);
			/*
			 * if (IC2CA.nanoBowBoost > 0) arrow.setDamage(arrow.getDamage() +
			 * IC2CA.nanoBowBoost * 0.5D + 0.5D);
			 */

			int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
			if (mode == NORMAL && arrow.isCritical())
				k += 1;
			else if (mode == SNIPER && arrow.isCritical())
				k += 5;
			if (k > 0)
				arrow.setPunch(k);

			if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0)
				arrow.setOnFireFor(100);
			if (mode == FLAME && arrow.isCritical())
				arrow.setOnFireFor(2000);
			if (mode == EXPLOSIVE && arrow.isCritical())
				arrow.setExplosive(true);

			arrow.pickupType = ProjectileEntity.PickupPermission.DISALLOWED;

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
				if (arrow.isCritical()) {
					TechArrowEntity arrow2 = new TechArrowEntity(world, player);
					arrow2.setProperties(player, player.pitch + 2.0F, player.yaw, 0.0F, f * 3.0F, 1.0F);
					arrow2.setCritical(true);
					arrow2.pickupType = ProjectileEntity.PickupPermission.DISALLOWED;
					TechArrowEntity arrow3 = new TechArrowEntity(world, player);
					arrow3.setProperties(player, player.pitch - 2.0F, player.yaw, 0.0F, f * 3.0F, 1.0F);
					arrow3.setCritical(true);
					arrow3.pickupType = ProjectileEntity.PickupPermission.DISALLOWED;
					TechArrowEntity arrow4 = new TechArrowEntity(world, player);
					arrow4.setProperties(player, player.pitch, player.yaw + 2.0F, 0.0F, f * 3.0F, 1.0F);
					arrow4.setCritical(true);
					arrow4.pickupType = ProjectileEntity.PickupPermission.DISALLOWED;
					TechArrowEntity arrow5 = new TechArrowEntity(world, player);
					arrow5.setProperties(player, player.pitch, player.yaw - 2.0F, 0.0F, f * 3.0F, 1.0F);
					arrow5.setCritical(true);
					arrow5.pickupType = ProjectileEntity.PickupPermission.DISALLOWED;
					world.spawnEntity(arrow2);
					world.spawnEntity(arrow3);
					world.spawnEntity(arrow4);
					world.spawnEntity(arrow5);
				}
				break;
			}
		}
		world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
		player.incrementStat(Stats.USED.getOrCreateStat(this));
	}

	public static float getPullProgress(int charge) {
		float f = charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		return f > 1.5F ? 1.5F : f;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		CompoundTag nbt = ItemStackHelper.getOrCreateNbtData(stack);
		switch (nbt.getInt("bowMode")) {
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		CompoundTag nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInt("bowMode");
		if (!world.isClient && isModeSwitchKeyDown(player) && nbt.getByte("toggleTimer") == 0) {
			byte toggle = 10;
			nbt.putByte("toggleTimer", toggle);

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
			nbt.putInt("bowMode", mode);
			player.sendMessage(new TranslatableText("bow.energycontrol." + MODE[mode - 1]));
			//NetworkHelper.chatMessage(player, "info.modeenabled", 1, mode - 1);
			return TypedActionResult.fail(stack);
		}

		if (canUse(stack, CHARGE[mode - 1])) {
			player.setCurrentHand(hand);
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.fail(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		CompoundTag nbt = ItemStackHelper.getOrCreateNbtData(stack);
		byte toggle = nbt.getByte("toggleTimer");
		if (toggle > 0)
			nbt.putByte("toggleTimer", --toggle);
		int mode = nbt.getInt("bowMode");
		if (mode == 0)
			nbt.putInt("bowMode", 1);
	}

	@Override
	public void usageTick(World world, LivingEntity player, ItemStack stack, int remainingUseTicks) {
		CompoundTag nbt = ItemStackHelper.getOrCreateNbtData(stack);
		int mode = nbt.getInt("bowMode");
		if (mode == RAPID) {
			int j = getMaxUseTime(stack) - remainingUseTicks;
			if ((j >= 10) && (canUse(stack, CHARGE[RAPID - 1])))
				player.stopUsingItem();
		}
	}

	protected abstract void discharge(ItemStack stack, double amount, LivingEntity entity);
	
	protected abstract boolean canUse(ItemStack stack, double amount);
	
	protected abstract boolean isModeSwitchKeyDown(PlayerEntity player);

	@Override
	public Rarity getRarity(ItemStack itemstack) {
		return Rarity.UNCOMMON;
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
