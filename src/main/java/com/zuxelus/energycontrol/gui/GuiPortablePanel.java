package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.api.IItemCard;
import java.util.List;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.gui.controls.ToggleButton;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.network.PacketPortableBars;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GuiPortablePanel extends AbstractContainerScreen<ContainerPortablePanel> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private static final int ROWS_PER_PAGE = 14;
	private int page;
	private Button previousPage;
	private Button nextPage;
	private ToggleButton toggleBars;
	private boolean showBars;
	private Player player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, Inventory inventory, Component title) {
		super(container, inventory, title, 226, 226);
		this.te = container.te;
		this.showBars = container.getShowBars();
		this.player = inventory.player;
	}

	@Override
	protected void init() {
		super.init();
		previousPage = addRenderableWidget(Button.builder(Component.literal("<"), button -> page--)
				.bounds(leftPos + 174, topPos + 53, 16, 16).build());
		nextPage = addRenderableWidget(Button.builder(Component.literal(">"), button -> page++)
				.bounds(leftPos + 174, topPos + 71, 16, 16).build());
		previousPage.visible = nextPage.visible = false;
		toggleBars = addRenderableWidget(new ToggleButton(leftPos + 174, topPos + 89, 16, 16, Component.literal("B"), showBars, button -> {
			showBars = !showBars;
			toggleBars.setPressed(showBars);
			menu.setShowBars(showBars);
			NetworkHelper.sendToServer(new PacketPortableBars(showBars));
			button.setTooltip(Tooltip.create(barButtonTitle()));
		}));
		toggleBars.setTooltip(Tooltip.create(barButtonTitle()));
		toggleBars.visible = false;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean result = super.mouseClicked(event, doubleClick);
		// a clicked button keeps focus and stays highlighted after the mouse leaves it
		if (getFocused() instanceof Button)
			setFocused(null);
		return result;
	}

	private Component barButtonTitle() {
		return Component.translatable(showBars ? "gui.ec.portableBarsOn" : "gui.ec.portableBarsOff");
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(context, mouseX, mouseY, partialTicks);
		extractTooltip(context, mouseX, mouseY);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int x, int y, float partialTicks) {
		super.extractBackground(context, x, y, partialTicks);
		int left = (width - imageWidth) / 2;
		int top = (height - imageHeight) / 2;
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0, 0, imageWidth, imageHeight, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor context, int x, int y) {
		ItemStack stack = te.getItem(InventoryPortablePanel.SLOT_CARD);
		if (ItemCardMain.isCard(stack)) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((IItemCard) stack.getItem()).getStringData(player.level(), Integer.MAX_VALUE, reader, false, true);

			int pageCount = joinedData.isEmpty() ? 1 : (joinedData.size() - 1) / ROWS_PER_PAGE + 1;
			page = Math.max(0, Math.min(page, pageCount - 1));
			previousPage.visible = nextPage.visible = pageCount > 1;
			previousPage.active = page > 0;
			nextPage.active = page < pageCount - 1;
			Map<Integer, Double> bars = PortableProgressBars.forRows(stack, reader);
			toggleBars.visible = !bars.isEmpty();

			int firstRow = page * ROWS_PER_PAGE;
			int lastRow = Math.min(firstRow + ROWS_PER_PAGE, joinedData.size());
			for (int index = firstRow; index < lastRow; index++) {
				PanelString panelString = joinedData.get(index);
				int row = index - firstRow;
				if (showBars && bars.containsKey(index)) {
					int top = row * 10 + 9;
					context.fill(8, top, 168, top + 10, 0xFF20343A);
					context.fill(8, top, 8 + (int) Math.round(160 * bars.get(index)), top + 10, 0xFF205E36);
				}
				if (panelString.textLeft != null)
					context.text(font, panelString.textLeft, 9, row * 10 + 10, 0xFF06aee4, false);
				if (panelString.textCenter != null)
					context.text(font, panelString.textCenter, (168 - font.width(panelString.textCenter)) / 2, row * 10 + 10, 0xFF06aee4, false);
				if (panelString.textRight != null)
					context.text(font, panelString.textRight, 168 - font.width(panelString.textRight), row * 10 + 10, 0xFF06aee4, false);
			}
			if (pageCount > 1)
				context.text(font, (page + 1) + " / " + pageCount, 9, 150, 0xFF06aee4, false);
		} else {
			page = 0;
			previousPage.visible = nextPage.visible = false;
			toggleBars.visible = false;
		}
	}
}
