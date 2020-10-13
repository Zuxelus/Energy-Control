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
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.WoodenButtonBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class ToggleItemCard extends MainCardItem implements ITouchAction {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockState state = world.getBlockState(target);
		if (state == null)
			return CardState.NO_TARGET;
		
		if (state.getBlock() == Blocks.LEVER || state.getBlock() instanceof AbstractButtonBlock) {
			boolean value = ((Boolean)state.get(LeverBlock.POWERED)).booleanValue();
			reader.setBoolean("value", value);
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean showLabels) {
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
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_TOGGLE;
	}

	@Override
	public int getCardType() {
		return ItemCardType.CARD_TOGGLE;
	}

	@Override
	public void runTouchAction(PlayerEntity player, World world, ICardReader reader) {
		BlockPos pos = reader.getTarget();
		if (pos == null)
			return;

		BlockState state = world.getBlockState(pos);
		if (state == null)
			return;
		
		Block block = state.getBlock();
		if (block instanceof LeverBlock) {
			((LeverBlock) block).method_21846(state, world, pos);
		}
		if (state.getBlock() instanceof AbstractButtonBlock) {
			world.setBlockState(pos, state.with(AbstractButtonBlock.POWERED, true), 3);
			world.updateNeighborsAlways(pos, block);
			world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), block);
			if (state.getBlock() instanceof WoodenButtonBlock)
				world.getBlockTickScheduler().schedule(pos, block, 30);
			else
				world.getBlockTickScheduler().schedule(pos, block, 20);
		}
	}

	protected static Direction getDirection(BlockState state) {
		switch ((WallMountLocation) state.get(WallMountedBlock.FACE)) {
		case CEILING:
			return Direction.DOWN;
		case FLOOR:
			return Direction.UP;
		default:
			return (Direction) state.get(HorizontalFacingBlock.FACING);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void renderImage(TextureManager manager, Matrix4f matrix4f, ICardReader reader) {
		float x = -0.5F, y = -0.5F, z = -0.00001F, width = 1, height = 1, textureX = 0, textureY = 0;
		if (reader.getBoolean("value"))
			manager.bindTexture(new Identifier(EnergyControl.MODID + ":textures/gui/green.png"));
		else
			manager.bindTexture(new Identifier(EnergyControl.MODID + ":textures/gui/red.png"));

		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		//RenderSystem.enableBlend();

		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferbuilder.vertex(matrix4f, x, y + height, z).texture(textureX, textureY + height).next();
		bufferbuilder.vertex(matrix4f, x + width, y + height, z).texture(textureX + width, textureY + height).next();
		bufferbuilder.vertex(matrix4f, x + width, y, z).texture(textureX + width, textureY).next();
		bufferbuilder.vertex(matrix4f, x, y, z).texture(textureX, textureY).next();
		bufferbuilder.end();
		BufferRenderer.draw(bufferbuilder);
		//RenderSystem.disableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
	}
}
