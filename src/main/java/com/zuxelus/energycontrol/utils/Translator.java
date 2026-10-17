package com.zuxelus.energycontrol.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.minecraft.locale.Language;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Translations that also work on a dedicated server, where no client language is loaded.
 * On a physical client (the integrated server included) the language of the player is used;
 * on a dedicated server the mod reads its own lang file chosen by the serverLanguage setting.
 */
public final class Translator {
	private static final String DEFAULT_LANGUAGE = "en_us";
	private static final boolean DEDICATED_SERVER = FMLEnvironment.dist == Dist.DEDICATED_SERVER;

	private static String loadedLanguage;
	private static Map<String, String> selected = Map.of();
	private static Map<String, String> fallback = Map.of();

	public static String get(String key, Object... args) {
		String pattern = pattern(key);
		try {
			return String.format(pattern, args);
		} catch (IllegalFormatException e) {
			return "Format error: " + pattern;
		}
	}

	private static String pattern(String key) {
		if (DEDICATED_SERVER) {
			load();
			String value = selected.get(key);
			if (value == null)
				value = fallback.get(key);
			if (value != null)
				return value;
		}
		return Language.getInstance().getOrDefault(key);
	}

	private static String configuredLanguage() {
		try {
			String code = ConfigHandler.SERVER_LANGUAGE.get().trim().toLowerCase(Locale.ROOT);
			return code.isEmpty() ? DEFAULT_LANGUAGE : code;
		} catch (IllegalStateException e) { // the config is not loaded yet
			return DEFAULT_LANGUAGE;
		}
	}

	private static synchronized void load() {
		String code = configuredLanguage();
		if (code.equals(loadedLanguage))
			return;
		fallback = read(DEFAULT_LANGUAGE);
		selected = code.equals(DEFAULT_LANGUAGE) ? fallback : read(code);
		loadedLanguage = code;
	}

	private static Map<String, String> read(String code) {
		Map<String, String> map = new HashMap<>();
		try (InputStream in = Translator.class.getResourceAsStream("/assets/energycontrol/lang/" + code + ".json")) {
			if (in == null)
				return map;
			JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : json.entrySet())
				if (entry.getValue().isJsonPrimitive())
					map.put(entry.getKey(), entry.getValue().getAsString());
		} catch (Exception e) { }
		return map;
	}

	private Translator() { }
}
