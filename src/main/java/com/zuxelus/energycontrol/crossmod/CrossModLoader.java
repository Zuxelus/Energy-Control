package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.crossmod.ic2.IC2Cross;

public class CrossModLoader {
	public static IC2Cross crossIc2;
	//public static CrossRF crossRF;
	//public static CrossBuildcraft crossBC;
	public static CrossRailcraft crossRailcraft;
    //public static CrossAppEng crossAppEng;
    //public static CrossBigReactors crossBigReactors;

    public static void preinit() { }

    public static void init() {
    	crossIc2 = IC2Cross.getIC2Cross();
    	//crossRF = new CrossRF();
    	//crossBC = new CrossBuildcraft();
		crossRailcraft = new CrossRailcraft();
		//crossAppEng = new CrossAppEng();
		//crossBigReactors = new CrossBigReactors();
		/*crossOC = new CrossOpenComputers();
		crossMekanism = new CrossMekanism();
		crossTE = new CrossTE();        
		crossGT6 = new CrossGT6();
		
		crossOC.RegisterItems();
		crossBigReactors.RegisterItems();
		crossAppEng.RegisterItems();
		crossMekanism.RegisterItems();
		crossTE.RegisterItems();*/
    	
    	//Registers waila stuff
        //FMLInterModComms.sendMessage("Waila", "register", "shedar.mods.ic2.nuclearcontrol.crossmod.waila.CrossWaila.callbackRegister");
    }

    public static void postinit() { }
}
