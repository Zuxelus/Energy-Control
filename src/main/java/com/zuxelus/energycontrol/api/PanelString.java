package com.zuxelus.energycontrol.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.minecraft.client.resources.language.I18n;

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

	public PanelString(String resourceName, double value, String eu, boolean showLabels) {
		this(resourceName, String.format("%s %s", getFormatter().format(value), eu), showLabels);
	}

	private static DecimalFormat formatter;
	public static DecimalFormat getFormatter() {
		if (formatter == null) {
			DecimalFormat lFormatter = new DecimalFormat("#,###.###");
			DecimalFormatSymbols smb = new DecimalFormatSymbols();
			smb.setGroupingSeparator(' ');
			lFormatter.setDecimalFormatSymbols(smb);
			formatter = lFormatter;
		}
		return formatter;
	}

	public String textLeft;

	public String textCenter;

	public String textRight;

	public int colorLeft;

	public int colorCenter;

	public int colorRight;

	private static String getFormatted(String resourceName, String value, boolean showLabels) {
		if (showLabels)
			return I18n.get(resourceName, value);
		return value;
	}
}