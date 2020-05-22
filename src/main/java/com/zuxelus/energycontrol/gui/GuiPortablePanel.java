package com.zuxelus.energycontrol.gui;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ItemStack stack = te.getStackInSlot(InventoryPortablePanel.SLOT_CARD);
		if (stack != null && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = reader.getStateMessage(state);
			else
				joinedData = ItemCardMain.getStringData(Integer.MAX_VALUE, reader, true);

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

	private List<PanelString> getRemoteCustomMSG() {
		List<PanelString> result = new LinkedList<PanelString>();
		PanelString line = new PanelString();
		line.textCenter = I18n.format("nc.msg.notValid");
		result.add(line);
		line = new PanelString();
		line.textCenter = I18n.format("nc.msg.notValid2");
		result.add(line);
		line = new PanelString();
		line.textCenter = "";
		result.add(line);
		line = new PanelString();
		line.textCenter = I18n.format("nc.msg.notValid3");
		result.add(line);
		return result;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (mc.thePlayer.getHeldItemMainhand() == null)
			mc.thePlayer.closeScreen();
	}
}
