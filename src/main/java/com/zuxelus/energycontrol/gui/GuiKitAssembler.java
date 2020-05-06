package com.zuxelus.energycontrol.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKitAssembler extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_kit_assembler.png");

	private String name;
	private ContainerKitAssembler container;
	private ItemStack prevStack;
	private GuiTextField textboxTitle;
	private boolean modified;

	public GuiKitAssembler(ContainerKitAssembler container) {
		super(container);
		this.container = container;
		ySize = 206;
		name = I18n.format("tile.kit_assembler.name");
		prevStack = ItemStack.EMPTY;
	}

	protected void initControls() {
		ItemStack stack = container.te.getStackInSlot(TileEntityKitAssembler.SLOT_INFO);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			if (!modified) {
				textboxTitle = new GuiTextField(0, fontRenderer, 7, 16, 162, 18);
				textboxTitle.setFocused(true);
				textboxTitle.setText(new ItemCardReader(stack).getTitle());
			}
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		initControls();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		Slot slot = container.getSlot(TileEntityKitAssembler.SLOT_INFO);
		if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY) && slot.isEnabled())
			renderInfoToolTip(slot, mouseX, mouseY);
		else
			renderHoveredToolTip(mouseX, mouseY);
	}

	private void renderInfoToolTip(Slot slot, int x, int y) {
		ItemStack stack = slot.getStack();
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return;
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		List<String> stackList = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		List<String> list = Lists.<String>newArrayList();
		if (stackList.size() > 0)
			list.add((String) stackList.get(0));
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(TextFormatting.GRAY + panelString.textLeft);
			}
		drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		
		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			drawTexturedModalRect(left + 9, top + 62 + (14 - energyHeight), 176, 14 - energyHeight, 14, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (energyHeight > 0)
			drawTexturedModalRect(left + 86, top + 60, 176, 15, productionWidth, 17);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
		if (textboxTitle != null)
			textboxTitle.drawTextBox();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (textboxTitle != null) {
			boolean focused = textboxTitle.isFocused();
			textboxTitle.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			if (textboxTitle.isFocused() != focused)
				updateTitle();
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (textboxTitle != null)
			textboxTitle.updateCursorCounter();
		initControls();
	}

	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (container.te.getWorld().isRemote) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 4);
			tag.setInteger("slot", 0);
			tag.setString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(container.te.getPos(), tag);
			ItemStack card = container.te.getStackInSlot(0);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void onGuiClosed() {
		updateTitle();
		super.onGuiClosed();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (textboxTitle != null && textboxTitle.isFocused())
			if (keyCode == 1)
				mc.player.closeScreen();
			else if (typedChar == 13)
				updateTitle();
			else {
				modified = true;
				textboxTitle.textboxKeyTyped(typedChar, keyCode);
			}
		else
			super.keyTyped(typedChar, keyCode);
	}
}
