package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.utils.PanelSetting;

public interface IPanelMultiCard {
	List<PanelSetting> getSettingsList(ItemCardMain card);

	int getCardType(ItemCardMain card);
}
