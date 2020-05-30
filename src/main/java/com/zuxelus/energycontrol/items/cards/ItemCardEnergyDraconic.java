package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardEnergyDraconic extends ItemCardBase {
	public ItemCardEnergyDraconic() {
		super(ItemCardType.CARD_ENERGY_DRACONIC, "card_energy_draconic");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;
		
		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		if (te == null)
			return CardState.NO_TARGET;

		if (te instanceof TileEnergyStorageCore) {
			reader.setDouble("storage", (double) ((TileEnergyStorageCore)te).getMaxEnergyStored());
			reader.setDouble("energy", (double) ((TileEnergyStorageCore)te).getEnergyStored());
			return CardState.OK;
		}
		if (te instanceof TileEnergyInfuser) {
			reader.setDouble("storage", (double) ((TileEnergyInfuser) te).energy.getMaxEnergyStored());
			reader.setDouble("energy", (double) ((TileEnergyInfuser) te).energy.getEnergyStored());
			return CardState.OK;
		}
		if (te instanceof TileEnergyTransceiver) {
			reader.setDouble("storage", (double) ((TileEnergyTransceiver) te).getStorage().getMaxEnergyStored());
			reader.setDouble("energy", (double) ((TileEnergyTransceiver) te).getStorage().getEnergyStored());
			return CardState.OK;
		}
		if (te instanceof TileGenerator) {
			reader.setDouble("storage", (double) ((TileGenerator) te).storage.getMaxEnergyStored());
			reader.setDouble("energy", (double) ((TileGenerator) te).storage.getEnergyStored());
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
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
	public List<PanelSetting> getSettingsList(ItemStack stack) {
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
