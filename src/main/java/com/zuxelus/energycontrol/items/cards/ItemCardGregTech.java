package com.zuxelus.energycontrol.items.cards;

import java.lang.reflect.Field;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.CS;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.recipes.Recipe;
import gregapi.tileentity.connectors.MultiTileEntityWireElectric;
import gregapi.util.UT;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.util.GT_LanguageManager;
import gregtech.tileentity.energy.converters.MultiTileEntityBoilerTank;
import gregtech.tileentity.energy.converters.MultiTileEntityDynamoElectric;
import gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorSolid;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardGregTech extends ItemCardBase {

	private int strCount;

	public ItemCardGregTech() {
		super(ItemCardType.CARD_GREGTECH, "card_gregtech");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		try {
			Class.forName("gregtech.GT6_Main");
			if (te instanceof MultiTileEntityGeneratorSolid) {
				reader.setInt("type", 1);
				MultiTileEntityGeneratorSolid generator = (MultiTileEntityGeneratorSolid) te;
				reader.setBoolean("active", generator.getVisualData() == 1);
				// Collection<TagData> data = generator.getEnergyTypes((byte) 0);
				reader.setString("euType", "HU");
				Field field = MultiTileEntityGeneratorSolid.class.getDeclaredField("mEnergy");
				field.setAccessible(true);
				reader.setLong("energy", (Long) field.get(generator));
				field = MultiTileEntityGeneratorSolid.class.getDeclaredField("mEfficiency");
				field.setAccessible(true);
				short efficiency = (Short) field.get(generator);
				reader.setDouble("efficiency", efficiency / 100.0D);
				reader.setLong("rate", generator.getEnergySizeOutputMin(null, (byte) 0));
				field = MultiTileEntityGeneratorSolid.class.getDeclaredField("mLastRecipe");
				field.setAccessible(true);
				Recipe recipe = (Recipe) field.get(generator);
				if (recipe != null)
					reader.setLong("input", UT.Code.units(Math.abs(recipe.mEUt * recipe.mDuration), 10000L, efficiency, false));
				else
					reader.setLong("input", 0L);

				ItemStack stack = generator.getStackInSlot(0);
				if (stack != null)
					reader.setString("iteminput", stack.stackSize + "x" + getItemName(stack));
				else
					reader.setString("iteminput", "");
				stack = generator.getStackInSlot(1);
				if (stack != null)
					reader.setString("itemoutput", stack.stackSize + "x" + getItemName(stack));
				else
					reader.setString("itemoutput", "");
				return CardState.OK;
			}
			if (te instanceof MultiTileEntityBoilerTank) {
				MultiTileEntityBoilerTank boiler = (MultiTileEntityBoilerTank) te;
				reader.setInt("type", 2);
				reader.setLong("energy", boiler.getEnergyStored(TD.Energy.HU, (byte) 0));
				reader.setLong("capacity", boiler.getEnergyCapacity(TD.Energy.HU, (byte) 0));
				reader.setInt("barometer", (int) boiler.getVisualData());
				Field field = MultiTileEntityBoilerTank.class.getDeclaredField("mEfficiency");
				field.setAccessible(true);
				short efficiency = (Short) field.get(boiler);
				reader.setDouble("efficiency", efficiency / 100.0D);
				field = MultiTileEntityBoilerTank.class.getDeclaredField("mTanks");
				field.setAccessible(true);
				FluidTankGT[] tanks = (FluidTankGT[]) field.get(boiler);
				field = MultiTileEntityBoilerTank.class.getDeclaredField("mOutput");
				field.setAccessible(true);
				if (tanks[1].amount() <= tanks[1].capacity() / 2L)
					reader.setLong("output", 0L);
				else if (tanks[1].amount() > 3 * tanks[1].capacity() / 4L)
					reader.setLong("output", (Long) field.get(boiler) * 2L);
				else
					reader.setLong("output", (Long) field.get(boiler));
				reader.setLong("tankinput", tanks[0].amount());
				long diff = tanks[1].amount() - reader.getLong("tankoutput");
				reader.setLong("diff", diff);
				reader.setLong("tankoutput", tanks[1].amount());
				return CardState.OK;
			}
			if (te instanceof MultiTileEntityTurbineSteam) {
				MultiTileEntityTurbineSteam turbine = (MultiTileEntityTurbineSteam) te;
				reader.setInt("type", 3);
				reader.setBoolean("active", turbine.mActivity.mActive);
				reader.setLong("energy", turbine.mStorage.mEnergy);
				reader.setLong("capacity", turbine.mStorage.mCapacity);
				reader.setLong("steam", turbine.mSteamCounter);
				reader.setLong("output", turbine.mEnergyProducedNextTick);
				reader.setBoolean("clockwise", (turbine.getVisualData() & 0x4) == 0);
				// reader.setBoolean("waste", turbine.mConverter.mWasteEnergy);
				reader.setString("in", turbine.mEnergyIN.mMin + "mB - " + turbine.mEnergyIN.mMax + "mB");
				reader.setString("out", turbine.mEnergyOUT.mMin + "RU - " + turbine.mEnergyOUT.mMax + "RU");
				return CardState.OK;
			}
			if (te instanceof MultiTileEntityDynamoElectric) {
				MultiTileEntityDynamoElectric dynamo = (MultiTileEntityDynamoElectric) te;
				reader.setInt("type", 4);
				reader.setBoolean("active", dynamo.mActivity.mActive);
				reader.setLong("energy", dynamo.mStorage.mEnergy);
				reader.setLong("capacity", dynamo.mStorage.mCapacity);
				reader.setString("in", dynamo.mEnergyIN.mMin + "RU - " + dynamo.mEnergyIN.mMax + "RU");
				reader.setString("out", dynamo.mEnergyOUT.mMin + "EU - " + dynamo.mEnergyOUT.mMax + "EU");
				TileEntity t = world.getTileEntity(te.xCoord + CS.OFFSETS_X[dynamo.mFacing],
						te.yCoord + CS.OFFSETS_Y[dynamo.mFacing], te.zCoord + CS.OFFSETS_Z[dynamo.mFacing]);
				if (!dynamo.mActivity.mActive)
					reader.setLong("output", 0L);
				else if (t instanceof MultiTileEntityWireElectric)
					reader.setLong("output", ((MultiTileEntityWireElectric) t).mTransferredWattage);
				else
					reader.setLong("output", -1L);
				return CardState.OK;
			}
		} catch (Exception ignored) { }

		try {
			Class.forName("gregtech.GT_Mod");
			if (te instanceof IGregTechDeviceInformation && ((IGregTechDeviceInformation) te).isGivingInformation()) {
				String[] tInfoData = ((IGregTechDeviceInformation) te).getInfoData();
				for (int i = 0; i < tInfoData.length; i++)
					reader.setString("mString" + i, tInfoData[i]);
				reader.setInt("mString", strCount = tInfoData.length);
			}
			return CardState.OK;
		} catch (Exception ignored) { }

		return CardState.NO_TARGET;
	}

	@SideOnly(Side.CLIENT)
	private String getItemName(ItemStack stack) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
		if (list.size() == 0)
			return stack.getItem().getUnlocalizedName();
		return list.get(0);
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		try {
			Class.forName("gregtech.GT6_Main");
			switch (reader.getInt("type")) {
			case 1:
				result.add(new PanelString("msg.ec.InfoPanelEnergyHU", reader.getInt("energy"), true));
				result.add(new PanelString("msg.ec.InfoPanelEfficiency", reader.getDouble("efficiency"), true));
				result.add(new PanelString("msg.ec.InfoPanelOutputHU", reader.getInt("rate"), true));
				result.add(new PanelString("msg.ec.InfoPanelInputHU", reader.getInt("input"), true));
				result.add(new PanelString("msg.ec.InfoPanelInput", reader.getString("iteminput"), true));
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getString("itemoutput"), true));
				addOnOff(result, reader.getBoolean("active"));
				break;
			case 2:
				result.add(new PanelString("msg.ec.InfoPanelEnergyHU", reader.getInt("energy"), true));
				result.add(new PanelString("msg.ec.InfoPanelCapacityHU", reader.getInt("capacity"), true));
				result.add(new PanelString("msg.ec.InfoPanelEfficiency", reader.getDouble("efficiency"), true));
				result.add(new PanelString("msg.ec.InfoPanelBarometer", reader.getInt("barometer"), true));
				result.add(new PanelString("msg.ec.InfoPanelOutputmB", reader.getInt("output"), true));
				result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getInt("diff"), true));
				result.add(new PanelString("msg.ec.InfoPanelWatermB", reader.getInt("tankinput"), true));
				result.add(new PanelString("msg.ec.InfoPanelSteammB", reader.getInt("tankoutput"), true));
				break;
			case 3:
				result.add(new PanelString("msg.ec.InfoPanelEnergyRU", reader.getInt("energy"), true));
				result.add(new PanelString("msg.ec.InfoPanelCapacityRU", reader.getInt("capacity"), true));
				result.add(new PanelString("msg.ec.InfoPanelSteammB", reader.getInt("steam"), true));
				result.add(new PanelString("msg.ec.InfoPanelOutputRU", reader.getInt("output"), true));
				result.add(
						new PanelString("msg.ec.InfoPanelClockwise", reader.getBoolean("clockwise").toString(), true));
				result.add(new PanelString("msg.ec.InfoPanelIn", reader.getString("in"), true));
				result.add(new PanelString("msg.ec.InfoPanelOut", reader.getString("out"), true));
				addOnOff(result, reader.getBoolean("active"));
				break;
			case 4:
				result.add(new PanelString("msg.ec.InfoPanelEnergyRU", reader.getInt("energy"), true));
				result.add(new PanelString("msg.ec.InfoPanelCapacityRU", reader.getInt("capacity"), true));
				result.add(new PanelString("msg.ec.InfoPanelOutputEU", reader.getInt("output").toString(), true));
				result.add(new PanelString("msg.ec.InfoPanelIn", reader.getString("in"), true));
				result.add(new PanelString("msg.ec.InfoPanelOut", reader.getString("out"), true));
				addOnOff(result, reader.getBoolean("active"));
				break;
			}
		} catch (Exception ignored) { }

		try {
			Class.forName("gregtech.GT_Mod");
			for (int i = 0; i < (strCount = reader.getInt("mString")); i++)
				if ((settings & 1 << i) != 0) {
					PanelString line = new PanelString();
					line.textLeft = GT_LanguageManager.getTranslation(reader.getString("mString" + i), "\\\\");
					result.add(line);
				}
		} catch (Exception ignored) { }

		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_GREGTECH;
	}
}
