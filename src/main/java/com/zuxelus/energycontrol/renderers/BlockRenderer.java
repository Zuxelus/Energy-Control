package com.zuxelus.energycontrol.renderers;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.zlib.blocks.FacingBlock;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class BlockRenderer implements ISimpleBlockRenderingHandler { // 1.7.10
// We need this class because we don't have metadata-sensitive version of Block.setBlockBoundsForItemRender
	private int modelId;

	public BlockRenderer(int modelId) {
		this.modelId = modelId;
	}

	@Override
	public void renderInventoryBlock(Block block, int meta, int model, RenderBlocks renderer) {
		if (model != modelId)
			return;

		meta = 1;
		if (block instanceof InfoPanel || block instanceof InfoPanelExtender ||
			block instanceof KitAssembler || block instanceof RemoteThermalMonitor ||
			block instanceof AverageCounter || block instanceof EnergyCounter ||
			block instanceof SeedAnalyzer || block instanceof SeedLibrary ||
			block instanceof RangeTrigger || block instanceof AFSU)
			meta = 2;
		if (block instanceof HoloPanel || block instanceof HoloPanelExtender)
			meta = 5;

		renderer.uvRotateTop = block instanceof ThermalMonitor || block instanceof TimerBlock ? 1 : 0;
		float[] bounds = ((FacingBlock) block).getBlockBounds(ForgeDirection.getOrientation(meta));
		renderer.setRenderBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
		Tessellator tesselator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tesselator.startDrawingQuads();
		tesselator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
		tesselator.draw();
		tesselator.startDrawingQuads();
		tesselator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
		tesselator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int model, RenderBlocks renderer) {
		if (model != modelId)
			return false;

		// To skip standard renderer
		if ((!(renderer.overrideBlockTexture != null && renderer.overrideBlockTexture.getIconName().indexOf("destroy_stage") > -1)) &&
				(block instanceof TimerBlock || block instanceof InfoPanel || block instanceof InfoPanelExtender ||
				block instanceof ThermalMonitor || block instanceof RemoteThermalMonitor))
			return true;

		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int i) {
		return true;
	}

	@Override
	public int getRenderId() {
		return EnergyControl.instance.modelId;
	}
}