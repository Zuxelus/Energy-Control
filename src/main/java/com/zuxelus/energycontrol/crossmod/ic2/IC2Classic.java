package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactor;
import ic2.core.block.generator.tileentity.TileEntityNuclearReactorSteam;
import ic2.core.block.generator.tileentity.TileEntityReactorChamber;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class IC2Classic extends CrossIC2 {
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
	public boolean isSteamReactor(TileEntity par1) {
		return par1 != null && par1 instanceof IReactor;
	}

	@Override
	public IC2Type getType() {
		return IC2Type.SPEIGER;
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
	public Item getItem(String name) {
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		return stack;
	}

	@Override
	public boolean isWrench(ItemStack par1) {
		return par1 != null && par1.getItem() instanceof ItemToolWrench;
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
		TileEntity te = world.getTileEntity(x, y, z);
		/*if (te instanceof TileEntitySolarPanel || te instanceof TileEntityGeneratorBase) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}*/
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			Boolean active = false;
			/*tag.setString("euType", "EU");
			if (te instanceof TileEntitySolarPanel) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntitySolarPanel) te).getOfferedEnergy());
				tag.setDouble("maxStorage", ((TileEntitySolarPanel) te).getOutput());
				active = ((TileEntitySolarPanel) te).getActive();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntitySolarPanel) te).getOutput());
				else
					tag.setDouble("production", 0);
				return tag;
			}
			if (te instanceof TileEntityGeneratorBase) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityGeneratorBase) te).getStoredEU());
				tag.setDouble("maxStorage", ((TileEntityGeneratorBase) te).getMaxEU());
				active = ((TileEntityGeneratorBase) te).getActive();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntityGeneratorBase) te).getOutput());
				else
					tag.setDouble("production", 0);
				return tag;
			}*/
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidTankInfo[] getAllTanks(TileEntity te) {
		if (!(te instanceof IFluidHandler))
			return null;

		return ((IFluidHandler) te).getTankInfo(ForgeDirection.UP);
	}

	@Override
	public ItemStack getReactorCard(World world, int x, int y, int z) {
		/*Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockTileEntity))
			return ItemStack.EMPTY;*/

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityNuclearReactor || te instanceof TileEntityReactorChamber || te instanceof TileEntityNuclearReactorSteam) {
			ChunkCoordinates position = ReactorHelper.getTargetCoordinates(world, x, y, z);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
				return sensorLocationCard;
			}
		}/* else if (te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort
				|| te instanceof TileEntityReactorAccessHatch) {
			BlockPos position = ReactorHelper.get5x5TargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR5X5);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}*/
		return null;
	}

	@Override
	public ItemStack getLiquidAdvancedCard(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
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
		boolean isSteam = ReactorHelper.isSteam(reactor);
		reader.setBoolean("isSteam", isSteam);

		/*IReactorChamber chamber = (IReactorChamber) reactor;
		int size = chamber.getReactorSize();*/
		int dmgLeft = 0;
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				ItemStack stack = reactor.getItemAt(x, y);
				if (stack != null)
					dmgLeft = Math.max(dmgLeft, ReactorHelper.getNuclearCellTimeLeft(stack));
			}
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
		// TODO Auto-generated method stub
		return CardState.NO_TARGET;
	}

	@Override
	public void showBarrelInfo(EntityPlayer player, TileEntity te) {
		// TODO Auto-generated method stub
		
	}
}
