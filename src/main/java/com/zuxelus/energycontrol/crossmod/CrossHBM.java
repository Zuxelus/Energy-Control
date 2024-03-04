package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.machine.ItemBattery;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityDummy;
import com.hbm.tileentity.machine.TileEntityMachineGasCent.PseudoFluidTank;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.hbm.tileentity.machine.storage.TileEntityMachineBattery;
import com.zuxelus.energycontrol.hooks.HBMHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardHBM;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitHBM;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import api.hbm.fluid.IFluidUser;
import api.hbm.tile.IInfoProviderEC;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CrossHBM extends CrossModBase {

	@Override
	public boolean isElectricItem(ItemStack stack) {
		if (stack.getItem() instanceof ItemBattery)
			return true;
		return false;
	}

	@Override
	public double dischargeItem(ItemStack stack, double needed) {
		ItemBattery item = (ItemBattery) stack.getItem();
		long amount =  Math.min(Math.min((long) needed, item.getDischargeRate()), item.getCharge(stack));
		item.dischargeBattery(stack, amount);
		return amount;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IInfoProviderEC) {
			NBTTagCompound tag = new NBTTagCompound();
			((IInfoProviderEC) te).provideExtraInfo(tag);
			NBTTagCompound result = new NBTTagCompound();
			result.setString(DataHelper.EUTYPE, "HE");
			if (tag.hasKey(DataHelper.ENERGY))
				result.setDouble(DataHelper.ENERGY, tag.getDouble(DataHelper.ENERGY));
			if (tag.hasKey(DataHelper.CAPACITY))
				result.setDouble(DataHelper.CAPACITY, tag.getDouble(DataHelper.CAPACITY));
			return tag;
		}
		return null;
	}

	@Override
	public int getHeat(World world, int x, int y, int z) {
		if (world == null)
			return -1;

		int t = -1;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			t = getHeat(te);
			if (t > 0)
				return t;
		}
		for (int xoffset = -3; xoffset < 4; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -3; zoffset < 4; zoffset++) {
					TileEntity te = world.getTileEntity(x + xoffset, y + yoffset, z + zoffset);
					t = getHeat(te);
					if (t > 0)
						return t;
				}
		return t;
	}

	private int getHeat(TileEntity te) {
		if (te instanceof TileEntityRBMKBase)
			return (int) ((TileEntityRBMKBase) te).heat;
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof TileEntityProxyCombo) {
			te = ((TileEntityProxyCombo) te).getTile();
		}
		if (te instanceof TileEntityDummy) {
			te = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
		}
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof IFluidUser) {
			FluidTank[] list = ((IFluidUser) te).getAllTanks();
			if (list.length == 0)
				return null;
			for (FluidTank tank : list)
				result.add(toFluidInfo(tank));
			return result;
		}
		return null;
	}

	private static FluidInfo toFluidInfo(FluidTank tank) {
		return new FluidInfo(tank.getTankType().getName(), tank.getFill(), tank.getMaxFill());
	}

	private static FluidInfo toFluidInfo(PseudoFluidTank tank) {
		return new FluidInfo(tank.getTankType().getName(), tank.getFill(), tank.getMaxFill());
	}

	private void addTank(String name, NBTTagCompound tag, FluidTank tank) {
		if (tank.getFill() == 0)
			tag.setString(name, "N/A");
		else {
			String fluidName = tank.getTankType().getUnlocalizedName();
			if (!StatCollector.translateToLocal(fluidName).equals(fluidName))
				tag.setString(name, String.format("%s: %s mB", StatCollector.translateToLocal(fluidName), tank.getFill()));
			else
				tag.setString(name, String.format("%s: %s mB", tank.getTankType().getName(), tank.getFill()));
		}
	}

	private void addTank(String name, NBTTagCompound tag, PseudoFluidTank tank) {
		if (tank.getFill() == 0)
			tag.setString(name, "N/A");
		else
			tag.setString(name, String.format("%s: %s mB", tank.getTankType().getName(), tank.getFill()));
	}

	public TileEntity findTileEntity(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block instanceof BlockDummyable) {
			int[] pos = ((BlockDummyable) block).findCore(world, x, y, z);
			return world.getTileEntity(pos[0], pos[1], pos[2]);
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileEntityProxyCombo) {
			te = ((TileEntityProxyCombo) te).getTile();
		}
		if (te instanceof TileEntityDummy) {
			te = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
		}
		if (te instanceof TileEntityMachineBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).getMaxPower());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}
		if (te instanceof IInfoProviderEC) {
			NBTTagCompound tag = new NBTTagCompound();
			((IInfoProviderEC) te).provideExtraInfo(tag);
			return tag;
		}
		return null;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = HBMHooks.map.get(te);
		if (values == null)
			HBMHooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitHBM::new);
		ItemCardMain.register(ItemCardHBM::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_HBM,
				new Object[] { "IT", "PD", 'P', Items.paper, 'D', "dyeBlack",
					'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'I', com.hbm.items.ModItems.ingot_steel });

			Recipes.addKitRecipe(ItemCardType.KIT_HBM, ItemCardType.CARD_HBM);
	}

	@Override
	public ResourceLocation getFluidTexture(String fluidName) {
		FluidType type = Fluids.fromName(fluidName);
		return type == null ? null : type.getTexture();
	}
}
