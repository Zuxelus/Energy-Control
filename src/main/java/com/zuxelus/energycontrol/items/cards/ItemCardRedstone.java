package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

public class ItemCardRedstone extends ItemCardMain {

	@Override
	public CardState update(Level level, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockState state = level.getBlockState(target);
		Block block = state.getBlock();
		if (block != Blocks.AIR) {
			reader.setString("name", block.getDescriptionId());
			reader.setBoolean("isPowered", level.hasNeighborSignal(target));
			reader.setInt("indirect", level.getBestNeighborSignal(target));
			reader.setBoolean("canProvide", state.isSignalSource());
			reader.setString("powered", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					level.getSignal(target.below(), Direction.DOWN), level.getSignal(target.above(), Direction.UP),
					level.getSignal(target.north(), Direction.NORTH), level.getSignal(target.south(), Direction.SOUTH),
					level.getSignal(target.west(), Direction.WEST), level.getSignal(target.east(), Direction.EAST)));
			reader.setString("week", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getSignal(level, target, Direction.UP), state.getSignal(level, target, Direction.DOWN),
					state.getSignal(level, target, Direction.SOUTH), state.getSignal(level, target, Direction.NORTH),
					state.getSignal(level, target, Direction.EAST), state.getSignal(level, target, Direction.WEST)));
			reader.setString("strong", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getDirectSignal(level, target, Direction.UP), state.getDirectSignal(level, target, Direction.DOWN),
					state.getDirectSignal(level, target, Direction.SOUTH), state.getDirectSignal(level, target, Direction.NORTH),
					state.getDirectSignal(level, target, Direction.EAST), state.getDirectSignal(level, target, Direction.WEST)));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(Level level, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<>();
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
}
