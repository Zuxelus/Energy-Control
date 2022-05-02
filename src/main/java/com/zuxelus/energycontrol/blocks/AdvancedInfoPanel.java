package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class AdvancedInfoPanel extends InfoPanel {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityAdvancedInfoPanel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { // 1.7.10
		int id = mapping[meta % 6][side];
		if (id > 1)
			return icons[2];
		return icons[id];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"info_panel/panel_advanced_back");
		icons[1] = registerIcon(ir,"info_panel/panel_advanced_face");
		icons[2] = registerIcon(ir,"info_panel/panel_advanced_side");
	}

	@Override
	public float[] getBlockBounds(TileEntityFacing tile) {
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return super.getBlockBounds(tile);

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return super.getBlockBounds(tile);

		switch (te.getFacingForge()) {
		case EAST:
			return new float[] { 0.0F, 0.0F, 0.0F, 0.0625F * te.thickness, 1.0F, 1.0F };
		case WEST:
			return new float[] { 1.0F - 0.0625F * te.thickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
		case SOUTH:
			return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F * te.thickness };
		case NORTH:
			return new float[] { 0.0F, 0.0F, 1.0F - 0.0625F * te.thickness, 1.0F, 1.0F, 1.0F };
		case UP:
			return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 0.0625F * te.thickness, 1.0F };
		case DOWN:
			return new float[] { 0.0F, 1.0F - 0.0625F * te.thickness, 0.0F, 1.0F, 1.0F, 1.0F };
		default:
			return super.getBlockBounds(tile);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem()))
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityInfoPanel))
			return true;
		if (!world.isRemote && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == facing)
			if (((TileEntityInfoPanel) te).runTouchAction(player.getHeldItem(), x, y, z, hitX, hitY, hitZ))
				return true;
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_ADVANCED_PANEL, world, x, y, z);
		return true;
	}
}
