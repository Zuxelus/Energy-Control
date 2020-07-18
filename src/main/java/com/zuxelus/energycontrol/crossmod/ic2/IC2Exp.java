package com.zuxelus.energycontrol.crossmod.ic2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemAFB;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBarrel;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Fluids;
import ic2.core.block.comp.Fluids.InternalFluidTank;
import ic2.core.block.generator.tileentity.*;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.block.kineticgenerator.tileentity.*;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.reactor.tileentity.*;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.profile.ProfileManager;
import ic2.core.profile.Version;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class IC2Exp extends CrossIC2 {

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack.isEmpty())
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
	public int getProfile() {
		return ProfileManager.selected.style == Version.OLD ? 1 : 0; 
	}

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("upgrade", "transformer");
		case "energy_storage":
			return IC2Items.getItem("upgrade", "energy_storage");
		case "machine":
			return IC2Items.getItem("resource", "machine");
		case "mfsu":
			return IC2Items.getItem("te","mfsu");
		case "circuit":
			return IC2Items.getItem("crafting","circuit");
		}
		return ItemStack.EMPTY;
	}

	@Override
	public Item getItem(String name) {
		switch (name) {
		case "afb":
			return new ItemAFB();
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
		return !stack.isEmpty() && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public boolean isSteamReactor(TileEntity par1) {
		return false;
	}

	@Override
	public ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IEnergyStorage) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
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
	public ItemStack getGeneratorCard(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityBaseGenerator || te instanceof TileEntityConversionGenerator) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		if (te instanceof TileEntityElectricKineticGenerator || te instanceof TileEntityManualKineticGenerator
				|| te instanceof TileEntitySteamKineticGenerator || te instanceof TileEntityStirlingKineticGenerator
				|| te instanceof TileEntityWaterKineticGenerator || te instanceof TileEntityWindKineticGenerator) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR_KINETIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		if (te instanceof TileEntityHeatSourceInventory) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR_HEAT);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
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
				Energy energy = ((TileEntityBaseGenerator) te).getComponent(Energy.class);
				tag.setDouble("storage", energy.getEnergy());
				tag.setDouble("maxStorage", energy.getCapacity());
				if (te instanceof TileEntitySolarGenerator) {
					float light = ((TileEntitySolarGenerator)te).skyLight;
					active = light > 0 && energy.getEnergy() < energy.getCapacity();
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
						if (!((TileEntityRTGenerator) te).fuelSlot.isEmpty(i))
							counter++;
					tag.setInteger("items", counter);
					if (counter == 0 || energy.getEnergy() >= energy.getCapacity()) {
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
						Field field = TileEntityWaterGenerator.class.getDeclaredField("energyMultiplier");
						field.setAccessible(true);
						tag.setDouble("production", (Double) field.get(te) * ((TileEntityWaterGenerator) te).water / 100);
						return tag;
					}
				}
				if (active) {
					Field field = TileEntityBaseGenerator.class.getDeclaredField("production");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				} else
					tag.setDouble("production", 0);
				return tag;
			}


			if (te instanceof TileEntityConversionGenerator) {
				if (active) {
					Field field = TileEntityConversionGenerator.class.getDeclaredField("lastProduction");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				} else
					tag.setDouble("production", 0);
				if (te instanceof TileEntityStirlingGenerator) {
					tag.setInteger("type", 2);
					Field field = TileEntityStirlingGenerator.class.getDeclaredField("productionpeerheat");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				if (te instanceof TileEntityKineticGenerator) {
					tag.setInteger("type", 3);
					Energy energy = ((TileEntityKineticGenerator) te).getComponent(Energy.class);
					tag.setDouble("storage", energy.getEnergy());
					tag.setDouble("maxStorage", energy.getCapacity());
					Field field = TileEntityKineticGenerator.class.getDeclaredField("euPerKu");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			
			if (te instanceof TileEntityManualKineticGenerator) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityManualKineticGenerator)te).currentKU);
				tag.setDouble("maxStorage", ((TileEntityManualKineticGenerator)te).maxKU);
				return tag;
			}
			
			Boolean active = ((TileEntityBlock) te).getActive();
			if (te instanceof TileEntityWindKineticGenerator) {
				TileEntityWindKineticGenerator entity = ((TileEntityWindKineticGenerator) te);
				tag.setInteger("type", 2);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWindKineticGenerator.class.getDeclaredField("windStrength");
				field.setAccessible(true);
				tag.setDouble("wind", (Double) field.get(te));
				tag.setDouble("multiplier", entity.getEfficiency() * entity.outputModifier);
				tag.setInteger("height", entity.getPos().getY());
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage()));
				return tag;
			}
			if (te instanceof TileEntityWaterKineticGenerator) {
				TileEntityWaterKineticGenerator entity = ((TileEntityWaterKineticGenerator) te);
				tag.setInteger("type", 2);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWaterKineticGenerator.class.getDeclaredField("waterFlow");
				field.setAccessible(true);
				tag.setDouble("wind", (Integer) field.get(te));
				field = TileEntityWaterKineticGenerator.class.getDeclaredField("outputModifier");
				field.setAccessible(true);
				tag.setDouble("multiplier", (double) (Float) field.get(te));
				tag.setInteger("height", entity.getPos().getY());
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage()));
				return tag;
			}
			if (te instanceof TileEntityStirlingKineticGenerator) {
				//TODO
			}
		} catch (Throwable t) {
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			Boolean active = ((TileEntityBlock)te).getActive();
			tag.setBoolean("active", active);
			if (te instanceof TileEntityHeatSourceInventory) {
				tag.setInteger("type", 1);
				if (active)
					tag.setInteger("output", ((TileEntityHeatSourceInventory)te).gettransmitHeat());
				else
					tag.setInteger("output", 0);
				if (te instanceof TileEntityElectricHeatGenerator) {
					Energy energy = ((TileEntityHeatSourceInventory) te).getComponent(Energy.class);
					tag.setDouble("storage", energy.getEnergy());
					tag.setDouble("maxStorage", energy.getCapacity());
					int count = 0;
					for (ItemStack stack : ((TileEntityElectricHeatGenerator) te).coilSlot)
						if (!stack.isEmpty())
							count++;
					tag.setInteger("coils", count);
				}
				if (te instanceof TileEntityLiquidHeatExchanger) {
					Fluids fluid = ((TileEntityLiquidHeatExchanger) te).getComponent(Fluids.class);
					Iterable<InternalFluidTank> tanks = fluid.getAllTanks();
					InternalFluidTank tank = tanks.iterator().next();
					tag.setDouble("storage", tank.getFluidAmount());
					tag.setDouble("maxStorage", tank.getCapacity());
					int count = 0;
					for (ItemStack stack : ((TileEntityLiquidHeatExchanger) te).heatexchangerslots)
						if (!stack.isEmpty())
							count++;
					tag.setInteger("coils", count);
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	private boolean isActive(TileEntity te) {
		if (te instanceof TileEntityGeoGenerator || te instanceof TileEntityConversionGenerator || te instanceof TileEntitySolarGenerator)
			return ((TileEntityBlock)te).getActive();
		if (te instanceof TileEntityBaseGenerator)
			return ((TileEntityBaseGenerator)te).isConverting();
		return false;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (!(te instanceof TileEntityBlock))
			return null;
		
		if (!((TileEntityBlock)te).hasComponent(Fluids.class))
			return null;

		Fluids fluid = ((TileEntityBlock)te).getComponent(Fluids.class);
		
		List<IFluidTank> result = new ArrayList<>();
		for (FluidTank tank: fluid.getAllTanks())
			result.add(tank);
		
		return result;
	}

	@Override
	public ItemStack getReactorCard(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric) {
			BlockPos position = ReactorHelper.getTargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		} else if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = ReactorHelper.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR5X5);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getLiquidAdvancedCard(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;
		
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = ReactorHelper.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return ItemStack.EMPTY;
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
			ItemStack rStack = inventory.getStackInSlot(i);
			if (!rStack.isEmpty())
				dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(rStack));
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
	public CardState updateCardReactor5x5(World world, ICardReader reader, BlockPos target) {
		IReactor reactor = ReactorHelper.getReactorAt(world, target);
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
			ItemStack rStack = inventory.getStackInSlot(i);
			if (!rStack.isEmpty())
				dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(rStack));
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
				NetworkHelper.chatMessage(player, age + " / " + ((TileEntityBarrel) te).timeNedForRum(boozeAmount));
		}
	}
}
