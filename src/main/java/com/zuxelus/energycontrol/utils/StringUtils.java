package com.zuxelus.energycontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

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

	@SuppressWarnings("rawtypes")
	@SideOnly(Side.CLIENT)
	public static String getItemName(ItemStack stack) {
		List list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
		if (list.size() == 0)
			return stack.getItem().getUnlocalizedName();
		return (String) list.get(0);
	}
}
