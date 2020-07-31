package com.zuxelus.energycontrol.api;

import com.zuxelus.energycontrol.items.cards.ItemCardSettingsReader;

/**
 * Interface for card's gui. Used to set wrapper object.
 * 
 * @author Shedar
 *
 */
public interface ICardGui {
	/**
	 * Method sets wrapper object, which should be used to store new settings and
	 * return to the Information Panel gui.
	 * 
	 * @param wrapper
	 * @see ICardSettingsWrapper
	 */
	void setCardSettingsHelper(ItemCardSettingsReader wrapper);
}
