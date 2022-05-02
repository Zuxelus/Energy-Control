package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class InfoPanelExtender extends FacingBlock {

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) > 5 ? 10 : 0;
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityInfoPanelExtender();
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
		icons[0] = registerIcon(ir,"info_panel/extender_back");
		icons[1] = registerIcon(ir,"info_panel/extender_face");
		icons[2] = registerIcon(ir,"info_panel/extender_side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem()))
			return true;
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return true;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (EnergyControl.altPressed.get(player) && panel.getFacing() == facing)
			if (panel.runTouchAction(player.getHeldItem(), x, y, z, hitX, hitY, hitZ))
				return true;
		if (panel != null)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_INFO_PANEL, world, panel.xCoord, panel.yCoord, panel.zCoord);
		return true;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_INFO_PANEL;
	}
}
