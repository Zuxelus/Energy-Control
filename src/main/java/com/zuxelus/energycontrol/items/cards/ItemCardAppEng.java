package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardAppEng extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getBlockEntity(target);
		CompoundNBT tag = CrossModLoader.getCrossMod(ModIDs.APPLIED_ENERGISTICS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			result.add(new PanelString("msg.ec.InfoPanelTotalNodes", reader.getInt("nodes"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelStorageCells", reader.getInt("cells"), showLabels));
			if (isServer) {
				result.add(new PanelString(String.format("%d of %d Bytes Used", reader.getInt("bytesUsed"), reader.getInt("bytesTotal"))));
				result.add(new PanelString(String.format("%d of %d Types", reader.getInt("typesUsed"), reader.getInt("typesTotal"))));
			} else {
				result.add(new PanelString(I18n.get("msg.ec.InfoPanelBytesUsed", reader.getInt("bytesUsed"), reader.getInt("bytesTotal"))));
				result.add(new PanelString(I18n.get("msg.ec.InfoPanelTypes", reader.getInt("typesUsed"), reader.getInt("typesTotal"))));
			}
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			break;
		case 2:
			result.add(new PanelString("msg.ec.InfoPanelName", reader.getString("name"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getInt("size"), showLabels));
			break;
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}
