package com.zuxelus.energycontrol.crossmod.ic2;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemAFB;
import com.zuxelus.energycontrol.items.ItemAFSUUpgradeKit;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ReactorHelper;
import com.zuxelus.zlib.network.NetworkHelper;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.BlockMultiID;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBarrel;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.generator.block.BlockGenerator;
import ic2.core.block.generator.tileentity.*;
import ic2.core.block.heatgenerator.tileentity.*;
import ic2.core.block.kineticgenerator.tileentity.*;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import ic2.core.block.reactor.tileentity.*;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class IC2Exp extends CrossIC2 {

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack == null)
			return 0;
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium || item instanceof ItemReactorLithiumCell || item instanceof ItemReactorMOX)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);
		// Coaxium Mod
		if (item.getClass().getName() == "com.sm.FirstMod.items.ItemCoaxiumRod" || item.getClass().getName() == "com.sm.FirstMod.items.ItemCesiumRod")
			return stack.getMaxDamage() - getCoaxiumDamage(stack);
		return 0;
	}

	public int getCoaxiumDamage(ItemStack stack) {
		if (!stack.hasTagCompound())
			return 0;
		return stack.getTagCompound().getInteger("fuelRodDamage");
	}

	@Override
	public IC2Type getType() {
		return IC2Type.EXP;
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
		case "mfsu":
			return IC2Items.getItem("mfsu");
		case "circuit":
			return IC2Items.getItem("circuit");
		}
		return null;
	}

	@Override
	public Item getItem(String name) {
		switch (name) {
		case "afb":
			return new ItemAFB();
		case "afsu_upgrade_kit":
			return new ItemAFSUUpgradeKit();
		default:
			return null;
		}
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
	public boolean isSteamReactor(TileEntity par1) {
		return false;
	}

	@Override
	public ItemStack getEnergyCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IEnergyStorage) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
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
	public ItemStack getGeneratorCard(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (!(block instanceof BlockMultiID))
			return null;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityBaseGenerator || te instanceof TileEntitySemifluidGenerator
				|| te instanceof TileEntityStirlingGenerator || te instanceof TileEntityGeoGenerator
				|| te instanceof TileEntityKineticGenerator || te instanceof TileEntitySteamGenerator) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		if (te instanceof TileEntityElectricKineticGenerator || te instanceof TileEntityManualKineticGenerator
				|| te instanceof TileEntitySteamKineticGenerator || te instanceof TileEntityStirlingKineticGenerator
				|| te instanceof TileEntityWaterKineticGenerator || te instanceof TileEntityWindKineticGenerator) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR_KINETIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		if (te instanceof TileEntityHeatSourceInventory) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR_HEAT);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
	}
	
	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			Boolean active = isActive(te);
			tag.setBoolean("active", active);
			tag.setString("euType", "EU");
			if (te instanceof TileEntityBaseGenerator) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityBaseGenerator) te).storage);
				tag.setDouble("maxStorage", ((TileEntityBaseGenerator) te).maxStorage);
				if (te instanceof TileEntitySolarGenerator) {
					Field field = TileEntitySolarGenerator.class.getDeclaredField("solarbasevalue");
					field.setAccessible(true);
					double solarbasevalue = (Double) field.get(te);
					double light = ((TileEntitySolarGenerator)te).sunIsVisible ? solarbasevalue : 0.0D; //((TileEntitySolarGenerator) te).solarbasevalue : 0.0D;
					active = light > 0 && ((TileEntityBaseGenerator) te).storage < ((TileEntityBaseGenerator) te).maxStorage;
					tag.setBoolean("active", active);
					if (active)
						tag.setDouble("production", (double) light);
					else
						tag.setDouble("production", 0);
					return tag;
				}
				if (te instanceof TileEntityRTGenerator) {
					tag.setInteger("type", 4);
					int counter = 0;
					for (int i = 0; i < ((TileEntityRTGenerator) te).fuelSlot.size(); i++)
						if (((TileEntityRTGenerator) te).fuelSlot.get(i) != null)
							counter++;
					tag.setInteger("items", counter);
					if (counter == 0 || ((TileEntityBaseGenerator) te).storage >= ((TileEntityBaseGenerator) te).maxStorage) {
						tag.setBoolean("active", false);
						tag.setDouble("production", 0);
						return tag;
					}
					tag.setBoolean("active", true);
					Field field = TileEntityRTGenerator.class.getDeclaredField("efficiency");
					field.setAccessible(true);
					tag.setDouble("multiplier", (double) (float) field.get(te));
					tag.setDouble("production", (double) Math.pow(2.0D, (counter - 1)) * (float) field.get(te));
					return tag;
				}
				if (te instanceof TileEntityWaterGenerator) {
					active = ((TileEntityWaterGenerator)te).water > 0 || ((TileEntityWaterGenerator)te).fuel > 0;
					tag.setBoolean("active", active);
					if (((TileEntityWaterGenerator) te).fuel <= 0) {
						Field field = TileEntityWaterGenerator.class.getDeclaredField("waterbasevalue");
						field.setAccessible(true);
						double waterbasevalue = (Float) field.get(te);
						tag.setDouble("production", waterbasevalue * ((TileEntityWaterGenerator) te).water / 100);
					}
					return tag;
				}
				if (active)
					tag.setDouble("production", ((TileEntityBaseGenerator) te).production);
				else
					tag.setDouble("production", 0);
				return tag;
			}

			if (te instanceof TileEntitySemifluidGenerator) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntitySemifluidGenerator) te).storage);
				Field field = TileEntitySemifluidGenerator.class.getDeclaredField("maxStorage");
				field.setAccessible(true);
				tag.setDouble("maxStorage", (short) field.get(te));
				field = TileEntitySemifluidGenerator.class.getDeclaredField("production");
				field.setAccessible(true);
				tag.setDouble("production", (Double) field.get(te));
				tag.setBoolean("active", ((TileEntitySemifluidGenerator) te).isConverting());
				return tag;
			}

			if (te instanceof TileEntityStirlingGenerator) {
				tag.setInteger("type", 3);
				tag.setDouble("storage", ((TileEntityStirlingGenerator) te).EUstorage);
				tag.setDouble("maxStorage", ((TileEntityStirlingGenerator) te).maxEUStorage);
				tag.setDouble("production", ((TileEntityStirlingGenerator) te).production);
				tag.setDouble("multiplier", ((TileEntityStirlingGenerator) te).productionpeerheat);
				return tag;
			}

			if (te instanceof TileEntityGeoGenerator) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityGeoGenerator) te).storage);
				tag.setDouble("maxStorage", ((TileEntityGeoGenerator) te).maxStorage);
				active = ((TileEntityGeoGenerator) te).isConverting();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntityGeoGenerator) te).production);
				else
					tag.setDouble("production", 0);
				return tag;
			}

			if (te instanceof TileEntityKineticGenerator) {
				tag.setInteger("type", 3);
				active = ((TileEntityKineticGenerator)te).getActive();
				tag.setBoolean("active", active);
				tag.setDouble("storage", ((TileEntityKineticGenerator)te).EUstorage);
				Field field = TileEntityKineticGenerator.class.getDeclaredField("maxEUStorage");
				field.setAccessible(true);
				tag.setDouble("maxStorage", (double) (int) field.get(te));
				tag.setDouble("production", ((TileEntityKineticGenerator) te).getproduction());
				field = TileEntityKineticGenerator.class.getDeclaredField("productionpeerkineticunit");
				field.setAccessible(true);
				tag.setDouble("multiplier", (Double) field.get(te));
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			Boolean active = ((TileEntityBlock) te).getActive();
			if (te instanceof TileEntityElectricKineticGenerator) {
				TileEntityElectricKineticGenerator entity = ((TileEntityElectricKineticGenerator) te);
				tag.setInteger("type", 1);
				int counter = 0;
				for (int i = 0; i < ((TileEntityElectricKineticGenerator) te).slotMotor.size(); i++)
					if (((TileEntityElectricKineticGenerator) te).slotMotor.get(i) != null)
						counter++;
				tag.setInteger("items", counter);
				if (counter == 0)
					active = false;
				tag.setBoolean("active", active);
				tag.setDouble("output", entity.getMaxKU());
				tag.setDouble("storage", ((TileEntityElectricKineticGenerator)te).ku);
				tag.setDouble("maxStorage", ((TileEntityElectricKineticGenerator)te).maxKU);
				//Energy energy = (Energy) ((TileEntityElectricKineticGenerator) te).getComponent("energy");
				Field field = TileEntityElectricKineticGenerator.class.getDeclaredField("energy");
				field.setAccessible(true);
				Energy energy = (Energy) field.get(te);
				tag.setDouble("energy", energy.getEnergy());
				tag.setDouble("maxEnergy", energy.getCapacity());
				tag.setDouble("items", counter);
				field = TileEntityElectricKineticGenerator.class.getDeclaredField("kuPerEU");
				field.setAccessible(true);
				tag.setDouble("multiplier", (double) (float) field.get(te));
				return tag;
			}
			if (te instanceof TileEntityManualKineticGenerator) {
				tag.setInteger("type", 2);
				tag.setDouble("storage", ((TileEntityManualKineticGenerator)te).currentKU);
				tag.setDouble("maxStorage", ((TileEntityManualKineticGenerator)te).maxKU);
				return tag;
			}
			if (te instanceof TileEntitySteamKineticGenerator) {
				tag.setInteger("type", 3);
				tag.setBoolean("active", active);
				tag.setDouble("output", ((TileEntitySteamKineticGenerator) te).gethUoutput());
				Field field = TileEntitySteamKineticGenerator.class.getDeclaredField("steamTank");
				field.setAccessible(true);
				FluidTank tank = (FluidTank) field.get(te);
				tag.setDouble("storage", tank.getFluidAmount());
				tag.setDouble("maxStorage", tank.getCapacity());
				return tag;
			}
			if (te instanceof TileEntityStirlingKineticGenerator) {
				tag.setInteger("type", 4);
				tag.setBoolean("active", ((TileEntityBlock)te).getActive());
				Field field = TileEntityStirlingKineticGenerator.class.getDeclaredField("kUBuffer");
				field.setAccessible(true);
				tag.setDouble("storage", (double) (int) field.get(te));
				field = TileEntityStirlingKineticGenerator.class.getDeclaredField("maxkUBuffer");
				field.setAccessible(true);
				tag.setDouble("maxStorage", (double) (int) field.get(te));
				field = TileEntityStirlingKineticGenerator.class.getDeclaredField("heatbuffer");
				field.setAccessible(true);
				tag.setDouble("energy", (double) (int) field.get(te));
				field = TileEntityStirlingKineticGenerator.class.getDeclaredField("maxHeatbuffer");
				field.setAccessible(true);
				tag.setDouble("maxEnergy", (double) (int) field.get(te));
				tag.setDouble("multiplier", 3);
				return tag;
			}
			if (te instanceof TileEntityWaterKineticGenerator) {
				TileEntityWaterKineticGenerator entity = ((TileEntityWaterKineticGenerator) te);
				tag.setInteger("type", 5);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWaterKineticGenerator.class.getDeclaredField("waterFlow");
				field.setAccessible(true);
				tag.setDouble("wind", (Integer) field.get(te));
				field = TileEntityWaterKineticGenerator.class.getDeclaredField("outputModifier");
				field.setAccessible(true);
				tag.setDouble("multiplier", (double) (Float) field.get(te));
				tag.setInteger("height", entity.yCoord);
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage()));
				return tag;
			}
			if (te instanceof TileEntityWindKineticGenerator) {
				TileEntityWindKineticGenerator entity = ((TileEntityWindKineticGenerator) te);
				tag.setInteger("type", 6);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWindKineticGenerator.class.getDeclaredField("windStrength");
				field.setAccessible(true);
				tag.setDouble("wind", (Double) field.get(te));
				tag.setDouble("multiplier", entity.getefficiency() * entity.outputModifier);
				tag.setInteger("height", entity.yCoord);
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage()));
				return tag;
			}
		} catch (Throwable t) {
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		if (!(te instanceof TileEntityHeatSourceInventory))
			return null;

		try {
			NBTTagCompound tag = new NBTTagCompound();
			int buffer = ((TileEntityHeatSourceInventory) te).getHeatBuffer();
			Boolean active = /*buffer > 0 ? false : */((TileEntityHeatSourceInventory)te).getActive();
			tag.setBoolean("active", active);
			if (active)
				tag.setInteger("output", ((TileEntityHeatSourceInventory)te).gettransmitHeat());
			else
				tag.setInteger("output", 0);
			tag.setInteger("energy", buffer);

			if (te instanceof TileEntityElectricHeatGenerator) {
				tag.setInteger("type", 1);
				//Energy energy = (Energy) ((TileEntityElectricHeatGenerator) te).getComponent("energy");
				Field field = TileEntityElectricHeatGenerator.class.getDeclaredField("energy");
				field.setAccessible(true);
				Energy energy = (Energy) field.get(te);
				tag.setDouble("storage", energy.getEnergy());
				tag.setDouble("maxStorage", energy.getCapacity());
				int counter = 0;
				for (int i = 0; i < ((TileEntityElectricHeatGenerator) te).CoilSlot.size(); i++)
					if (((TileEntityElectricHeatGenerator) te).CoilSlot.get(i) != null)
						counter++;
				tag.setInteger("items", counter);
			}
			if (te instanceof TileEntityFluidHeatGenerator) {
				tag.setInteger("type", 2);
				FluidTank tank = ((TileEntityFluidHeatGenerator) te).getFluidTank();
				tag.setDouble("storage", tank.getFluidAmount());
				tag.setDouble("maxStorage", tank.getCapacity());
			}
			if (te instanceof TileEntityLiquidHeatExchanger) {
				tag.setInteger("type", 3);
				FluidTank tank = ((TileEntityLiquidHeatExchanger) te).inputTank;
				tag.setDouble("storage", tank.getFluidAmount());
				tag.setDouble("maxStorage", tank.getCapacity());
				int counter = 0;
				for (int i = 0; i < ((TileEntityLiquidHeatExchanger) te).heatexchangerslots.size(); i++)
					if (((TileEntityLiquidHeatExchanger) te).heatexchangerslots.get(i) != null)
						counter++;
				tag.setInteger("items", counter);
			}
			if (te instanceof TileEntityRTHeatGenerator) {
				tag.setInteger("type", 4);
				int counter = 0;
				for (int i = 0; i < ((TileEntityRTHeatGenerator) te).fuelSlot.size(); i++)
					if (((TileEntityRTHeatGenerator) te).fuelSlot.get(i) != null)
						counter++;
				tag.setInteger("items", counter);
				tag.setDouble("multiplier", (double) ((TileEntityRTHeatGenerator) te).outputMultiplier);
			}
			if (te instanceof TileEntitySolidHeatGenerator) {
				tag.setInteger("type", 5);
				Field field = TileEntitySolidHeatGenerator.class.getDeclaredField("heatbuffer");
				field.setAccessible(true);
				tag.setInteger("energy", (int) field.get(te) + ((TileEntitySolidHeatGenerator) te).getHeatBuffer());
			}
			return tag;
		} catch (Throwable t) { }
		return null;
	}

	private boolean isActive(TileEntity te) {
		if (te instanceof TileEntityGeoGenerator || te instanceof TileEntityStirlingGenerator || te instanceof TileEntitySolarGenerator)
			return ((TileEntityBlock)te).getActive();
		if (te instanceof TileEntityBaseGenerator)
			return ((TileEntityBaseGenerator)te).isConverting();
		return false;
	}

	@Override
	public FluidTankInfo[] getAllTanks(TileEntity te) {
		if (!(te instanceof IFluidHandler))
			return null;

		return ((IFluidHandler) te).getTankInfo(ForgeDirection.UP);
	}

	@Override
	public ItemStack getReactorCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric) {
			ChunkCoordinates position = ReactorHelper.getTargetCoordinates(world, x, y, z);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
				return sensorLocationCard;
			}
		} else if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			ChunkCoordinates position = ReactorHelper.get5x5TargetCoordinates(world, x, y, z);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR5X5);
				ItemStackHelper.setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
				return sensorLocationCard;
			}
		}
		return null;
	}

	@Override
	public ItemStack getLiquidAdvancedCard(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (!(block instanceof BlockTileEntity))
			return null;
		
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			ChunkCoordinates position = ReactorHelper.get5x5TargetCoordinates(world, x, y, z);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
				ItemStackHelper.setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
				return sensorLocationCard;
			}
		}
		return null;
	}

	@Override
	public CardState updateCardReactor(World world, ICardReader reader, IReactor reactor) {
		reader.setInt("heat", reactor.getHeat());
		reader.setInt("maxHeat", reactor.getMaxHeat());
		reader.setBoolean("reactorPoweredB", reactor.produceEnergy());
		reader.setInt("output", (int) Math.round(reactor.getReactorEUEnergyOutput()));
		boolean isSteam = ReactorHelper.isSteam(reactor);
		reader.setBoolean("isSteam", isSteam);

		IInventory inventory = (IInventory) reactor;
		int slotCount = inventory.getSizeInventory();
		int dmgLeft = 0;
		for (int i = 0; i < slotCount; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(stack));
		}

		int timeLeft = 0;
		//Classic has a Higher Tick rate for Steam generation but damage tick rate is still the same...
		if (isSteam) {
			timeLeft = dmgLeft;
		} else
			timeLeft = dmgLeft * reactor.getTickRate() / 20;
		reader.setInt("timeLeft", timeLeft);
		return CardState.OK;
	}

	@Override
	public CardState updateCardReactor5x5(World world, ICardReader reader, int x, int y, int z) {
		IReactor reactor = ReactorHelper.getReactorAt(world, x, y, z);
		if (reactor == null || !(reactor instanceof TileEntityNuclearReactorElectric))
			return CardState.NO_TARGET;
		
		reader.setInt("heat", reactor.getHeat());
		reader.setInt("maxHeat", reactor.getMaxHeat());
		reader.setBoolean("reactorPowered", reactor.produceEnergy());
		reader.setInt("output", ((TileEntityNuclearReactorElectric)reactor).EmitHeat);

		IInventory inventory = (IInventory) reactor;
		int slotCount = inventory.getSizeInventory();
		int dmgLeft = 0;
		for (int i = 0; i < slotCount; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(stack));
		}

		int timeLeft = dmgLeft * reactor.getTickRate() / 20;
		reader.setInt("timeLeft", timeLeft);
		return CardState.OK;
	}

	@Override
	public void showBarrelInfo(EntityPlayer player, TileEntity te) {
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
				player.addChatMessage(new ChatComponentText(age + " / " + ((TileEntityBarrel) te).timeNedForRum(boozeAmount)));
		}
	}
}
