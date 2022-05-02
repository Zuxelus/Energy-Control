package com.zuxelus.energycontrol.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiPortablePanel extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container) {
		super(container);
		this.te = container.te;
		this.xSize = 226;
		this.ySize = 226;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ItemStack stack = te.getStackInSlot(InventoryPortablePanel.SLOT_CARD);
		if (ItemCardMain.isCard(stack)) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ItemCardMain.getStringData(Integer.MAX_VALUE, reader, false, true);

			int row = 0;
			for (PanelString panelString : joinedData) {
				if (row < 14) {
					if (panelString.textLeft != null)
						fontRendererObj.drawString(panelString.textLeft, 9, row * 10 + 10, 0x06aee4);
					if (panelString.textCenter != null)
						fontRendererObj.drawString(panelString.textCenter, (168 - fontRendererObj.getStringWidth(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4);
					if (panelString.textRight != null)
						fontRendererObj.drawString(panelString.textRight, 168 - fontRendererObj.getStringWidth(panelString.textRight), row * 10 + 10, 0x06aee4);
				} else if (row == 14)
					fontRendererObj.drawString("...", 9, row * 10 + 10, 0x06aee4);
				row++;
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (mc.thePlayer.getHeldItem() == null)
			mc.thePlayer.closeScreen();
	}
}
