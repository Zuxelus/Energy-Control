package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.ServerTickHandler;
import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemCardMain extends Item implements IItemCard {
	public static final int LOCATION_RANGE = 8;

	public ItemCardMain() {
		super(new Item.Properties().group(EnergyControl.ITEM_GROUP).maxStackSize(1).setNoRepair());
	}

	public static boolean isCard(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IItemCard;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	protected void addInformation(ItemCardReader reader, List<ITextComponent> tooltip) { }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		ItemCardReader reader = new ItemCardReader(stack);
		String title = reader.getTitle();
		if (title != null && !title.isEmpty())
			tooltip.add(new TranslationTextComponent(title));

		addInformation(reader, tooltip);

		BlockPos target = reader.getTarget();
		if (target != null)
			tooltip.add(new TranslationTextComponent(String.format("x: %d, y: %d, z: %d", target.getX(), target.getY(), target.getZ())));
		int count = reader.getCardCount();
		if (count > 0)
			tooltip.add(new TranslationTextComponent(I18n.format("msg.ec.cards", reader.getCardCount())));
	}

	public CardState updateCardNBT(World world, BlockPos pos, ICardReader reader, ItemStack upgradeStack) {
		int upgradeCountRange = 0;
		if (upgradeStack != ItemStack.EMPTY && upgradeStack.getItem().equals(ModItems.upgrade_range.get()))
			upgradeCountRange = upgradeStack.getCount();

		boolean needUpdate = true;
		int range = LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7));

		CardState state = CardState.INVALID_CARD;
		if (!ConfigHandler.DISABLE_RANGE_CHECK.get() && isRemoteCard()) {
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
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
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
			if (((ITouchAction) cardStack.getItem()).runTouchAction(panel.getWorld(), reader, stack))
				reader.updateClient(cardStack, panel, slot);
		}
	}

	protected void addOnOff(List<PanelString> result, boolean isServer, boolean value) {
		String text;
		int txtColor = 0;
		if (value) {
			txtColor = 0x00ff00;
			text = isServer ? "On" : I18n.format("msg.ec.InfoPanelOn");
		} else {
			txtColor = 0xff0000;
			text = isServer ? "Off" : I18n.format("msg.ec.InfoPanelOff");
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

	public static void sendCardToWS(List<PanelString> list, ICardReader reader) {
		if (ConfigHandler.WS_HOST.get().isEmpty())
			return;
		String id = reader.getId();
		JsonObject json = new JsonObject();
		json.addProperty("id", id);
		JsonArray array = new JsonArray();
		for (PanelString panelString : list) {
			JsonObject line = new JsonObject();
			if (panelString.textLeft != null) {
				line.addProperty("left", panelString.textLeft);
				line.addProperty("left_color", panelString.colorLeft);
			}
			if (panelString.textCenter != null) {
				line.addProperty("center", panelString.textCenter);
				line.addProperty("center_color", panelString.colorCenter);
			}
			if (panelString.textRight != null) {
				line.addProperty("right", panelString.textRight);
				line.addProperty("right_color", panelString.colorRight);
			}
			array.add(line);
		}
		json.add("lines", array);
		ServerTickHandler.instance.cards.put(id, json);
	}
}
