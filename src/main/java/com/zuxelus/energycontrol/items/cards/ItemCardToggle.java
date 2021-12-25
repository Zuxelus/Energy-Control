package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WoodenButtonBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class ItemCardToggle extends ItemCardMain implements ITouchAction {
	private static final BooleanProperty POWERED = Properties.POWERED;
	private static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;
	private static final DirectionProperty HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;
	private static final Identifier TEXTURE_ON = new Identifier(EnergyControl.MODID, "textures/gui/green.png");
	private static final Identifier TEXTURE_OFF = new Identifier(EnergyControl.MODID, "textures/gui/grey.png");

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block == Blocks.LEVER || block instanceof AbstractButtonBlock) {
			reader.setBoolean("value", state.get(POWERED));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		return null;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public boolean enableTouch() {
		return true;
	}

	@Override
	public boolean runTouchAction(World world, ICardReader reader, ItemStack stack) {
		BlockPos pos = reader.getTarget();
		if (pos == null)
			return false;

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.LEVER) {
			state = state.cycle(POWERED);
			world.setBlockState(pos, state, 3);
			world.updateNeighborsAlways(pos, block);
			world.updateNeighborsAlways(pos.offset(getFacing(state).getOpposite()), block);
		}
		if (block instanceof AbstractButtonBlock) {
			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 3);
			world.updateNeighborsAlways(pos, block);
			world.updateNeighborsAlways(pos.offset(getFacing(state).getOpposite()), block);
			world.createAndScheduleBlockTick(pos, block, block instanceof WoodenButtonBlock ? 30 : 20);
		}
		return false;
	}

	private static Direction getFacing(BlockState state) {
		switch ((WallMountLocation) state.get(FACE)) {
		case CEILING:
			return Direction.DOWN;
		case FLOOR:
			return Direction.UP;
		default:
			return state.get(HORIZONTAL_FACING);
		}
	}

	@Override
	public void renderImage(ICardReader reader, MatrixStack matrixStack) {
		float x = -0.5F;
		float y = -0.5F;
		float z = 0.009F;
		float height = 1;
		float width = 1;
		float textureX = 0;
		float textureY = 0;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		if (reader.getBoolean("value"))
			RenderSystem.setShaderTexture(0, TEXTURE_ON);
		else
			RenderSystem.setShaderTexture(0, TEXTURE_OFF);
		RenderSystem.enableDepthTest();
		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuffer();
		bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		bufferbuilder.vertex(matrix, x + 0, y + height, z).texture(textureX + 0, textureY + height).next();
		bufferbuilder.vertex(matrix, x + width, y + height, z).texture(textureX + width, textureY + height).next();
		bufferbuilder.vertex(matrix, x + width, y + 0, z).texture(textureX + width, textureY + 0).next();
		bufferbuilder.vertex(matrix, x + 0, y + 0, z).texture(textureX + 0, textureY + 0).next();
		tesselator.draw();
		RenderSystem.disableDepthTest();
	}
}
