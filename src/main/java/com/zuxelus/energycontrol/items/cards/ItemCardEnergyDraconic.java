package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardEnergyDraconic extends ItemCardBase {
	public ItemCardEnergyDraconic() {
		super(ItemCardType.CARD_ENERGY_DRACONIC, "card_energy_draconic");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		if (te == null)
			return CardState.NO_TARGET;

		if (te instanceof IExtendedRFStorage) {
			reader.setDouble("storage", (double) ((IExtendedRFStorage)te).getExtendedCapacity());
			reader.setDouble("energy", (double) ((IExtendedRFStorage)te).getExtendedCapacity());
			return CardState.OK;
		}
		if (te instanceof TileEnergyInfuser) {
			reader.setDouble("storage", (double) ((TileEnergyInfuser) te).energyStorage.getMaxEnergyStored());
			reader.setDouble("energy", (double) ((TileEnergyInfuser) te).energyStorage.getEnergyStored());
			return CardState.OK;
		}
		if (te instanceof TileCrystalDirectIO) {
			reader.setDouble("storage", (double) ((TileCrystalDirectIO) te).getMaxEnergyStored());
			reader.setDouble("energy", (double) ((TileCrystalDirectIO) te).getEnergyStored());
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();

		double energy = reader.getDouble("energy");
		double storage = reader.getDouble("storage");

		if ((displaySettings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", energy, showLabels));
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFreeRF", storage - energy, showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", storage, showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", storage == 0 ? 100 : ((energy / storage) * 100), showLabels));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(4);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelFree"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelPercentage"), 8, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_DRACONIC;
	}
}
