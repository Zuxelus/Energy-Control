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
			reader.setString("name", block.getTranslationKey());
			reader.setBoolean("isPowered", world.isBlockPowered(target));
			reader.setInt("indirect", world.getRedstonePowerFromNeighbors(target));
			reader.setBoolean("canProvide", state.canProvidePower());
			reader.setString("powered", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					world.getRedstonePower(target.down(), Direction.DOWN), world.getRedstonePower(target.up(), Direction.UP),
					world.getRedstonePower(target.north(), Direction.NORTH), world.getRedstonePower(target.south(), Direction.SOUTH),
					world.getRedstonePower(target.west(), Direction.WEST), world.getRedstonePower(target.east(), Direction.EAST)));
			reader.setString("week", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getWeakPower(world, target, Direction.UP), state.getWeakPower(world, target, Direction.DOWN),
					state.getWeakPower(world, target, Direction.SOUTH), state.getWeakPower(world, target, Direction.NORTH),
					state.getWeakPower(world, target, Direction.EAST), state.getWeakPower(world, target, Direction.WEST)));
			reader.setString("strong", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getStrongPower(world, target, Direction.UP), state.getStrongPower(world, target, Direction.DOWN),
					state.getStrongPower(world, target, Direction.SOUTH), state.getStrongPower(world, target, Direction.NORTH),
					state.getStrongPower(world, target, Direction.EAST), state.getStrongPower(world, target, Direction.WEST)));
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
