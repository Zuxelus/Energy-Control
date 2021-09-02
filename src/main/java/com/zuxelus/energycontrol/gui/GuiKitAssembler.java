package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiKitAssembler extends GuiContainerBase<ContainerKitAssembler> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");

	private ContainerKitAssembler container;
	private TextFieldWidget textboxTitle;
	private boolean modified;

	public GuiKitAssembler(ContainerKitAssembler container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		this.container = container;
		ySize = 206;
	}

	protected void initControls() {
		ItemStack stack = container.te.getStackInSlot(TileEntityKitAssembler.SLOT_INFO);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			if (!modified)
				textboxTitle = addTextFieldWidget(7, 16, 162, 18, true, new ItemCardReader(stack).getTitle());
		} else {
			modified = false;
			textboxTitle = null;
		}
	}

	@Override
	public void init() {
		super.init();
		initControls();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
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
		net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
		List<ITextComponent> stackList = stack.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		List<String> list = Lists.<String>newArrayList();
		if (stackList.size() > 0)
			list.add(stackList.get(0).getFormattedText());
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(TextFormatting.GRAY + panelString.textLeft);
			}
		FontRenderer fontStack = stack.getItem().getFontRenderer(stack);
		renderTooltip(list, x, y, (fontStack == null ? font : fontStack));
		net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			blit(guiLeft + 9, guiTop + 62 + (14 - energyHeight), 176, 14 - energyHeight, 14, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (energyHeight > 0)
			blit(guiLeft + 86, guiTop + 60, 176, 15, productionWidth, 17);
		if (textboxTitle != null)
			textboxTitle.renderButton(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(title.getString(), xSize, 6);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (textboxTitle != null) {
			textboxTitle.mouseReleased(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			if (textboxTitle.isFocused())
				return true;
			func_212932_b(null);
			updateTitle();
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		super.tick();
		if (textboxTitle != null)
			textboxTitle.tick();
		initControls();
	}

	@SuppressWarnings("resource")
	protected void updateTitle() {
		if (textboxTitle == null)
			return;
		if (container.te.getWorld().isRemote) {
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("type", 4);
			tag.putInt("slot", 0);
			tag.putString("title", textboxTitle.getText());
			NetworkHelper.updateSeverTileEntity(container.te.getPos(), tag);
			ItemStack card = container.te.getStackInSlot(0);
			if (!card.isEmpty() && card.getItem() instanceof ItemCardMain)
				new ItemCardReader(card).setTitle(textboxTitle.getText());
		}
	}

	@Override
	public void onClose() {
		updateTitle();
		super.onClose();
	}

	/*@Override
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
	}*/
}
