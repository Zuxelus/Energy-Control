package com.zuxelus.energycontrol.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.blocks.BlockMain;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ScreenManager {

	private final Map<Integer, List<Screen>> screens;
	private final Map<Integer, List<TileEntityInfoPanel>> unusedPanels;

	public ScreenManager() {
		screens = new HashMap<Integer, List<Screen>>();
		unusedPanels = new HashMap<Integer, List<TileEntityInfoPanel>>();
	}

	private int getWorldKey(World world) {
		if (world == null)
			return -10;
		if (world.provider == null)
			return -10;
		return world.provider.dimensionId;
	}

	private void checkWorldLists(Integer key) {
		if (!screens.containsKey(key))
			screens.put(key, new ArrayList<Screen>());
		if (!unusedPanels.containsKey(key))
			unusedPanels.put(key, new ArrayList<TileEntityInfoPanel>());
	}

	public void clearWorld(World world) {
		Integer key = getWorldKey(world);
		if (screens.containsKey(key))
			screens.get(key).clear();
		if (unusedPanels.containsKey(key))
			unusedPanels.get(key).clear();
	}

	public void registerInfoPanel(TileEntityInfoPanel panel) {
		if (panel.getWorldObj().isRemote)
			return;
		checkWorldLists(getWorldKey(panel.getWorldObj()));

		for (Screen screen : screens.get(getWorldKey(panel.getWorldObj())))
			if (screen.isBlockPartOf(panel)) {
				destroyScreen(screen, panel.getWorldObj()); // occurs on chunk unloading/loading
				break;
			}

		Screen screen = buildFromPanel(panel);
		screens.get(getWorldKey(panel.getWorldObj())).add(screen);
	}

	private void destroyScreen(Screen screen, World world) {
		screens.get(getWorldKey(world)).remove(screen);
		screen.destroy(true, world);
	}

	private Screen buildFromPanel(TileEntityInfoPanel panel) {
		Screen screen = new Screen(panel);
		ForgeDirection facing = panel.getFacingForge();
		int dx = (facing == ForgeDirection.WEST) || (facing == ForgeDirection.EAST) ? 0 : -1;
		int dy = (facing == ForgeDirection.DOWN) || (facing == ForgeDirection.UP) ? 0 : -1;
		int dz = (facing == ForgeDirection.NORTH) || (facing == ForgeDirection.SOUTH) ? 0 : -1;
		boolean advanced = panel instanceof TileEntityAdvancedInfoPanel;
		updateScreenBound(screen, dx, 0, 0, panel.getWorldObj(), advanced);
		updateScreenBound(screen, -dx, 0, 0, panel.getWorldObj(), advanced);
		updateScreenBound(screen, 0, dy, 0, panel.getWorldObj(), advanced);
		updateScreenBound(screen, 0, -dy, 0, panel.getWorldObj(), advanced);
		updateScreenBound(screen, 0, 0, dz, panel.getWorldObj(), advanced);
		updateScreenBound(screen, 0, 0, -dz, panel.getWorldObj(), advanced);
		screen.init(false, panel.getWorldObj());
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
						TileEntityInfoPanel core = screen.getCore(world);
						allOk = core != null && isValidExtender(world, x + dir * interX, y + dir * interY, z + dir * interZ, core.getFacingForge(), advanced);
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

	private boolean isValidExtender(World world, int x, int y, int z, ForgeDirection facing, boolean advanced) {
		if (!(world.getBlock(x, y, z) instanceof BlockMain))
			return false;
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityInfoPanelExtender))
			return false;
		if (advanced ^ (tileEntity instanceof TileEntityAdvancedInfoPanelExtender))
			return false;
		if (((TileEntityInfoPanelExtender) tileEntity).getFacingForge() != facing)
			return false;
		if (((IScreenPart) tileEntity).getScreen() != null)
			return false;
		return true;
	}

	public Screen loadScreen(TileEntityInfoPanel panel) {
		if (panel.screenData == null)
			return null;
		
		Screen screen = new Screen(panel, panel.screenData);
		if (!panel.getWorldObj().isRemote) {
			Integer key = getWorldKey(panel.getWorldObj());
			checkWorldLists(key);
			if (!screens.get(key).contains(screen))
				screens.get(key).add(screen);
		}
		return screen;
	}

	public void registerInfoPanelExtender(TileEntityInfoPanelExtender extender) {
		if (extender.getWorldObj().isRemote)
			return;
		if (!screens.containsKey(getWorldKey(extender.getWorldObj())))
			screens.put(getWorldKey(extender.getWorldObj()), new ArrayList<Screen>());
		if (!unusedPanels.containsKey(getWorldKey(extender.getWorldObj())))
			unusedPanels.put(getWorldKey(extender.getWorldObj()), new ArrayList<TileEntityInfoPanel>());

		List<TileEntityInfoPanel> rebuildPanels = new ArrayList<TileEntityInfoPanel>();
		List<Screen> screensToDestroy = new ArrayList<Screen>();

		for (Screen screen : screens.get(getWorldKey(extender.getWorldObj()))) {
			TileEntityInfoPanel core = screen.getCore(extender.getWorldObj());
			if (screen.isBlockNearby(extender) && core != null && extender.getFacingForge() == core.getFacingForge()) {
				rebuildPanels.add(core);
				screensToDestroy.add(screen);
			} else if (screen.isBlockPartOf(extender)) {
				// block is already part of the screen
				// shouldn't be registered twice
				return;
			}
		}
		for (Screen screen : screensToDestroy)
			destroyScreen(screen, extender.getWorldObj());

		for (TileEntityInfoPanel panel : unusedPanels.get(getWorldKey(extender.getWorldObj()))) {
			if (((panel.xCoord == extender.xCoord && panel.yCoord == extender.yCoord
					&& (panel.zCoord == extender.zCoord + 1 || panel.zCoord == extender.zCoord - 1))
					|| (panel.xCoord == extender.xCoord
							&& (panel.yCoord == extender.yCoord + 1 || panel.yCoord == extender.yCoord - 1)
							&& panel.zCoord == extender.zCoord)
					|| ((panel.xCoord == extender.xCoord + 1 || panel.xCoord == extender.xCoord - 1)
							&& panel.yCoord == extender.yCoord && panel.zCoord == extender.zCoord))
					&& extender.getFacingForge() == panel.getFacingForge()) {
				rebuildPanels.add(panel);
			}
		}
		for (TileEntityInfoPanel panel : rebuildPanels) {
			Screen screen = buildFromPanel(panel);
			screens.get(getWorldKey(extender.getWorldObj())).add(screen);
			if (unusedPanels.get(getWorldKey(extender.getWorldObj())).contains(panel))
				unusedPanels.get(getWorldKey(extender.getWorldObj())).remove(panel);
		}
	}

	public void unregisterScreenPart(TileEntity part) {
		if (part.getWorldObj().isRemote)
			return;
		if (!screens.containsKey(getWorldKey(part.getWorldObj())))
			return;
		if (!unusedPanels.containsKey(getWorldKey(part.getWorldObj())))
			return;
		if (!(part instanceof IScreenPart))
			return;
		IScreenPart screenPart = (IScreenPart) part;
		Screen screen = screenPart.getScreen();
		if (screen == null) {
			if (part instanceof TileEntityInfoPanel && unusedPanels.get(getWorldKey(part.getWorldObj())).contains(part))
				unusedPanels.get(getWorldKey(part.getWorldObj())).remove(part);
			return;
		}
		TileEntityInfoPanel core = screen.getCore(part.getWorldObj());
		destroyScreen(screen, part.getWorldObj());
		boolean isCoreDestroyed = part instanceof TileEntityInfoPanel;
		if (!isCoreDestroyed && core != null) {
			Screen newScreen = buildFromPanel(core);
			screens.get(getWorldKey(core.getWorldObj())).add(newScreen);
		}
	}
}
