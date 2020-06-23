package com.zuxelus.energycontrol.items;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.reactor.IReactor;
import ic2.core.block.TileEntityBarrel;
import ic2.core.block.generator.tileentity.TileEntityConversionGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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

		IReactor reactor = ReactorHelper.getReactorAround(world, pos);
		if (reactor == null)
			reactor = ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null) {
			messagePlayer(player, reactor);
			damage(stack, 1, player);
			return EnumActionResult.SUCCESS;
		}

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityBarrel) {
			int age = -1;
			int boozeAmount = 0;
			try {
				Field field = TileEntityBarrel.class.getDeclaredField("age");
				field.setAccessible(true);
				age = (int) field.get(te);
				field = TileEntityBarrel.class.getDeclaredField("boozeAmount");
				field.setAccessible(true);
				boozeAmount = (int) field.get(te);
			} catch (Throwable t) { }
			if (age >= 0)
				NetworkHelper.chatMessage(player, age + " / " + ((TileEntityBarrel) te).timeNedForRum(boozeAmount));
		}
		return EnumActionResult.PASS;
	}

	protected void messagePlayer(EntityPlayer entityplayer, IReactor reactor) {
		NetworkHelper.chatMessage(entityplayer, I18n.format("msg.ec.Thermo", reactor.getHeat()));
	}

	protected void damage(ItemStack itemstack, int i, EntityPlayer entityplayer) {
		itemstack.damageItem(10, entityplayer);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
