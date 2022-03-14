package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.IC2ReactorHelper;

import ic2.api.reactor.IReactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemThermometer extends Item {

	public ItemThermometer() {
		super();
		setMaxDamage(102);
		setMaxStackSize(1);
		setCreativeTab(EnergyControl.creativeTab);
	}

	protected boolean canTakeDamage(ItemStack itemstack, int i) {
		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (!(player instanceof EntityPlayerMP))
			return EnumActionResult.PASS;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty())
			return EnumActionResult.PASS;
		if (!canTakeDamage(stack, 2))
			return EnumActionResult.PASS;

		IReactor reactor = IC2ReactorHelper.getReactorAround(world, pos);
		if (reactor == null)
			reactor = IC2ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null) {
			messagePlayer(player, reactor);
			damage(stack, 1, player);
			return EnumActionResult.SUCCESS;
		}

		//TileEntity te = world.getTileEntity(pos);
		//CrossModLoader.getCrossMod(ModIDs.IC2).showBarrelInfo(player, te);
		return EnumActionResult.PASS;
	}

	protected void messagePlayer(EntityPlayer player, IReactor reactor) {
		player.sendMessage(new TextComponentTranslation("msg.ec.Thermo", reactor.getHeat()));
	}

	protected void damage(ItemStack stack, int i, EntityPlayer player) {
		stack.damageItem(10, player);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
