package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemCardMekanism extends ItemCardBase {

	public ItemCardMekanism() {
		super(ItemCardType.CARD_MEKANISM, "card_mekanism");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		if (te instanceof TileEntityReactorController) {
			TileEntityReactorController controller = (TileEntityReactorController) te;
			if (controller.getReactor() == null)
				return CardState.NO_TARGET;
			reader.setInt("type", 1);
			reader.setBoolean("active", controller.isBurning());
			reader.setDouble("case", controller.getCaseTemp());
			reader.setDouble("plasma", controller.getPlasmaTemp());
			reader.setDouble("energy",controller.getEnergy());
			reader.setDouble("capacity",controller.getMaxEnergy());
			reader.setInt("output",controller.getOutput());
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.getInt("type") == 1) {
			result.add(new PanelString("msg.ec.InfoPanelCaseTemp", reader.getDouble("case"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelPlasmaTemp", reader.getDouble("plasma"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCaseTemp", reader.getDouble("energy"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelPlasmaTemp", reader.getDouble("capacity"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), showLabels));
			addOnOff(result, isServer, reader.getBoolean("active"));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitId() {
		return ItemCardType.KIT_MEKANISM;
	}
}
