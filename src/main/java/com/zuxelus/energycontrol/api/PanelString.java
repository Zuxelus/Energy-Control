package com.zuxelus.energycontrol.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.minecraft.client.resources.I18n;

/**
 * Object defines one line of the Information Panel. Each line has contain 3
 * sections: left, center, right. At least one of them should be not null. Each
 * section can have its own color.
 * 
 * @see IItemCard#getStringData(int, ICardWrapper, boolean)
 * @author Shedar
 */
public class PanelString {
		
	public PanelString() {}
	
	public PanelString(String text) {
		textLeft = text;
	}

	public PanelString(String resourceName, double value, boolean showLabels) {
		this(resourceName, getFormatter().format(value), showLabels);
	}
	
	public PanelString(String resourceName, String value, boolean showLabels) {
		textLeft = getFormatted(resourceName, value, showLabels);
	}
	
	private static DecimalFormat formatter = null;
	private static DecimalFormat getFormatter() {
		if (formatter == null) {
			DecimalFormat lFormatter = new DecimalFormat("#,###.###");
			DecimalFormatSymbols smb = new DecimalFormatSymbols();
			smb.setGroupingSeparator(' ');
			lFormatter.setDecimalFormatSymbols(smb);
			formatter = lFormatter;
		}
		return formatter;
	}
	
	/**
	 * Text of the left aligned part of the line.
	 */
	public String textLeft;

	/**
	 * Text of the centered part of the line.
	 */
	public String textCenter;

	/**
	 * text of the right aligned part of the line.
	 */
	public String textRight;

	/**
	 * Color of the left aligned part of the line.
	 */
	public int colorLeft = 0;

	/**
	 * Color of the centered part of the line.
	 */
	public int colorCenter = 0;

	/**
	 * Color of the right aligned part of the line.
	 */
	public int colorRight = 0;

	private static String getFormatted(String resourceName, String value, boolean showLabels) {
		if (showLabels)
			return I18n.format(resourceName, value);
		return value;
	}
}