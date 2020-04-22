package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.gui.GuiCardText;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardText extends ItemCardBase {
	public ItemCardText() {
		super(ItemCardType.CARD_TEXT, "card_text");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.card_text";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		return CardState.OK;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		boolean started = false;
		for (int i = 9; i >= 0; i--) {
			String text = reader.getString("line_" + i);
			if (text.equals("") && !started)
				continue;
			started = true;
			result.add(0, new PanelString(text));
		}
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		return null;
	}
	
	@Override
	public ICardGui getSettingsScreen(ItemCardReader reader) {
		return new GuiCardText(reader);
	}
}
