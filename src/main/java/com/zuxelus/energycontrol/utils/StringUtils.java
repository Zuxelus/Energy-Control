package com.zuxelus.energycontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.minecraft.client.resources.I18n;

public class StringUtils {
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

	public static String getFormatted(String resourceName, String value, boolean showLabels) {
		if (showLabels)
			return I18n.format(resourceName, value);
		return value;
	}

	public static String getFormatted(String resourceName, double value, boolean showLabels) {
		return getFormatted(resourceName, getFormatter().format(value), showLabels);
	}

	public static String getFormattedKey(String resourceName, Object... arguments) {
		return I18n.format(resourceName, arguments);
	}
/*
	public static List<PanelString> getStateMessage(CardState state) {
		List<PanelString> result = new LinkedList<PanelString>();
		PanelString line = new PanelString();
		switch (state) {
		case OUT_OF_RANGE: line.textCenter = I18n.format("msg.ec.InfoPanelOutOfRange");
			break;
		case INVALID_CARD: line.textCenter = I18n.format("msg.ec.InfoPanelInvalidCard");
			break;
		case NO_TARGET: line.textCenter = I18n.format("msg.ec.InfoPanelNoTarget");
			break;
		case CUSTOM_ERROR:
			break;
		case OK:
			break;
		default:
			break;
		}
		result.add(line);
		return result;
	}*/
}
