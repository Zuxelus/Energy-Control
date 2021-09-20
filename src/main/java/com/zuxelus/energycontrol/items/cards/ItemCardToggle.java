package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardToggle extends ItemCardMain implements ITouchAction {
	private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block == Blocks.LEVER || block instanceof AbstractButtonBlock) {
			reader.setBoolean("value", state.getValue(POWERED));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		return null;
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
	public boolean runTouchAction(World world, ICardReader reader, ItemStack stack) {
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
		if (block instanceof AbstractButtonBlock) {
			world.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(true)), 3);
			world.updateNeighborsAt(pos, block);
			world.updateNeighborsAt(pos.relative(getFacing(state).getOpposite()), block);
			world.getBlockTicks().scheduleTick(pos, block, block instanceof WoodButtonBlock ? 30 : 20);
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
	public void renderImage(TextureManager manager, ICardReader reader, MatrixStack matrixStack) {
		float x = -0.5F;
		float y = -0.5F;
		float z = 0.009F;
		float height = 1;
		float width = 1;
		float textureX = 0;
		float textureY = 0;
		if (reader.getBoolean("value"))
			manager.bind(new ResourceLocation(EnergyControl.MODID + ":textures/gui/green.png"));
		else
			manager.bind(new ResourceLocation(EnergyControl.MODID + ":textures/gui/grey.png"));

		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		Matrix4f matrix = matrixStack.last().pose();
		bufferbuilder.vertex(matrix, x + 0, y + height, z).uv(textureX + 0, textureY + height).endVertex();
		bufferbuilder.vertex(matrix, x + width, y + height, z).uv(textureX + width, textureY + height).endVertex();
		bufferbuilder.vertex(matrix, x + width, y + 0, z).uv(textureX + width, textureY + 0).endVertex();
		bufferbuilder.vertex(matrix, x + 0, y + 0, z).uv(textureX + 0, textureY + 0).endVertex();
		tessellator.end();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
	}
}
