package com.zuxelus.energycontrol.items.cards;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import java.util.ArrayList;

import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class ItemCardMain extends Item implements IItemCard {
	public static final int LOCATION_RANGE = 8;

	public ItemCardMain() {
		super(ModItems.itemSettings().stacksTo(1));
	}

	public static boolean isCard(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IItemCard;
	}

	protected void addInformation(ItemCardReader reader, List<Component> tooltip) { }

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag type) {
		List<Component> tooltip = new ArrayList<>();
		ItemCardReader reader = new ItemCardReader(stack);
		String title = reader.getTitle();
		if (title != null && !title.isEmpty())
			tooltip.add(Component.translatable(title));

		addInformation(reader, tooltip);

		BlockPos target = reader.getTarget();
		if (target != null)
			tooltip.add(Component.translatable(String.format("x: %d, y: %d, z: %d", target.getX(), target.getY(), target.getZ())));
		int count = reader.getCardCount();
		if (count > 0)
			tooltip.add(Component.translatable(I18n.get("msg.ec.cards", reader.getCardCount())));
		tooltip.forEach(builder);
	}

	public CardState updateCardNBT(Level world, BlockPos pos, ICardReader reader, ItemStack upgradeStack) {
		int upgradeCountRange = 0;
		if (upgradeStack != ItemStack.EMPTY && upgradeStack.getItem().equals(ModItems.upgrade_range))
			upgradeCountRange = upgradeStack.getCount();

		boolean needUpdate = true;
		int range = LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7));

		CardState state = CardState.INVALID_CARD;
		if (isRemoteCard()) {
			BlockPos target = reader.getTarget();
			if (target != null) {
				int dx = target.getX() - pos.getX();
				int dy = target.getY() - pos.getY();
				int dz = target.getZ() - pos.getZ();
				if (Math.abs(dx) > range || Math.abs(dy) > range || Math.abs(dz) > range) {
					needUpdate = false;
					state = CardState.OUT_OF_RANGE;
				}
			} else
				needUpdate = false;
		}

		if (needUpdate)
			state = update(world, reader, range, pos);
		reader.setState(state);
		return state;
	}

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
		return CardState.OK;
	}

	@Override
	public boolean isRemoteCard() {
		return false;
	}

	protected BlockPos getCoordinates(ICardReader reader, int cardNumber) {
		if (cardNumber >= reader.getCardCount())
			return null;
		return new BlockPos(reader.getInt(String.format("_%dx", cardNumber)),
				reader.getInt(String.format("_%dy", cardNumber)), reader.getInt(String.format("_%dz", cardNumber)));
	}

	public void runTouchAction(TileEntityInfoPanel panel, ItemStack cardStack, ItemStack stack, int slot) { 
		if (cardStack.getItem() instanceof ITouchAction) {
			ICardReader reader = new ItemCardReader(cardStack);
			if (((ITouchAction) cardStack.getItem()).runTouchAction(panel.getLevel(), reader, stack))
				reader.updateClient(cardStack, panel, slot);
		}
	}

	protected void addOnOff(List<PanelString> result, boolean isServer, boolean value) {
		String text;
		int txtColor = 0;
		if (value) {
			txtColor = 0x00ff00;
			text = isServer ? "On" : I18n.get("msg.ec.InfoPanelOn");
		} else {
			txtColor = 0xff0000;
			text = isServer ? "Off" : I18n.get("msg.ec.InfoPanelOff");
		}
		if (result.size() > 0) {
			PanelString firstLine = result.get(0);
			if (firstLine.textCenter == null) {
				firstLine.textRight = text;
				firstLine.colorRight = txtColor;
				return;
			}
			if (result.size() > 1) {
				firstLine = result.get(1);
				firstLine.textRight = text;
				firstLine.colorRight = txtColor;
				return;
			}
		}
		PanelString line = new PanelString();
		line.textLeft = text;
		line.colorLeft = txtColor;
		result.add(line);
	}
}
