package com.zuxelus.energycontrol.gametest;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Holo panel screen in one line: extender, panel (core), lever.
 */
public class HoloPanelTest implements FabricGameTest {
	private static final BlockPos EXTENDER = new BlockPos(1, 2, 1);
	private static final BlockPos PANEL = new BlockPos(2, 2, 1);
	private static final BlockPos LEVER = new BlockPos(3, 2, 1);

	private static void build(TestContext ctx) {
		for (int x = 1; x <= 3; x++)
			ctx.setBlockState(new BlockPos(x, 1, 1), Blocks.STONE);
		ctx.setBlockState(EXTENDER, holo(ModItems.holo_panel_extender));
		ctx.setBlockState(PANEL, holo(ModItems.holo_panel));
		ctx.setBlockState(LEVER, Blocks.LEVER.getDefaultState().with(LeverBlock.FACE, BlockFace.FLOOR).with(LeverBlock.FACING, Direction.NORTH));
	}

	private static net.minecraft.block.BlockState holo(Block block) {
		return block.getDefaultState().with(FacingHorizontal.FACING, Direction.NORTH).with(FacingHorizontalActive.ACTIVE, false);
	}

	private static void expectOn(TestContext ctx, boolean on, String when) {
		boolean panel = ctx.getBlockState(PANEL).get(FacingHorizontalActive.ACTIVE);
		boolean extender = ctx.getBlockState(EXTENDER).get(FacingHorizontalActive.ACTIVE);
		boolean powered = ((TileEntityInfoPanel) ctx.getBlockEntity(PANEL)).getPowered();
		if (panel != on || extender != on || powered != on)
			throw new GameTestException(when + ": expected " + on + " but panel active=" + panel + ", extender active=" + extender + ", panel powered=" + powered);
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
	public void leverTurnsScreenOnAndOff(TestContext ctx) {
		build(ctx);
		ctx.runAtTick(20, () -> ctx.useBlock(LEVER));
		ctx.runAtTick(40, () -> expectOn(ctx, true, "after lever on"));
		ctx.runAtTick(50, () -> ctx.useBlock(LEVER));
		ctx.runAtTick(80, () -> {
			expectOn(ctx, false, "after lever off");
			ctx.complete();
		});
	}

	// what the panel GUI sends when it closes: the card title (type 4)
	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
	public void closingPanelGuiKeepsScreenOn(TestContext ctx) {
		build(ctx);
		ctx.runAtTick(20, () -> ctx.useBlock(LEVER));
		ctx.runAtTick(40, () -> expectOn(ctx, true, "after lever on"));
		ctx.runAtTick(50, () -> {
			NbtCompound tag = new NbtCompound();
			tag.putInt("type", 4);
			tag.putInt("slot", 0);
			tag.putString("title", "test");
			((TileEntityInfoPanel) ctx.getBlockEntity(PANEL)).onServerMessageReceived(tag);
			ctx.useBlock(EXTENDER);
		});
		ctx.runAtTick(80, () -> {
			expectOn(ctx, true, "after GUI close and extender click");
			ctx.complete();
		});
	}
}
