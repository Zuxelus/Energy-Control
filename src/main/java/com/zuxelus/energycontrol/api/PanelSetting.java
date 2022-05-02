package com.zuxelus.energycontrol.api;

/**
 * Object of PanelSetting class defines one checkbox in the card's settings.
 * 
 * @author Shedar
 * @see IItemCard#getSettingsList(ItemStack) 
 */
public class PanelSetting {
	/**
	 * Name of the option
	 */
	public String title;

	/**
	 * Bit number in display settings. Should be in the range 0-31.
	 */
	public int displayBit;

	/**
	 * @param title Name of the option
	 * @param displayBit Bit number in display settings. Should be in the range 0-31.
	 */
	public PanelSetting(String title, int displayBit) {
		this.title = title;
		this.displayBit = displayBit;
	}
}