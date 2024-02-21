package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.hooklib.asm.At;
import com.zuxelus.hooklib.asm.Hook;
import com.zuxelus.hooklib.asm.Hook.ReturnValue;
import com.zuxelus.hooklib.asm.InjectionPoint;
import com.zuxelus.hooklib.asm.ReturnCondition;

import gregapi.code.TagData;
import gregapi.data.CS;
import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.connectors.MultiTileEntityWireElectric;
import gregapi.tileentity.connectors.TileEntityBase10ConnectorRendered;
import gregapi.tileentity.energy.ITileEntityEnergy.Util;
import gregapi.tileentity.energy.TileEntityBase10EnergyBatBox;
import gregapi.tileentity.energy.TileEntityBase10EnergyConverter;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT.Code;
import gregtech.tileentity.energy.converters.MultiTileEntityBoilerTank;
import gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import gregtech.tileentity.energy.generators.MultiTileEntitySolarPanelElectric;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorCore2x2;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class GregTechHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();
	public static Map<TileEntity, long[]> buffer = new HashMap<TileEntity, long[]>();
	public static final int len = 360 * 2;
	public static final int buf_len = 5; 

	public static ArrayList initArray(int size) {
		ArrayList list = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			list.add(0L);
		return list;
	}

	public static void addValue(ArrayList<Long> values, int start, int size, long value) {
		for (int i = start + size - 1; i > start; i--)
			values.set(i, values.get(i - 1));
		values.set(start, value);
	}

	@Hook
	public static void onTick2(MultiTileEntityBoilerTank te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;
		long mEnergy = DataHelper.getLong(MultiTileEntityBoilerTank.class, "mEnergy", te);
		long mOutput = DataHelper.getLong(MultiTileEntityBoilerTank.class, "mOutput", te);
		short mEfficiency = DataHelper.getShort(MultiTileEntityBoilerTank.class, "mEfficiency", te);
		short mCoolDownResetTimer = DataHelper.getShort(MultiTileEntityBoilerTank.class, "mCoolDownResetTimer", te);
		FluidTankInfo[] info = ((MultiTileEntityBoilerTank) te).getTankInfo(null);
		long tConversions = Math.min(info[1].capacity / 2560L, Math.min(mEnergy / 80L, info[0].fluid != null ? info[0].fluid.amount : 0));
		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 19; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, tConversions); // water
			for (int i = 39; i > 20; i--)
				values.set(i, values.get(i - 1));
			values.set(20, Code.units(tConversions, 10000L, mEfficiency * 160, false));
			for (int i = 59; i > 40; i--)
				values.set(i, values.get(i - 1));
			values.set(40, tConversions * 80L);
			if (mCoolDownResetTimer == 1) {
				values.set(20, -mOutput * 64L);
				values.set(40, mOutput * 64L / CS.STEAM_PER_EU);
			}
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 60; i++)
				values.add(0L);
			map.put(te, values);
		}
	}

	@Hook(at = @At(point = InjectionPoint.HEAD))
	public static void doConversion(MultiTileEntityTurbineSteam te, long aTimer) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;
		if (!buffer.containsKey(te))
			buffer.put(te, new long[buf_len]);
		long[] list = buffer.get(te);
		long water = 0;
		if (te.mEnergyProducedNextTick == 0) {
			if (te.mTank.has(te.getEnergySizeInputMin(te.mConverter.mEnergyIN.mType, (byte)6) * 2L)) {
				long steam = te.mSteamCounter + te.mTank.amount();
				if (steam >= 200) {
					water = steam / 200;
				}
			}
		}
		list[4] = water;
	}

	@Hook
	public static void onTick2(TileEntityBase10EnergyConverter te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !buffer.containsKey(te) || !aIsServerSide)
			return;
		ArrayList<Long> values = map.get(te);
		long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			if (te instanceof MultiTileEntityTurbineSteam) {
				addValue(values, 0, len, list[3]);
				addValue(values, len, len, list[1]);
				addValue(values, len * 2, len, list[4]);
				list[3] = 0; // consumption
				list[1] = 0; // output
				list[4] = 0; // water
			} else {
				addValue(values, 0, len, list[0]);
				addValue(values, len, len, list[1]);
				list[0] = 0; // consumption
				list[1] = 0; // output
			}
		} else
			map.put(te, initArray(len * (te instanceof MultiTileEntityTurbineSteam ? 3 : 2)));
	}

	@Hook
	public static void onTick2(TileEntityBase10ConnectorRendered te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !buffer.containsKey(te) || !aIsServerSide || te instanceof MultiTileEntityWireElectric)
			return;
		ArrayList<Long> values = map.get(te);
		long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, list[0]);
			addValue(values, len, len, list[1]);
			//values.set(128, list[2]);
			list[0] = 0;
			list[1] = 0;
		} else
			map.put(te, initArray(len * 2));
	}

	@Hook
	public static void onTick2(TileEntityBase10EnergyBatBox te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !buffer.containsKey(te) || !aIsServerSide)
			return;
		ArrayList<Long> values = map.get(te);
		long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, list[0]);
			addValue(values, len, len, list[1]);
			//values.set(128, list[2]);
			list[0] = 0;
			list[1] = 0;
		} else
			map.put(te, initArray(len * 2));
	}

	@Hook(at = @At(point = InjectionPoint.RETURN), returnCondition = ReturnCondition.ALWAYS)
	public static final long insertEnergyInto(Util te, TagData aEnergyType, byte aSideInto, long aSize, long aAmount, Object aEmitter, TileEntity aReceiver, @ReturnValue long rUsedAmount) {
		if (map.containsKey(aReceiver)) {
			if (!buffer.containsKey(aReceiver))
				buffer.put(aReceiver, new long[buf_len]);
			long[] list = buffer.get(aReceiver);
			list[0] = list[0] + rUsedAmount * aSize;
			if (list[2] < aSize)
				list[2] = aSize;
		}
		if (aEmitter instanceof TileEntity && map.containsKey(aEmitter)) {
			if (!buffer.containsKey(aEmitter))
				buffer.put((TileEntity) aEmitter, new long[buf_len]);
			long[] list = buffer.get(aEmitter);
			list[1] = list[1] + rUsedAmount * aSize;
		}
		return rUsedAmount;
	}

	@Hook(at = @At(point = InjectionPoint.RETURN), returnCondition = ReturnCondition.ALWAYS)
	public static int fill(TileEntityBase01Root te, ForgeDirection aDirection, FluidStack aFluid, boolean aDoFill, @ReturnValue int rFilledAmount) {
		if (!map.containsKey(te) || !aDoFill)
			return rFilledAmount;
		if (!buffer.containsKey(te))
			buffer.put(te, new long[buf_len]);
		long[] list = buffer.get(te);
		list[3] = list[3] + rFilledAmount;
		return rFilledAmount;
	}

	@Hook
	public static void onTick2(MultiTileEntityWireElectric te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !aIsServerSide)
			return;
		ArrayList<Long> values = map.get(te);
		//long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, te.mTransferredWattage);
			values.set(len, te.mTransferredAmperes);
		} else
			map.put(te, initArray(len * 2));
	}

	@Hook
	public static void onTick2(MultiTileEntitySolarPanelElectric te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !aIsServerSide)
			return;
		ArrayList<Long> values = map.get(te);
		long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, list[0]);
			addValue(values, len, len, list[1]);
			list[0] = 0;
			list[1] = 0;
		} else
			map.put(te, initArray(len * 2));
	}

	@Hook
	public static void onTick2(MultiTileEntityBasicMachine te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !aIsServerSide)
			return;
		ArrayList<Long> values = map.get(te);
		if (!buffer.containsKey(te))
			buffer.put(te, new long[buf_len]);
		long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, list[0]);
			addValue(values, len, len, list[3]);
			list[0] = 0;
			list[3] = 0;
		} else
			map.put(te, initArray(len * 2));
	}

	@Hook
	public static void onTick2(MultiTileEntityMotorLiquid te, long aTimer, boolean aIsServerSide) {
		if (!map.containsKey(te) || !aIsServerSide)
			return;
		ArrayList<Long> values = map.get(te);
		if (!buffer.containsKey(te))
			buffer.put(te, new long[buf_len]);
		long[] list = buffer.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, list[1]);
			addValue(values, len, len, list[3]);
			list[1] = 0;
			list[3] = 0;
		} else
			map.put(te, initArray(len * 2));
	}

	@Hook(at = @At(point = InjectionPoint.RETURN))
	public static void onServerTickPre(MultiTileEntityPipeFluid te, boolean aFirst) {
		if (!map.containsKey(te))
			return;
		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, te.mTransferredAmount);
		} else
			map.put(te, initArray(len));
	}

	@Hook
	public static void onServerTickPost(MultiTileEntityReactorCore2x2 te, boolean aFirst) {
		if (aFirst || !map.containsKey(te))
			return;
		if (!buffer.containsKey(te))
			buffer.put(te, new long[buf_len]);
		long[] list = buffer.get(te);
		list[0] = te.mTanks[0].amount();
		list[1] = te.mTanks[1].amount();
	}

	@Hook(at = @At(point = InjectionPoint.RETURN), targetMethod = "onServerTickPost", returnCondition = ReturnCondition.ALWAYS)
	public static void onServerTickPostEnd(MultiTileEntityReactorCore2x2 te, boolean aFirst) {
		if (aFirst || !map.containsKey(te))
			return;
		if (!buffer.containsKey(te))
			buffer.put(te, new long[buf_len]);
		long[] list = buffer.get(te);
		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			addValue(values, 0, len, te.mTanks[1].amount() - list[1]);
			addValue(values, len, len, list[0] - te.mTanks[0].amount());
		} else
			map.put(te, initArray(len * 2));
	}
	
}
