package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPortablePanel extends AbstractContainerScreen<ContainerPortablePanel> {
	private static final ResourceLocation TEXTURE = ResourceLocation.parse(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private static final int ROWS_PER_PAGE = 14;
	private int page;
	private Button previousPage;
	private Button nextPage;
	private Button toggleBars;
	private boolean showBars = true;
	private Player player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, Inventory inventory, Component title) {
		super(container, inventory, title);
		this.te = container.te;
		this.player = inventory.player;
		this.imageWidth = 226;
		this.imageHeight = 226;
	}

	@Override
	protected void init() {
		super.init();
		previousPage = addRenderableWidget(Button.builder(Component.literal("<"), button -> page--)
				.bounds(leftPos + 174, topPos + 60, 18, 18).build());
		nextPage = addRenderableWidget(Button.builder(Component.literal(">"), button -> page++)
				.bounds(leftPos + 174, topPos + 82, 18, 18).build());
		previousPage.visible = nextPage.visible = false;
		toggleBars = addRenderableWidget(Button.builder(barButtonTitle(), button -> {
			showBars = !showBars;
			button.setMessage(barButtonTitle());
		}).bounds(leftPos + 174, topPos + 106, 44, 18).build());
		toggleBars.visible = false;
	}

	private Component barButtonTitle() {
		return Component.translatable(showBars ? "gui.ec.portableBarsOn" : "gui.ec.portableBarsOff");
	}

	@Override
	public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics matrixStack, float partialTicks, int x, int y) {
		int left = (width - imageWidth) / 2;
		int top = (height - imageHeight) / 2;
		matrixStack.blit(TEXTURE, left, top, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		ItemStack stack = te.getItem(InventoryPortablePanel.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((ItemCardMain) stack.getItem()).getStringData(player.level(), Integer.MAX_VALUE, reader, false, true);

			int pageCount = joinedData.isEmpty() ? 1 : (joinedData.size() - 1) / ROWS_PER_PAGE + 1;
			page = Math.max(0, Math.min(page, pageCount - 1));
			previousPage.visible = nextPage.visible = pageCount > 1;
			previousPage.active = page > 0;
			nextPage.active = page < pageCount - 1;
			var bars = PortableProgressBars.forRows(stack, reader);
			toggleBars.visible = !bars.isEmpty();

			int firstRow = page * ROWS_PER_PAGE;
			int lastRow = Math.min(firstRow + ROWS_PER_PAGE, joinedData.size());
			for (int index = firstRow; index < lastRow; index++) {
				PanelString panelString = joinedData.get(index);
				int row = index - firstRow;
				if (showBars && bars.containsKey(index)) {
					int top = row * 10 + 9;
					matrixStack.fill(8, top, 168, top + 10, 0xFF20343A);
					matrixStack.fill(8, top, 8 + (int) Math.round(160 * bars.get(index)), top + 10, 0xFF205E36);
				}
				if (panelString.textLeft != null)
					matrixStack.drawString(font, panelString.textLeft, 9, row * 10 + 10, 0x06aee4, false);
				if (panelString.textCenter != null)
					matrixStack.drawString(font, panelString.textCenter, (168 - font.width(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4, false);
				if (panelString.textRight != null)
					matrixStack.drawString(font, panelString.textRight, 168 - font.width(panelString.textRight), row * 10 + 10, 0x06aee4, false);
			}
			if (pageCount > 1)
				matrixStack.drawString(font, (page + 1) + " / " + pageCount, 9, 150, 0x06aee4, false);
		} else {
			page = 0;
			previousPage.visible = nextPage.visible = false;
			toggleBars.visible = false;
		}
	}
}
