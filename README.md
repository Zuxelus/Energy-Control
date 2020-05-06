# Energy Control

Energy Control is a remastered version of original Nuclear Control.

Created by Zuxelus.

Mod page https://www.curseforge.com/minecraft/mc-mods/energy-control

Group in Discord https://discord.gg/Y5zRsHV

## How-to create addon for version 1.12.2

* Download and install Eclipse
* Download forge-1.12.2-14.23.5.2847-mdk.zip
* Unzip in NewFolder, run "Standalone source installation" steps from README.txt
* Download example mod from folder addon and put src in src, replace build.gradle with file from addon folder
* In your NewFolder create folder "libs" and copy EnergyControl-1.12.2-X.X.X-api.jar there (or replace path in build.gradle)
* Open eclipse, select Workspace folder as NewFolder\eclipse
* Add api jar file to project (Select Project->Properties->Java Build Path->Libraries->Add External Jar)
* Delete KitFurnace.java and CardFurnace.java or rename them to your new items, update all resources
* Ask me for an item id range for your items (should be more than 50, in example there is one id = 50)
* Create your mod, build (use gradlew build) and use (mod file will be in NewFolder\build\libs)
