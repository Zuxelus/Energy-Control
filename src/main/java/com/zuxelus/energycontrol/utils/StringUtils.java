package com.zuxelus.energycontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

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
			return I18n.translate(resourceName, value);
		return value;
	}

	public static String getFormatted(String resourceName, double value, boolean showLabels) {
		return getFormatted(resourceName, getFormatter().format(value), showLabels);
	}

	public static String getFormattedKey(String resourceName, Object... arguments) {
		return I18n.translate(resourceName, arguments);
	}

	@Environment(EnvType.CLIENT)
	public static String getItemName(ItemStack stack) {
		List<Text> list = stack.getTooltip(MinecraftClient.getInstance().player, TooltipContext.Default.NORMAL);
		if (list.size() == 0)
			return stack.getItem().getTranslationKey();
		return list.get(0).getString();// .getFormattedText();
	}
}
