package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemCardToggle extends ItemCardBase implements ITouchAction {
	private static final PropertyBool POWERED = PropertyBool.create("powered");

	public ItemCardToggle() {
		super(ItemCardType.CARD_TOGGLE, "card_toggle");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		IBlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block == Blocks.LEVER || block instanceof BlockButton) {
			reader.setBoolean("value", state.getValue(POWERED));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
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
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitId() {
		return ItemCardType.KIT_TOGGLE;
	}

	@Override
	public boolean enableTouch(ItemStack stack) {
		return true;
	}

	@Override
	public boolean runTouchAction(World world, ICardReader reader, ItemStack stack) {
		BlockPos pos = reader.getTarget();
		if (pos == null)
			return false;

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.LEVER) {
			state = state.cycleProperty(POWERED);
			world.setBlockState(pos, state, 3);
			world.notifyNeighborsOfStateChange(pos, state.getBlock(), false);
			EnumFacing enumfacing = state.getValue(BlockLever.FACING).getFacing();
			world.notifyNeighborsOfStateChange(pos.offset(enumfacing.getOpposite()), state.getBlock(), false);
		}
		if (block instanceof BlockButton) {
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.TRUE), 3);
			world.markBlockRangeForRenderUpdate(pos, pos);
			world.notifyNeighborsOfStateChange(pos, block, false);
			world.notifyNeighborsOfStateChange(pos.offset(state.getValue(BlockButton.FACING).getOpposite()), block, false);
			world.scheduleUpdate(pos, block, block.tickRate(world));
		}
		return false;
	}

	@Override
	public void renderImage(TextureManager manager, ICardReader reader) {
		double x = -0.5D;
		double y = -0.5D;
		double z = 0.009;
		double height = 1;
		double width = 1;
		double textureX = 0;
		double textureY = 0;
		if (reader.getBoolean("value"))
			manager.bindTexture(new ResourceLocation(EnergyControl.MODID + ":textures/gui/green.png"));
		else
			manager.bindTexture(new ResourceLocation(EnergyControl.MODID + ":textures/gui/grey.png"));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x + 0, y + height, z).tex((float)(textureX + 0), (float)(textureY + height)).endVertex();
		bufferbuilder.pos(x + width, y + height, z).tex((float)(textureX + width), (float)(textureY + height)).endVertex();
		bufferbuilder.pos(x + width, y + 0, z).tex((float)(textureX + width), (float)(textureY + 0)).endVertex();
		bufferbuilder.pos(x + 0, y + 0, z).tex((float)(textureX + 0), (float)(textureY + 0)).endVertex();
		tessellator.draw();
	}
}
