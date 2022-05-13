package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HoloPanelExtender extends HoloPanel {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityHoloPanelExtender();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) { // 1.7.10
		icons[0] = registerIcon(ir,"holo_panel/extender_back_hor");
		icons[1] = registerIcon(ir,"holo_panel/extender_back_vert");
		icons[2] = registerIcon(ir,"holo_panel/extender_top_hor");
		icons[3] = registerIcon(ir,"holo_panel/extender_top_vert");
		icons[4] = registerIcon(ir,"holo_panel/extender_top_on_hor");
		icons[5] = registerIcon(ir,"holo_panel/extender_top_on_vert");
		icons[6] = registerIcon(ir,"holo_panel/extender_side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem()))
			return true;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityHoloPanelExtender))
			return true;
		TileEntityInfoPanel panel = ((TileEntityHoloPanelExtender) te).getCore();
		if (panel != null)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_HOLO_PANEL, world, panel.xCoord, panel.yCoord, panel.zCoord);
		return true;
	}
}
