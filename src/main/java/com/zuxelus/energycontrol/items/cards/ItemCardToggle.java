package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.renderers.ModRenderTypes;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardToggle extends ItemCardMain implements ITouchAction {
	private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final ResourceLocation TEXTURE_ON = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/green.png");
	private static final ResourceLocation TEXTURE_OFF = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/grey.png");

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block == Blocks.LEVER || block instanceof ButtonBlock) {
			reader.setBoolean("value", state.getValue(POWERED));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(Level world, int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		PanelString line = new PanelString();
		if (reader.getBoolean("value")) {
			line.textCenter = "o";
			line.colorCenter = 0x00ff00;
		} else {
			line.textCenter = "o";
			line.colorCenter = 0xff0000;
		}
		result.add(line);
		return result;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public boolean enableTouch() {
		return true;
	}

	@Override
	public boolean runTouchAction(Level world, ICardReader reader, ItemStack stack) {
		BlockPos pos = reader.getTarget();
		if (pos == null)
			return false;

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.LEVER) {
			state = state.cycle(POWERED);
			world.setBlock(pos, state, 3);
			world.updateNeighborsAt(pos, block);
			world.updateNeighborsAt(pos.relative(getFacing(state).getOpposite()), block);
		}
		if (block instanceof ButtonBlock) {
			world.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(true)), 3);
			world.updateNeighborsAt(pos, block);
			world.updateNeighborsAt(pos.relative(getFacing(state).getOpposite()), block);
			world.scheduleTick(pos, block, block.equals(Blocks.STONE_BUTTON) ? 20 : 30);
		}
		return false;
	}

	private static Direction getFacing(BlockState state) {
		switch ((AttachFace) state.getValue(FACE)) {
		case CEILING:
			return Direction.DOWN;
		case FLOOR:
			return Direction.UP;
		default:
			return state.getValue(HORIZONTAL_FACING);
		}
	}

	@Override
	public void renderImage(ICardReader reader, PoseStack matrixStack, MultiBufferSource buffer) {
		float x = -0.5F;
		float y = -0.5F;
		float z = 0.009F;
		float height = 1;
		float width = 1;
		float textureX = 0;
		float textureY = 0;
		VertexConsumer builder = buffer.getBuffer(ModRenderTypes.screenImage(reader.getBoolean("value") ? TEXTURE_ON : TEXTURE_OFF));
		Matrix4f matrix = matrixStack.last().pose();
		builder.vertex(matrix, x + 0, y + height, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(textureX + 0, textureY + height).uv2(LightTexture.FULL_BRIGHT).endVertex();
		builder.vertex(matrix, x + width, y + height, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(textureX + width, textureY + height).uv2(LightTexture.FULL_BRIGHT).endVertex();
		builder.vertex(matrix, x + width, y + 0, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(textureX + width, textureY + 0).uv2(LightTexture.FULL_BRIGHT).endVertex();
		builder.vertex(matrix, x + 0, y + 0, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(textureX + 0, textureY + 0).uv2(LightTexture.FULL_BRIGHT).endVertex();
	}
}
