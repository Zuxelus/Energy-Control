package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.reactor.IReactor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public class CrossModBase {

	public int getProfile() {
		return -1;
	}

	public ItemStack getItemStack(String name) {
		return ItemStack.EMPTY;
	}

	public ItemStack getChargedStack(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	public ICapabilityProvider initCapabilities(ItemStack stack) {
		return null;
	}

	public boolean isWrench(ItemStack stack) {
		return false;
	}

	public boolean isElectricItem(ItemStack stack) {
		return false;
	}

	public double dischargeItem(ItemStack stack, double needed) {
		return 0;
	}

	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getCardData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getInventoryData(TileEntity te) {
		return null;
	}

	public int getHeat(World world, BlockPos pos) {
		return -1;
	}

	public List<FluidInfo> getAllTanks(TileEntity te) {
		return null;
	}

	public ArrayList getHookValues(TileEntity te) {
		return null;
	}

	public void registerBlocks(Register<Block> event) { }

	public void registerItems(Register<Item> event) { }

	public void registerModels(ModelRegistryEvent event) { }

	public void registerTileEntities() {}

	public void loadOreInfo() { }
}
