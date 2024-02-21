package com.zuxelus.energycontrol.hooks;

import org.apache.logging.log4j.Level;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.hooklib.asm.At;
import com.zuxelus.hooklib.asm.Hook;
import com.zuxelus.hooklib.asm.InjectionPoint;
import com.zuxelus.hooklib.asm.ReturnCondition;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

public class BaseHooks {

	@Hook(at = @At(point = InjectionPoint.RETURN), returnCondition = ReturnCondition.ALWAYS)
	public static void updateEntities(World world) {
		for (Object obj : world.loadedTileEntityList)
			if (obj instanceof TileEntityInfoPanel) {
				TileEntityInfoPanel te = (TileEntityInfoPanel) obj;
				try {
					((TileEntityInfoPanel) te).updateEntityEnd();
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking block entity");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Block entity being ticked");
					te.func_145828_a(crashreportcategory);
					if (ForgeModContainer.removeErroringTileEntities) {
						FMLLog.getLogger().log(Level.ERROR, crashreport.getCompleteReport());
						te.invalidate();
						world.setBlockToAir(te.xCoord, te.yCoord, te.zCoord);
					} else {
						throw new ReportedException(crashreport);
					}
				}
			}
	}
}
