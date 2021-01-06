package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardRedstone extends ItemCardBase {

	public ItemCardRedstone() {
		super(ItemCardType.CARD_REDSTONE, "card_redstone");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		Block block = world.getBlock(target.posX, target.posY, target.posZ);
		if (block != Blocks.air) {
			reader.setString("name", block.getLocalizedName());
			reader.setBoolean("isPowered", world.isBlockIndirectlyGettingPowered(target.posX, target.posY, target.posZ));
			reader.setInt("indirect", world.getStrongestIndirectPower(target.posX, target.posY, target.posZ));
			reader.setBoolean("canProvide", block.canProvidePower());
			reader.setString("powered", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					world.getIndirectPowerLevelTo(target.posX, target.posY - 1, target.posZ, 0), world.getIndirectPowerLevelTo(target.posX, target.posY + 1, target.posZ, 1),
					world.getIndirectPowerLevelTo(target.posX, target.posY, target.posZ - 1, 2), world.getIndirectPowerLevelTo(target.posX, target.posY, target.posZ + 1, 3),
					world.getIndirectPowerLevelTo(target.posX - 1, target.posY, target.posZ, 4), world.getIndirectPowerLevelTo(target.posX + 1, target.posY, target.posZ, 5)));
			reader.setString("week", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					block.isProvidingWeakPower(world, target.posX, target.posY, target.posZ, 1), block.isProvidingWeakPower(world, target.posX, target.posY, target.posZ, 0),
					block.isProvidingWeakPower(world, target.posX, target.posY, target.posZ, 3), block.isProvidingWeakPower(world, target.posX, target.posY, target.posZ, 2),
					block.isProvidingWeakPower(world, target.posX, target.posY, target.posZ, 5), block.isProvidingWeakPower(world, target.posX, target.posY, target.posZ, 4)));
			reader.setString("strong", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					block.isProvidingStrongPower(world, target.posX, target.posY, target.posZ, 1), block.isProvidingStrongPower(world, target.posX, target.posY, target.posZ, 0),
					block.isProvidingStrongPower(world, target.posX, target.posY, target.posZ, 3), block.isProvidingStrongPower(world, target.posX, target.posY, target.posZ, 2),
					block.isProvidingStrongPower(world, target.posX, target.posY, target.posZ, 5), block.isProvidingStrongPower(world, target.posX, target.posY, target.posZ, 4)));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		result.add(new PanelString("msg.ec.InfoPanelName", reader.getString("name"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelBlockPowered", reader.getBoolean("isPowered").toString(), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelIndirectPower", reader.getInt("indirect"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelCanProvidePower", reader.getBoolean("canProvide").toString(), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelRedstonePower", reader.getString("powered"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelSendWeekPower", reader.getString("week"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelSendStrongPower", reader.getString("strong"), showLabels));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_REDSTONE;
	}
}
