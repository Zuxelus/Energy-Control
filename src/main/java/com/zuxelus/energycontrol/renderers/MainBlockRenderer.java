package com.zuxelus.energycontrol.renderers;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockMain;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class MainBlockRenderer implements ISimpleBlockRenderingHandler {
// We need this class because we don't have metadata-sensitive version of Block.setBlockBoundsForItemRender
	private int modelId;

	public MainBlockRenderer(int modelId) {
		this.modelId = modelId;
	}

	@Override
	public void renderInventoryBlock(Block block, int meta, int model, RenderBlocks renderer) {
		if (model != modelId)
			return;

		renderer.uvRotateTop = 1;
		float[] size = BlockMain.getBlockBounds(meta, null);
		block.setBlockBounds(size[0], size[1], size[2], size[3], size[4], size[5]);
		renderer.setRenderBoundsFromBlock(block);
		Tessellator tesselator = Tessellator.instance;
		if (meta == BlockDamages.DAMAGE_RANGE_TRIGGER || meta == BlockDamages.DAMAGE_REMOTE_THERMO
				|| meta == BlockDamages.GUI_KIT_ASSEMBER || meta == BlockDamages.DAMAGE_AFSU) {
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}
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
		if (meta == BlockDamages.DAMAGE_HOWLER_ALARM || meta == BlockDamages.DAMAGE_INDUSTRIAL_ALARM
				|| meta == BlockDamages.DAMAGE_THERMAL_MONITOR)
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
		else
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

		int meta = world.getBlockMetadata(x, y, z);
		
		if (meta == BlockDamages.DAMAGE_THERMAL_MONITOR || meta == BlockDamages.DAMAGE_REMOTE_THERMO
				|| meta == BlockDamages.DAMAGE_INFO_PANEL || meta == BlockDamages.DAMAGE_INFO_PANEL_EXTENDER
				|| meta == BlockDamages.DAMAGE_ADVANCED_PANEL || meta == BlockDamages.DAMAGE_ADVANCED_EXTENDER)
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