package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardRedstone extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block != Blocks.AIR) {
			reader.setString("name", block.getDescriptionId());
			reader.setBoolean("isPowered", world.hasNeighborSignal(target));
			reader.setInt("indirect", world.getBestNeighborSignal(target));
			reader.setBoolean("canProvide", state.isSignalSource());
			reader.setString("powered", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					world.getSignal(target.below(), Direction.DOWN), world.getSignal(target.above(), Direction.UP),
					world.getSignal(target.north(), Direction.NORTH), world.getSignal(target.south(), Direction.SOUTH),
					world.getSignal(target.west(), Direction.WEST), world.getSignal(target.east(), Direction.EAST)));
			reader.setString("week", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getSignal(world, target, Direction.UP), state.getSignal(world, target, Direction.DOWN),
					state.getSignal(world, target, Direction.SOUTH), state.getSignal(world, target, Direction.NORTH),
					state.getSignal(world, target, Direction.EAST), state.getSignal(world, target, Direction.WEST)));
			reader.setString("strong", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getDirectSignal(world, target, Direction.UP), state.getDirectSignal(world, target, Direction.DOWN),
					state.getDirectSignal(world, target, Direction.SOUTH), state.getDirectSignal(world, target, Direction.NORTH),
					state.getDirectSignal(world, target, Direction.EAST), state.getDirectSignal(world, target, Direction.WEST)));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
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

	@Override
	public Item getKitFromCard() {
		return ModItems.kit_redstone.get();
	}
}
