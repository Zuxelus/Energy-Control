package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class ItemCardRedstone extends ItemCardBase {

	public ItemCardRedstone() {
		super(ItemCardType.CARD_REDSTONE, "card_redstone");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		IBlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block != Blocks.AIR) {
			reader.setString("name", block.getLocalizedName());
			reader.setBoolean("isPowered", world.isBlockPowered(target));
			reader.setInt("indirect", world.isBlockIndirectlyGettingPowered(target));
			reader.setBoolean("canProvide", state.canProvidePower());
			reader.setString("powered", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					world.getRedstonePower(target.down(), EnumFacing.DOWN), world.getRedstonePower(target.up(), EnumFacing.UP),
					world.getRedstonePower(target.north(), EnumFacing.NORTH), world.getRedstonePower(target.south(), EnumFacing.SOUTH),
					world.getRedstonePower(target.west(), EnumFacing.WEST), world.getRedstonePower(target.east(), EnumFacing.EAST)));
			reader.setString("week", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getWeakPower(world, target, EnumFacing.UP), state.getWeakPower(world, target, EnumFacing.DOWN),
					state.getWeakPower(world, target, EnumFacing.SOUTH), state.getWeakPower(world, target, EnumFacing.NORTH),
					state.getWeakPower(world, target, EnumFacing.EAST), state.getWeakPower(world, target, EnumFacing.WEST)));
			reader.setString("strong", String.format("D:%d U:%d N:%d S:%d W:%d E:%d",
					state.getStrongPower(world, target, EnumFacing.UP), state.getStrongPower(world, target, EnumFacing.DOWN),
					state.getStrongPower(world, target, EnumFacing.SOUTH), state.getStrongPower(world, target, EnumFacing.NORTH),
					state.getStrongPower(world, target, EnumFacing.EAST), state.getStrongPower(world, target, EnumFacing.WEST)));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
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
	public int getKitId() {
		return ItemCardType.KIT_REDSTONE;
	}
}
