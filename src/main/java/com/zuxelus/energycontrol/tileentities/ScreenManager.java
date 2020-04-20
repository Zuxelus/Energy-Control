package com.zuxelus.energycontrol.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.blocks.InfoPanelExtender;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
		return world.provider.getDimension();
	}

	private boolean isValidExtender(World world, BlockPos pos, EnumFacing facing, boolean advanced) {
		if (!(world.getBlockState(pos).getBlock() instanceof InfoPanelExtender))
			return false;
		TileEntity tileEntity = world.getTileEntity(pos);
		if (!(tileEntity instanceof TileEntityInfoPanelExtender))
			return false;
		//if (advanced ^ (tileEntity instanceof TileEntityAdvancedInfoPanelExtender)) //TODO
			//return false;
		if (((TileEntityInfoPanelExtender) tileEntity).facing != facing)
			return false;
		if (((IScreenPart) tileEntity).getScreen() != null)
			return false;
		return true;
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
						allOk = core != null && isValidExtender(world, new BlockPos(x + dir * interX, y + dir * interY, z + dir * interZ), core.facing, advanced);
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

	public Screen loadScreen(TileEntityInfoPanel panel) {
		if (panel.screenData == null)
			return null;
		
		NBTTagCompound tag = panel.screenData;
		Screen screen = new Screen();

		screen.minX = tag.getInteger("minX");
		screen.minY = tag.getInteger("minY");
		screen.minZ = tag.getInteger("minZ");

		screen.maxX = tag.getInteger("maxX");
		screen.maxY = tag.getInteger("maxY");
		screen.maxZ = tag.getInteger("maxZ");

		screen.setCore(panel);

		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			Integer key = getWorldKey(panel.getWorld());
			if (!screens.containsKey(key))
				screens.put(key, new ArrayList<Screen>());
			if (!unusedPanels.containsKey(key))
				unusedPanels.put(key, new ArrayList<TileEntityInfoPanel>());
			if (!screens.get(key).contains(screen))
				screens.get(key).add(screen);
		}
		return screen;
	}

	public Screen loadScreen(World world, BlockPos pos) {
		TileEntity core = world.getTileEntity(pos);
		if (core != null && core instanceof TileEntityInfoPanel)
			return loadScreen((TileEntityInfoPanel) core);
		return null;
	}

	private Screen tryBuildFromPanel(TileEntityInfoPanel panel) {
		boolean advanced = false; //panel instanceof TileEntityAdvancedInfoPanel; //TODO
		Screen screen = new Screen();
		BlockPos pos = panel.getPos();
		screen.maxX = screen.minX = pos.getX();
		screen.maxY = screen.minY = pos.getY();
		screen.maxZ = screen.minZ = pos.getZ();
		screen.setCore(panel);
		EnumFacing facing = panel.getFacing();
		int dx = (facing == EnumFacing.WEST) || (facing == EnumFacing.EAST) ? 0 : -1;
		int dy = (facing == EnumFacing.DOWN) || (facing == EnumFacing.UP) ? 0 : -1;
		int dz = (facing == EnumFacing.NORTH) || (facing == EnumFacing.SOUTH) ? 0 : -1;
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

	private void destroyScreen(Screen screen, World world) {
		screens.get(getWorldKey(world)).remove(screen);
		screen.destroy(false, world);
	}

	public void unregisterScreenPart(TileEntity part) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
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
			if (part instanceof TileEntityInfoPanel && unusedPanels.get(getWorldKey(part.getWorld())).contains(part))
				unusedPanels.get(getWorldKey(part.getWorld())).remove(part);
			return;
		}
		TileEntityInfoPanel core = screen.getCore(part.getWorld());
		destroyScreen(screen, part.getWorld());
		boolean isCoreDestroyed = part instanceof TileEntityInfoPanel;
		if (!isCoreDestroyed && core != null) {
			Screen newScreen = tryBuildFromPanel(core);
			if (newScreen == null)
				unusedPanels.get(getWorldKey(core.getWorld())).add(core);
			else
				screens.get(getWorldKey(core.getWorld())).add(newScreen);
		}
	}

	public void registerInfoPanel(TileEntityInfoPanel panel) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (!screens.containsKey(getWorldKey(panel.getWorld())))
			screens.put(getWorldKey(panel.getWorld()), new ArrayList<Screen>());
		if (!unusedPanels.containsKey(getWorldKey(panel.getWorld())))
			unusedPanels.put(getWorldKey(panel.getWorld()), new ArrayList<TileEntityInfoPanel>());
		for (Screen screen : screens.get(getWorldKey(panel.getWorld()))) {
			if (screen.isBlockPartOf(panel)) {
				// occurs on chunk unloading/loading
				destroyScreen(screen, panel.getWorld());
				break;
			}
		}
		Screen screen = tryBuildFromPanel(panel);
		if (screen != null)
			screens.get(getWorldKey(panel.getWorld())).add(screen);
		else
			unusedPanels.get(getWorldKey(panel.getWorld())).add(panel);
	}

	public void registerInfoPanelExtender(TileEntityInfoPanelExtender extender) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (!screens.containsKey(getWorldKey(extender.getWorld())))
			screens.put(getWorldKey(extender.getWorld()), new ArrayList<Screen>());
		if (!unusedPanels.containsKey(getWorldKey(extender.getWorld())))
			unusedPanels.put(getWorldKey(extender.getWorld()), new ArrayList<TileEntityInfoPanel>());

		List<TileEntityInfoPanel> rebuildPanels = new ArrayList<TileEntityInfoPanel>();
		List<Screen> screensToDestroy = new ArrayList<Screen>();

		for (Screen screen : screens.get(getWorldKey(extender.getWorld()))) {
			TileEntityInfoPanel core = screen.getCore(extender.getWorld());
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
		for (TileEntityInfoPanel panel : unusedPanels.get(getWorldKey(extender.getWorld()))) {
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
		for (TileEntityInfoPanel panel : rebuildPanels) {
			Screen screen = tryBuildFromPanel(panel);
			if (screen != null) {
				screens.get(getWorldKey(extender.getWorld())).add(screen);
				if (unusedPanels.get(getWorldKey(extender.getWorld())).contains(panel))
					unusedPanels.get(getWorldKey(extender.getWorld())).remove(panel);
			} else {
				if (!unusedPanels.get(getWorldKey(extender.getWorld())).contains(panel))
					unusedPanels.get(getWorldKey(extender.getWorld())).add(panel);
			}
		}
	}

	public void clearWorld(World world) {
		if (screens.containsKey(getWorldKey(world)))
			screens.get(getWorldKey(world)).clear();
	}
}
