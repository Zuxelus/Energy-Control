package com.zuxelus.energycontrol.gui;

import java.util.List;
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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiPortablePanel extends HandledScreen<ContainerPortablePanel> {
	private static final Identifier TEXTURE = Identifier.of(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private static final int ROWS_PER_PAGE = 14;
	private int page;
	private ButtonWidget previousPage;
	private ButtonWidget nextPage;
	private ToggleButton toggleBars;
	private boolean showBars;
	private PlayerEntity player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
		this.te = container.te;
		this.showBars = container.getShowBars();
		this.player = inventory.player;
		this.backgroundWidth = 226;
		this.backgroundHeight = 226;
	}

	@Override
	protected void init() {
		super.init();
		previousPage = addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> page--)
				.dimensions(x + 174, y + 53, 16, 16).build());
		nextPage = addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> page++)
				.dimensions(x + 174, y + 71, 16, 16).build());
		previousPage.visible = nextPage.visible = false;
		toggleBars = addDrawableChild(new ToggleButton(x + 174, y + 89, 16, 16, Text.literal("B"), showBars, button -> {
			showBars = !showBars;
			toggleBars.setPressed(showBars);
			handler.setShowBars(showBars);
			NetworkHelper.sendToServer(new PacketPortableBars(showBars));
			button.setTooltip(Tooltip.of(barButtonTitle()));
		}));
		toggleBars.setTooltip(Tooltip.of(barButtonTitle()));
		toggleBars.visible = false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean result = super.mouseClicked(mouseX, mouseY, button);
		// a clicked button keeps focus and stays highlighted after the mouse leaves it
		if (getFocused() instanceof ButtonWidget)
			setFocused(null);
		return result;
	}

	private Text barButtonTitle() {
		return Text.translatable(showBars ? "gui.ec.portableBarsOn" : "gui.ec.portableBarsOff");
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float partialTicks, int x, int y) {
		int left = (width - backgroundWidth) / 2;
		int top = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, left, top, 0, 0, backgroundWidth, backgroundHeight);
	}

	@Override
	protected void drawForeground(DrawContext context, int x, int y) {
		ItemStack stack = te.getStack(InventoryPortablePanel.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((ItemCardMain) stack.getItem()).getStringData(player.getWorld(), Integer.MAX_VALUE, reader, false, true);

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
					context.drawText(textRenderer, panelString.textLeft, 9, row * 10 + 10, 0x06aee4, false);
				if (panelString.textCenter != null)
					context.drawText(textRenderer, panelString.textCenter, (168 - textRenderer.getWidth(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4, false);
				if (panelString.textRight != null)
					context.drawText(textRenderer, panelString.textRight, 168 - textRenderer.getWidth(panelString.textRight), row * 10 + 10, 0x06aee4, false);
			}
			if (pageCount > 1)
				context.drawText(textRenderer, (page + 1) + " / " + pageCount, 9, 150, 0x06aee4, false);
		} else {
			page = 0;
			previousPage.visible = nextPage.visible = false;
			toggleBars.visible = false;
		}
	}
}
