package com.zuxelus.energycontrol.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.blocks.HoloPanelExtender;
import com.zuxelus.energycontrol.blocks.InfoPanelExtender;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ScreenManager {

	private final Map<String, List<Screen>> screens;
	private final Map<String, List<TileEntityInfoPanel>> unusedPanels;

	public ScreenManager() {
		screens = new HashMap<String, List<Screen>>();
		unusedPanels = new HashMap<String, List<TileEntityInfoPanel>>();
	}

	private String getWorldKey(LevelAccessor world) {
		if (world == null || !(world instanceof Level))
			return "null";
		ResourceKey<Level> dimension = ((Level)world).dimension(); 
		if (dimension == null)
			return "null";
		return dimension.toString();
	}

	private void checkWorldLists(String key) {
		if (!screens.containsKey(key))
			screens.put(key, new ArrayList<Screen>());
		if (!unusedPanels.containsKey(key))
			unusedPanels.put(key, new ArrayList<TileEntityInfoPanel>());
	}

	public void clearWorld(LevelAccessor world) {
		String key = getWorldKey(world);
		if (screens.containsKey(key))
			screens.get(key).clear();
		if (unusedPanels.containsKey(key))
			unusedPanels.get(key).clear();
	}

	@SuppressWarnings("resource")
	public void registerInfoPanel(TileEntityInfoPanel panel) {
		Level world = panel.getLevel();
		if (world.isClientSide)
			return;
		checkWorldLists(getWorldKey(world));

		for (Screen screen : screens.get(getWorldKey(world)))
			if (screen.isBlockPartOf(panel)) {
				destroyScreen(screen, world); // occurs on chunk unloading/loading
				break;
			}

		Screen screen = buildFromPanel(panel);
		screens.get(getWorldKey(world)).add(screen);
	}

	private void destroyScreen(Screen screen, Level world) {
		screens.get(getWorldKey(world)).remove(screen);
		screen.destroy(true, world);
	}

	private Screen buildFromPanel(TileEntityInfoPanel panel) {
		Screen screen = new Screen(panel);
		Direction facing = panel.getFacing();
		int dx = (facing == Direction.WEST) || (facing == Direction.EAST) ? 0 : -1;
		int dy = (facing == Direction.DOWN) || (facing == Direction.UP) ? 0 : -1;
		int dz = (facing == Direction.NORTH) || (facing == Direction.SOUTH) ? 0 : -1;
		boolean advanced = panel instanceof TileEntityAdvancedInfoPanel;
		boolean holo = panel instanceof TileEntityHoloPanel;
		updateScreenBound(screen, dx, 0, 0, panel.getLevel(), advanced, holo);
		updateScreenBound(screen, -dx, 0, 0, panel.getLevel(), advanced, holo);
		updateScreenBound(screen, 0, dy, 0, panel.getLevel(), advanced, holo);
		updateScreenBound(screen, 0, -dy, 0, panel.getLevel(), advanced, holo);
		updateScreenBound(screen, 0, 0, dz, panel.getLevel(), advanced, holo);
		updateScreenBound(screen, 0, 0, -dz, panel.getLevel(), advanced, holo);
		screen.init(false, panel.getLevel());
		panel.updateData();
		return screen;
	}

	private void updateScreenBound(Screen screen, int dx, int dy, int dz, Level world, boolean advanced, boolean holo) {
		if (dx == 0 && dy == 0 && dz == 0)
			return;
		boolean isMin = dx + dy + dz < 0;
		int dir = isMin ? 1 : -1;
		int steps = 0;
		while (steps < 20) {
			int x, rx;
			int y, ry;
			int z, rz;
			if (isMin) {
				x = screen.minX + dx;
				y = screen.minY + dy;
				z = screen.minZ + dz;
			} else {
				x = screen.maxX + dx;
				y = screen.maxY + dy;
				z = screen.maxZ + dz;
			}
			rx = dx != 0 ? 0 : (screen.maxX - screen.minX);
			ry = dy != 0 ? 0 : (screen.maxY - screen.minY);
			rz = dz != 0 ? 0 : (screen.maxZ - screen.minZ);
			boolean allOk = true;
			for (int interX = 0; interX <= rx && allOk; interX++) {
				for (int interY = 0; interY <= ry && allOk; interY++) {
					for (int interZ = 0; interZ <= rz && allOk; interZ++) {
						TileEntityInfoPanel core = screen.getCore(world);
						allOk = core != null && isValidExtender(world, new BlockPos(x + dir * interX, y + dir * interY, z + dir * interZ), core.getFacing(), advanced, holo);
					}
				}
			}
			if (!allOk)
				break;
			if (isMin) {
				screen.minX += dx;
				screen.minY += dy;
				screen.minZ += dz;
			} else {
				screen.maxX += dx;
				screen.maxY += dy;
				screen.maxZ += dz;
			}
			steps++;
		}
	}

	private boolean isValidExtender(Level world, BlockPos pos, Direction facing, boolean advanced, boolean holo) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof InfoPanelExtender || block instanceof HoloPanelExtender))
			return false;
		BlockEntity te = world.getBlockEntity(pos);
		if (te.isRemoved())
			return false;
		if (!(te instanceof TileEntityInfoPanelExtender))
			return false;
		if (advanced ^ (te instanceof TileEntityAdvancedInfoPanelExtender))
			return false;
		if (holo ^ (te instanceof TileEntityHoloPanelExtender))
			return false;
		if (((TileEntityInfoPanelExtender) te).getFacing() != facing)
			return false;
		return ((IScreenPart) te).getScreen() == null;
	}

	@SuppressWarnings("resource")
	public Screen loadScreen(TileEntityInfoPanel panel) {
		if (panel.screenData == null)
			return null;
		
		Screen screen = new Screen(panel, panel.screenData);
		if (!panel.getLevel().isClientSide) {
			String key = getWorldKey(panel.getLevel());
			checkWorldLists(key);
			if (!screens.get(key).contains(screen))
				screens.get(key).add(screen);
		}
		return screen;
	}

	@SuppressWarnings("resource")
	public void registerInfoPanelExtender(TileEntityInfoPanelExtender extender) {
		if (extender.getLevel().isClientSide)
			return;
		if (!screens.containsKey(getWorldKey(extender.getLevel())))
			screens.put(getWorldKey(extender.getLevel()), new ArrayList<>());
		if (!unusedPanels.containsKey(getWorldKey(extender.getLevel())))
			unusedPanels.put(getWorldKey(extender.getLevel()), new ArrayList<>());

		List<TileEntityInfoPanel> rebuildPanels = new ArrayList<>();
		List<Screen> screensToDestroy = new ArrayList<>();

		for (Screen screen : screens.get(getWorldKey(extender.getLevel()))) {
			TileEntityInfoPanel core = screen.getCore(extender.getLevel());
			if (screen.isBlockNearby(extender) && core != null && extender.getFacing() == core.getFacing()) {
				rebuildPanels.add(core);
				screensToDestroy.add(screen);
			} else if (screen.isBlockPartOf(extender)) {
				// block is already part of the screen
				// shouldn't be registered twice
				return;
			}
		}
		for (Screen screen : screensToDestroy)
			destroyScreen(screen, extender.getLevel());

		BlockPos pos = extender.getBlockPos();
		for (TileEntityInfoPanel panel : unusedPanels.get(getWorldKey(extender.getLevel()))) {
			BlockPos posPanel = panel.getBlockPos();
			if (((posPanel.getX() == pos.getX() && posPanel.getY() == pos.getY()
					&& (posPanel.getZ() == pos.getZ() + 1 || posPanel.getZ() == pos.getZ() - 1))
					|| (posPanel.getX() == pos.getX()
							&& (posPanel.getY() == pos.getY() + 1 || posPanel.getY() == pos.getY() - 1)
							&& posPanel.getZ() == pos.getZ())
					|| ((posPanel.getX() == pos.getX() + 1 || posPanel.getX() == pos.getX() - 1)
							&& posPanel.getY() == pos.getY() && posPanel.getZ() == pos.getZ()))
					&& extender.getFacing() == panel.getFacing()) {
				rebuildPanels.add(panel);
			}
		}
		for (TileEntityInfoPanel panel : rebuildPanels) {
			Screen screen = buildFromPanel(panel);
			screens.get(getWorldKey(extender.getLevel())).add(screen);
			unusedPanels.get(getWorldKey(extender.getLevel())).remove(panel);
		}
	}

	@SuppressWarnings("resource")
	public void unregisterScreenPart(BlockEntity part) {
		if (part.getLevel().isClientSide)
			return;
		if (!screens.containsKey(getWorldKey(part.getLevel())))
			return;
		if (!unusedPanels.containsKey(getWorldKey(part.getLevel())))
			return;
		if (!(part instanceof IScreenPart))
			return;
		IScreenPart screenPart = (IScreenPart) part;
		Screen screen = screenPart.getScreen();
		if (screen == null) {
			if (part instanceof TileEntityInfoPanel)
				unusedPanels.get(getWorldKey(part.getLevel())).remove(part);
			return;
		}
		TileEntityInfoPanel core = screen.getCore(part.getLevel());
		destroyScreen(screen, part.getLevel());
		boolean isCoreDestroyed = part instanceof TileEntityInfoPanel;
		if (!isCoreDestroyed && core != null && !core.isRemoved()) {
			Screen newScreen = buildFromPanel(core);
			screens.get(getWorldKey(core.getLevel())).add(newScreen);
		}
	}
}
