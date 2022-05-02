package com.zuxelus.energycontrol.items.cards;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

public class ItemCardText extends ItemCardBase {

	public ItemCardText() {
		super(ItemCardType.CARD_TEXT, "card_text");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
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
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public boolean isRemoteCard() {
		return false;
	}
}
