package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemCardToggle extends ItemCardBase implements ITouchAction {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/remote_thermo/all.png");

	public ItemCardToggle() {
		super(ItemCardType.CARD_TOGGLE, "card_toggle");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		Block block = world.getBlock(target.posX, target.posY, target.posZ);
		if (block == null)
			return CardState.NO_TARGET;
		
		if (block == Blocks.lever || block instanceof BlockButton) {
			boolean value = (world.getBlockMetadata(target.posX, target.posY, target.posZ) & 8) > 0;
			reader.setBoolean("value", value);
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
	public int getKitFromCard() {
		return ItemCardType.KIT_TOGGLE;
	}

	@Override
	public void runTouchAction(World world, ICardReader reader) {
		ChunkCoordinates pos = reader.getTarget();
		if (pos == null)
			return;

		Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
		if (block == Blocks.lever) {
			int metadata = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
			int meta = metadata & 7;
			int k1 = 8 - (metadata & 8);
			world.setBlockMetadataWithNotify(pos.posX, pos.posY, pos.posZ, meta + k1, 3);
			world.notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ, block);

			if (meta == 1)
				world.notifyBlocksOfNeighborChange(pos.posX - 1, pos.posY, pos.posZ, block);
			else if (meta == 2)
				world.notifyBlocksOfNeighborChange(pos.posX + 1, pos.posY, pos.posZ, block);
			else if (meta == 3)
				world.notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ - 1, block);
			else if (meta == 4)
				world.notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ + 1, block);
			else if (meta != 5 && meta != 6) {
				if (meta == 0 || meta == 7)
					world.notifyBlocksOfNeighborChange(pos.posX, pos.posY + 1, pos.posZ, block);
			} else
				world.notifyBlocksOfNeighborChange(pos.posX, pos.posY - 1, pos.posZ, block);
		}
		if (block instanceof BlockButton) {
			int metadata = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
			int meta = metadata & 7;
			int k1 = 8 - (metadata & 8);

			if (k1 != 0) {
				world.setBlockMetadataWithNotify(pos.posX, pos.posY, pos.posZ, meta + k1, 3);
				world.markBlockRangeForRenderUpdate(pos.posX, pos.posY, pos.posZ, pos.posX, pos.posY, pos.posZ);
				world.notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ, block);

				if (meta == 1)
					world.notifyBlocksOfNeighborChange(pos.posX - 1, pos.posY, pos.posZ, block);
				else if (meta == 2)
					world.notifyBlocksOfNeighborChange(pos.posX + 1, pos.posY, pos.posZ, block);
				else if (meta == 3)
					world.notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ - 1, block);
				else if (meta == 4)
					world.notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ + 1, block);
				else
					world.notifyBlocksOfNeighborChange(pos.posX, pos.posY - 1, pos.posZ, block);
				world.scheduleBlockUpdate(pos.posX, pos.posY, pos.posZ, block, block.tickRate(world));
			}
		}
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
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + height, z, textureX + 0, textureY + height);
		tessellator.addVertexWithUV(x + width, y + height, z, textureX + width, textureY + height);
		tessellator.addVertexWithUV(x + width, y + 0, z, textureX + width, textureY + 0);
		tessellator.addVertexWithUV(x + 0, y + 0, z, textureX + 0, textureY + 0);
		tessellator.draw();
	}
}
