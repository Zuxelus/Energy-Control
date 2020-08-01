package com.zuxelus.energycontrol.blockentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.blocks.InfoPanelExtenderBlock;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ScreenManager {

	private final Map<Integer, List<Screen>> screens;
	private final Map<Integer, List<InfoPanelBlockEntity>> unusedPanels;

	public ScreenManager() {
		screens = new HashMap<Integer, List<Screen>>();
		unusedPanels = new HashMap<Integer, List<InfoPanelBlockEntity>>();
	}

	private int getWorldKey(World world) {
		if (world == null)
			return -10;
		if (world.dimension == null)
			return -10;
		return world.dimension.getType().getRawId();
	}

	private void checkWorldLists(Integer key) {
		if (!screens.containsKey(key))
			screens.put(key, new ArrayList<Screen>());
		if (!unusedPanels.containsKey(key))
			unusedPanels.put(key, new ArrayList<InfoPanelBlockEntity>());
	}

	public void clearWorld(World world) {
		Integer key = getWorldKey(world);
		if (screens.containsKey(key))
			screens.get(key).clear();
		if (unusedPanels.containsKey(key))
			unusedPanels.get(key).clear();
	}

	public void registerInfoPanel(InfoPanelBlockEntity panel) {
		if (panel.getWorld().isClient)
			return;
		checkWorldLists(getWorldKey(panel.getWorld()));

		for (Screen screen : screens.get(getWorldKey(panel.getWorld())))
			if (screen.isBlockPartOf(panel)) {
				destroyScreen(screen, panel.getWorld()); // occurs on chunk unloading/loading
				break;
			}

		Screen screen = buildFromPanel(panel);
		screens.get(getWorldKey(panel.getWorld())).add(screen);
	}

	private void destroyScreen(Screen screen, World world) {
		screens.get(getWorldKey(world)).remove(screen);
		screen.destroy(true, world);
	}

	private Screen buildFromPanel(InfoPanelBlockEntity panel) {
		Screen screen = new Screen(panel);
		Direction facing = panel.getFacing();
		int dx = (facing == Direction.WEST) || (facing == Direction.EAST) ? 0 : -1;
		int dy = (facing == Direction.DOWN) || (facing == Direction.UP) ? 0 : -1;
		int dz = (facing == Direction.NORTH) || (facing == Direction.SOUTH) ? 0 : -1;
		boolean advanced = false; //panel instanceof TileEntityAdvancedInfoPanel;
		updateScreenBound(screen, dx, 0, 0, panel.getWorld(), advanced);
		updateScreenBound(screen, -dx, 0, 0, panel.getWorld(), advanced);
		updateScreenBound(screen, 0, dy, 0, panel.getWorld(), advanced);
		updateScreenBound(screen, 0, -dy, 0, panel.getWorld(), advanced);
		updateScreenBound(screen, 0, 0, dz, panel.getWorld(), advanced);
		updateScreenBound(screen, 0, 0, -dz, panel.getWorld(), advanced);
		screen.init(false, panel.getWorld());
		panel.updateData();
		return screen;
	}

	private void updateScreenBound(Screen screen, int dx, int dy, int dz, World world, boolean advanced) {
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
						InfoPanelBlockEntity core = screen.getCore(world);
						allOk = core != null && isValidExtender(world, new BlockPos(x + dir * interX, y + dir * interY, z + dir * interZ), core.getFacing(), advanced);
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

	private boolean isValidExtender(World world, BlockPos pos, Direction facing, boolean advanced) {
		if (!(world.getBlockState(pos).getBlock() instanceof InfoPanelExtenderBlock))
			return false;
		BlockEntity be = world.getBlockEntity(pos);
		if (!(be instanceof InfoPanelExtenderBlockEntity))
			return false;
		/*if (advanced ^ (tileEntity instanceof TileEntityAdvancedInfoPanelExtender))
			return false;*/
		if (((InfoPanelExtenderBlockEntity) be).getFacing() != facing)
			return false;
		if (((IScreenPart) be).getScreen() != null)
			return false;
		return true;
	}

	public Screen loadScreen(InfoPanelBlockEntity panel) {
		if (panel.screenData == null)
			return null;
		
		Screen screen = new Screen(panel, panel.screenData);
		if (!panel.getWorld().isClient) {
			Integer key = getWorldKey(panel.getWorld());
			checkWorldLists(key);
			if (!screens.get(key).contains(screen))
				screens.get(key).add(screen);
		}
		return screen;
	}

	public void registerInfoPanelExtender(InfoPanelExtenderBlockEntity extender) {
		if (extender.getWorld().isClient)
			return;
		if (!screens.containsKey(getWorldKey(extender.getWorld())))
			screens.put(getWorldKey(extender.getWorld()), new ArrayList<Screen>());
		if (!unusedPanels.containsKey(getWorldKey(extender.getWorld())))
			unusedPanels.put(getWorldKey(extender.getWorld()), new ArrayList<InfoPanelBlockEntity>());

		List<InfoPanelBlockEntity> rebuildPanels = new ArrayList<InfoPanelBlockEntity>();
		List<Screen> screensToDestroy = new ArrayList<Screen>();

		for (Screen screen : screens.get(getWorldKey(extender.getWorld()))) {
			InfoPanelBlockEntity core = screen.getCore(extender.getWorld());
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
			destroyScreen(screen, extender.getWorld());

		BlockPos pos = extender.getPos();
		for (InfoPanelBlockEntity panel : unusedPanels.get(getWorldKey(extender.getWorld()))) {
			BlockPos posPanel = panel.getPos();
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
		for (InfoPanelBlockEntity panel : rebuildPanels) {
			Screen screen = buildFromPanel(panel);
			screens.get(getWorldKey(extender.getWorld())).add(screen);
			if (unusedPanels.get(getWorldKey(extender.getWorld())).contains(panel))
				unusedPanels.get(getWorldKey(extender.getWorld())).remove(panel);
		}
	}

	public void unregisterScreenPart(BlockEntity part) {
		if (part.getWorld().isClient)
			return;
		if (!screens.containsKey(getWorldKey(part.getWorld())))
			return;
		if (!unusedPanels.containsKey(getWorldKey(part.getWorld())))
			return;
		if (!(part instanceof IScreenPart))
			return;
		IScreenPart screenPart = (IScreenPart) part;
		Screen screen = screenPart.getScreen();
		if (screen == null) {
			if (part instanceof InfoPanelBlockEntity && unusedPanels.get(getWorldKey(part.getWorld())).contains(part))
				unusedPanels.get(getWorldKey(part.getWorld())).remove(part);
			return;
		}
		InfoPanelBlockEntity core = screen.getCore(part.getWorld());
		destroyScreen(screen, part.getWorld());
		boolean isCoreDestroyed = part instanceof InfoPanelBlockEntity;
		if (!isCoreDestroyed && core != null) {
			Screen newScreen = buildFromPanel(core);
			screens.get(getWorldKey(core.getWorld())).add(newScreen);
		}
	}
}
