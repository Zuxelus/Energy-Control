package com.zuxelus.energycontrol.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import javax.annotation.Nullable;

/**
 * A resource pack made of a folder of files named alarm-NAME.ogg, nothing else has to be in the folder.
 * The sounds.json is generated from the file names, so a player only copies the sound files.
 */
public class AlarmFolderPack extends AbstractPackResources {
	private static final String META = "{\"pack\":{\"description\":\"Energy Control custom alarms\",\"pack_format\":34}}";
	private static final String PREFIX = "alarm-";
	private static final String EXTENSION = ".ogg";

	private final Path folder;

	public AlarmFolderPack(PackLocationInfo location, Path folder) {
		super(location);
		this.folder = folder;
	}

	@Override
	public @Nullable IoSupplier<InputStream> getRootResource(String... path) {
		if (path.length == 1 && PACK_META.equals(path[0]))
			return () -> new ByteArrayInputStream(META.getBytes(StandardCharsets.UTF_8));
		return null;
	}

	@Override
	public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		if (type != PackType.CLIENT_RESOURCES || !EnergyControl.MODID.equals(location.getNamespace()))
			return null;
		String path = location.getPath();
		if ("sounds.json".equals(path)) {
			byte[] json = createSoundsJson();
			return () -> new ByteArrayInputStream(json);
		}
		if (path.startsWith("sounds/") && isAlarmFile(path.substring("sounds/".length()))) {
			Path file = folder.resolve(path.substring("sounds/".length()));
			return Files.isRegularFile(file) ? IoSupplier.create(file) : null;
		}
		return null;
	}

	@Override
	public void listResources(PackType type, String namespace, String directory, PackResources.ResourceOutput output) {
		if (type != PackType.CLIENT_RESOURCES || !EnergyControl.MODID.equals(namespace) || !"sounds".equals(directory))
			return;
		for (String file : getAlarmFiles())
			output.accept(ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "sounds/" + file), IoSupplier.create(folder.resolve(file)));
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? Set.of(EnergyControl.MODID) : Set.of();
	}

	@Override
	public void close() { }

	// the name of the sound is the same as of the file, a resource path can have only these characters
	private static boolean isAlarmFile(String name) {
		return name.startsWith(PREFIX) && name.endsWith(EXTENSION) && name.length() > PREFIX.length() + EXTENSION.length() && name.matches("[a-z0-9._-]+");
	}

	private List<String> getAlarmFiles() {
		List<String> result = new ArrayList<>();
		try (Stream<Path> files = Files.list(folder)) {
			files.filter(Files::isRegularFile).map(file -> file.getFileName().toString()).filter(AlarmFolderPack::isAlarmFile).sorted().forEach(result::add);
		} catch (IOException e) {
			EnergyControl.LOGGER.warn("Can't read the alarms folder {}", folder, e);
		}
		return result;
	}

	private byte[] createSoundsJson() {
		JsonObject root = new JsonObject();
		for (String file : getAlarmFiles()) {
			String name = file.substring(0, file.length() - EXTENSION.length());
			JsonObject sound = new JsonObject();
			sound.addProperty("name", EnergyControl.MODID + ":" + name);
			sound.addProperty("stream", true);
			JsonArray sounds = new JsonArray();
			sounds.add(sound);
			JsonObject event = new JsonObject();
			event.addProperty("category", "master");
			event.add("sounds", sounds);
			root.add(name, event);
		}
		return root.toString().getBytes(StandardCharsets.UTF_8);
	}
}
