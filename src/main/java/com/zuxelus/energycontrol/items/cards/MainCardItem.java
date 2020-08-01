package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardGui;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MainCardItem extends Item {
	public static final int LOCATION_RANGE = 8;

	public MainCardItem() {
		super(new Item.Settings().group(EnergyControl.ITEM_GROUP).maxCount(1));
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		ItemCardReader reader = new ItemCardReader(stack);
		String title = reader.getTitle();
		if (title != null && !title.isEmpty())
			tooltip.add(new LiteralText(title));
		switch (((MainCardItem) stack.getItem()).getCradType()) {
		case ItemCardType.CARD_TEXT:
		case ItemCardType.CARD_TIME:
			return;
		case ItemCardType.CARD_ENERGY_ARRAY:
		case ItemCardType.CARD_LIQUID_ARRAY:
		case ItemCardType.CARD_GENERATOR_ARRAY:
			tooltip.add(new TranslatableText("msg.ec.cards", reader.getCardCount()));
			return;
		case ItemCardType.CARD_TOGGLE:
			tooltip.add(new TranslatableText("msg.ec.touch_card"));
		}

		BlockPos target = reader.getTarget();
		if (target != null)
			tooltip.add(new LiteralText(String.format("x: %d, y: %d, z: %d", target.getX(), target.getY(), target.getZ())));
	}

	public abstract List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean showLabels);

	@Environment(EnvType.CLIENT)
	public abstract List<PanelSetting> getSettingsList();

	public CardState updateCardNBT(World world, BlockPos pos, ICardReader reader, ItemStack upgradeStack) {
		int upgradeCountRange = 0;
		/*if (upgradeStack != ItemStack.EMPTY && upgradeStack.getItem() instanceof ItemUpgrade && upgradeStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
			upgradeCountRange = upgradeStack.getCount();*/

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

	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		return CardState.INVALID_CARD;
	}

	public ICardGui getSettingsScreen(ItemCardReader reader) {
		return null;
	}

	public boolean isRemoteCard() {
		return false;
	}

	public int getKitFromCard() {
		return -1;
	}
	
	public abstract int getCradType();
}
