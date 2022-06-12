package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardThermalExpansion;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.items.kits.ItemKitThermalExpansion;
import com.zuxelus.energycontrol.utils.FluidInfo;

import cofh.core.block.TileNameable;
import cofh.core.block.TilePowered;
import cofh.core.fluid.FluidTankCore;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.thermalexpansion.block.device.TileFluidBuffer;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.block.dynamo.TileDynamoCompression;
import cofh.thermalexpansion.block.dynamo.TileDynamoMagmatic;
import cofh.thermalexpansion.block.machine.TileBrewer;
import cofh.thermalexpansion.block.machine.TileInsolator;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.block.machine.TileRefinery;
import cofh.thermalexpansion.block.machine.TileSmelter;
import cofh.thermalexpansion.item.ItemAugment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.IFluidTank;

public class CrossThermalExpansion extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TilePowered) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyStorage storage = ((TilePowered) te).getEnergyStorage();
			if (storage != null) {
				tag.setString("euType", "RF");
				tag.setDouble("storage", storage.getEnergyStored());
				tag.setDouble("maxStorage", storage.getMaxEnergyStored());
				return tag;
			}
		}
		return null;
	}

	/*@Override
	public ItemStack getGeneratorCard(TileEntity te) {
		if (te instanceof TileDynamoBase) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "RF");
			if (te instanceof TileDynamoBase) {
				tag.setInteger("type", 1);
				IEnergyStorage storage = ((TileDynamoBase)te).getEnergyStorage();
				tag.setDouble("storage", storage.getEnergyStored());
				tag.setDouble("maxStorage", storage.getMaxEnergyStored());
				int production = ((TileDynamoBase) te).getInfoEnergyPerTick();
				tag.setBoolean("active", ((TileDynamoBase) te).isActive);
				tag.setDouble("production", production);
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}*/

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileDynamoCompression) {
			IFluidTank tank0 = ((TileDynamoCompression) te).getTank(0);
			IFluidTank tank1 = ((TileDynamoCompression) te).getTank(1);
			if (tank0 != null && tank1 != null) {
				result.add(new FluidInfo(tank0));
				result.add(new FluidInfo(tank1));
				return result;
			}
		}
		if (te instanceof TileDynamoMagmatic) {
			IFluidTank tank0 = ((TileDynamoMagmatic) te).getTank(0);
			IFluidTank tank1 = ((TileDynamoMagmatic) te).getTank(1);
			if (tank0 != null && tank1 != null) {
				result.add(new FluidInfo(tank0));
				result.add(new FluidInfo(tank1));
				return result;
			}
		}
		if (te instanceof TileRefinery) {
			IFluidTank tank0 = ((TileRefinery) te).getTank(0);
			IFluidTank tank1 = ((TileRefinery) te).getTank(1);
			if (tank0 != null && tank1 != null) {
				result.add(new FluidInfo(tank0));
				result.add(new FluidInfo(tank1));
				return result;
			}
		}
		if (te instanceof TileBrewer) {
			IFluidTank tank0 = ((TileBrewer) te).getTank(0);
			IFluidTank tank1 = ((TileBrewer) te).getTank(1);
			if (tank0 != null && tank1 != null) {
				result.add(new FluidInfo(tank0));
				result.add(new FluidInfo(tank1));
				return result;
			}
		}
		if (te instanceof TileFluidBuffer) {
			IFluidTank tank0 = ((TileFluidBuffer) te).getTank(0);
			IFluidTank tank1 = ((TileFluidBuffer) te).getTank(1);
			IFluidTank tank2 = ((TileFluidBuffer) te).getTank(2);
			if (tank0 != null && tank1 != null && tank2 != null) {
				result.add(new FluidInfo(tank0));
				result.add(new FluidInfo(tank1));
				result.add(new FluidInfo(tank2));
				return result;
			}
		}
		if (te instanceof TileNameable) {
			IFluidTank tank = ((TileNameable) te).getTank();
			if (tank != null) {
				result.add(new FluidInfo(tank));
				return result;
			}
		}
		if (te instanceof TileDynamoBase) {
			IFluidTank tank = ((TileDynamoBase) te).getTank(0);
			if (tank != null) {
				result.add(new FluidInfo(tank));
				return result;
			}
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		try {
			if (te instanceof TileMachineBase) {
				NBTTagCompound tag = new NBTTagCompound();
				TileMachineBase machine = (TileMachineBase) te;
				tag.setInteger("type", 1);
				tag.setBoolean("active", machine.isActive);
				tag.setInteger("usage", machine.getInfoEnergyPerTick());
				tag.setString("rsmode", machine.getControl().name());
				IEnergyStorage storage = machine.getEnergyStorage();
				if (storage != null) {
					tag.setInteger("storage", storage.getEnergyStored());
					tag.setInteger("maxStorage", storage.getMaxEnergyStored());
				}
				Field field = TileMachineBase.class.getDeclaredField("energyConfig");
				field.setAccessible(true);
				EnergyConfig energyConfig = (EnergyConfig) field.get(machine);
				tag.setString("power", String.format("%s-%s RF/t", energyConfig.minPower, energyConfig.maxPower));
				String augmentation = "";
				if (machine.getAugmentSlots().length == 0)
					augmentation = StringHelper.localize("info.cofh.upgradeRequired");
				else
					for (int i = 0; i < machine.getAugmentSlots().length; i++) {
						ItemStack stack = machine.getAugmentSlots()[i]; 
						if (!stack.isEmpty() && stack.getItem() instanceof ItemAugment) {
							ItemAugment item = (ItemAugment) stack.getItem();
							if (augmentation.equals(""))
								augmentation = StringHelper.localize(String.format("item.thermalexpansion.augment.%s.name", item.getAugmentIdentifier(stack)));
							else
								augmentation = augmentation + "," + StringHelper.localize(String.format("item.thermalexpansion.augment.%s.name", item.getAugmentIdentifier(stack)));
						}
					}
				tag.setString("augmentation", augmentation);
				if (te instanceof TileSmelter) {
					tag.setInteger("type", 2);
					if (((TileSmelter) machine).lockPrimary)
						tag.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.smelter.modeLocked"));
					else
						tag.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.smelter.modeUnlocked"));
				}
				if (te instanceof TileInsolator) {
					tag.setInteger("type", 3);
					if (((TileInsolator) machine).lockPrimary)
						tag.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.insolator.modeLocked"));
					else
						tag.setString("lock", StringHelper.localize("gui.thermalexpansion.machine.insolator.modeUnlocked"));
					FluidTankCore tank = ((TileInsolator) machine).getTank();
					tag.setString("water", String.format("%s / %s mB", tank.getFluidAmount(), tank.getCapacity()));
				}
				return tag;
			}
		} catch (Throwable t) {}
		return null;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitThermalExpansion::new);
		ItemCardMain.register(ItemCardThermalExpansion::new);
	}
}
