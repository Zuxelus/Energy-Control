package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

public class ItemCardText extends ItemCardBase {

	public ItemCardText() {
		super(ItemCardType.CARD_TEXT, "card_text");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<>();
		boolean started = false;
		for (int i = 9; i >= 0; i--) {
			String text = reader.getString("line_" + i);
			if (text.equals("") && !started)
				continue;
			started = true;
			result.add(0, new PanelString(text.replace("@", "\u00a7").replace("\u00a7\u00a7", "@")));
		}
		String title = reader.getTitle();
		if (title != null && !title.isEmpty()) {
			PanelString titleString = new PanelString();
			titleString.textCenter = title.replace("@", "\u00a7").replace("\u00a7\u00a7", "@");
			result.add(0, titleString);
		}
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		return null;
	}

	@Override
	public boolean isRemoteCard(ItemStack stack) {
		return false;
	}
}
