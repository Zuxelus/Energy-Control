package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.IEnergyStorage;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class CrossIC2Classic extends CrossModBase {

	@Override
	public String getModType() {
		return "IC2Classic";
	}

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("transformerUpgrade");
		case "energy_storage":
			return IC2Items.getItem("energyStorageUpgrade");
		case "machine":
			return IC2Items.getItem("machine");
		case "circuit":
			return IC2Items.getItem("circuit");
		}
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		return stack;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack == null)
			return 0;
		
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);
		
		return 0;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IEnergyStorage) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyStorage storage = (IEnergyStorage) te;
			tag.setInteger("type", 1);
			tag.setDouble("storage", storage.getStored());
			tag.setDouble("maxStorage", storage.getCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public ItemStack getReactorCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IReactor || te instanceof IReactorChamber) {
			ChunkCoordinates position = ReactorHelper.getTargetCoordinates(world, x, y, z);
			if (position != null) {
				ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(card, x, y, z);
				return card;
			}
		}
		return null;
	}

	@Override
	public CardState updateCardReactor(World world, ICardReader reader, IReactor reactor) {
		if (reactor == null)
			return CardState.NO_TARGET;
	
		reader.setInt("heat", reactor.getHeat());
		reader.setInt("maxHeat", reactor.getMaxHeat());
		reader.setBoolean("reactorPoweredB", reactor.produceEnergy());
		reader.setInt("output", (int) Math.round(reactor.getReactorEUEnergyOutput()));

		int dmgLeft = 0;
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				ItemStack stack = reactor.getItemAt(x, y);
				if (stack != null)
					dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(stack));
			}
		}
		reader.setInt("timeLeft", dmgLeft * reactor.getTickRate() / 20);
		return CardState.OK;
	}

	@Override
	public FluidTankInfo[] getAllTanks(TileEntity te) {
		if (!(te instanceof IFluidHandler))
			return null;

		return ((IFluidHandler) te).getTankInfo(ForgeDirection.UP);
	}
}
