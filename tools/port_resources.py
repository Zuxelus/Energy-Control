"""Reuse upstream GPL assets and emit only the implemented preview's resources."""
from pathlib import Path
import json
import shutil

root=Path(__file__).resolve().parents[1]
source=root/'src/main/resources/assets/energycontrol'
target=root/'common/src/main/resources'
assets=target/'assets/energycontrol'
blocks=['info_panel','info_panel_advanced','info_panel_extender','info_panel_advanced_extender']
cards=['card_text','card_energy','card_time','card_redstone']
def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
def copy(relative):
    dest=assets/relative
    dest.parent.mkdir(parents=True,exist_ok=True)
    shutil.copy2(source/relative,dest)
for file in (source/'textures/block/info_panel').glob('*.png'):
    copy(file.relative_to(source))
copy(Path('models/block/full_box.json'))
rotations={'north':{},'south':{'y':180},'east':{'y':90},'west':{'y':270},'up':{'x':270},'down':{'x':90}}
for block in blocks:
    copy(Path(f'models/block/{block}.json'))
    copy(Path(f'models/item/{block}.json'))
    write(assets/f'blockstates/{block}.json',{'variants':{f'facing={face}':{'model':f'energycontrol:block/{block}',**rot} for face,rot in rotations.items()}})
    write(target/f'data/energycontrol/loot_table/blocks/{block}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':f'energycontrol:{block}'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
for card in cards:
    copy(Path(f'models/item/{card}.json'))
    copy(Path(f'textures/item/{card}.png'))
write(target/'pack.mcmeta',{'pack':{'pack_format':34,'description':'Energy Control 1.21.1 port preview resources'}})
write(target/'data/minecraft/tags/block/mineable/pickaxe.json',{'replace':False,'values':[f'energycontrol:{b}' for b in blocks]})
recipes={
    'info_panel':(['III','RGR','III'],{'I':'minecraft:iron_ingot','R':'minecraft:redstone','G':'minecraft:glass_pane'}),
    'info_panel_advanced':(['GQG','QPQ','GQG'],{'G':'minecraft:gold_ingot','Q':'minecraft:quartz','P':'energycontrol:info_panel'}),
    'info_panel_extender':(['III','IGI','III'],{'I':'minecraft:iron_ingot','G':'minecraft:glass_pane'}),
    'info_panel_advanced_extender':(['GQG','QPQ','GQG'],{'G':'minecraft:gold_ingot','Q':'minecraft:quartz','P':'energycontrol:info_panel_extender'}),
}
for name,(pattern,keys) in recipes.items():
    write(target/f'data/energycontrol/recipe/{name}.json',{'type':'minecraft:crafting_shaped','pattern':pattern,'key':{k:{'item':v} for k,v in keys.items()},'result':{'id':f'energycontrol:{name}','count':1}})
for card,ingredient in zip(cards,['minecraft:ink_sac','minecraft:redstone','minecraft:clock','minecraft:redstone_torch']):
    write(target/f'data/energycontrol/recipe/{card}.json',{'type':'minecraft:crafting_shapeless','ingredients':[{'item':'minecraft:paper'},{'item':ingredient}],'result':{'id':f'energycontrol:{card}','count':1}})
en={
    'block.energycontrol.info_panel':'Information Panel','block.energycontrol.info_panel_advanced':'Advanced Information Panel',
    'block.energycontrol.info_panel_extender':'Panel Extender','block.energycontrol.info_panel_advanced_extender':'Advanced Panel Extender',
    'item.energycontrol.card_text':'Text Card','item.energycontrol.card_energy':'Energy Sensor Card','item.energycontrol.card_time':'Time Card','item.energycontrol.card_redstone':'Redstone Sensor Card',
    'tooltip.energycontrol.card.text':'Insert in a panel; edit using its menu.',
    'tooltip.energycontrol.card.energy':'Sneak-use on energy storage to bind; range: 64 blocks.',
    'tooltip.energycontrol.card.redstone':'Sneak-use on a block to bind; range: 64 blocks.',
    'tooltip.energycontrol.card.time':'Shows the current world time.',
    'message.energycontrol.bound':'Card bound to %s',
    'gui.energycontrol.text':'Card text','gui.energycontrol.save':'Save','gui.energycontrol.slot':'Slot','gui.energycontrol.color':'Color','gui.energycontrol.power':'Power'
}
zh={
    'block.energycontrol.info_panel':'信息面板','block.energycontrol.info_panel_advanced':'高级信息面板',
    'block.energycontrol.info_panel_extender':'面板扩展器','block.energycontrol.info_panel_advanced_extender':'高级面板扩展器',
    'item.energycontrol.card_text':'文字卡','item.energycontrol.card_energy':'能源传感卡','item.energycontrol.card_time':'时间卡','item.energycontrol.card_redstone':'红石传感卡',
    'tooltip.energycontrol.card.text':'插入面板，在面板界面中编辑。','tooltip.energycontrol.card.energy':'潜行右键储能方块以绑定；范围 64 格。',
    'tooltip.energycontrol.card.redstone':'潜行右键目标方块以绑定；范围 64 格。','tooltip.energycontrol.card.time':'显示当前世界时间。',
    'message.energycontrol.bound':'卡片已绑定到 %s','gui.energycontrol.text':'卡片文字','gui.energycontrol.save':'保存','gui.energycontrol.slot':'卡槽','gui.energycontrol.color':'颜色','gui.energycontrol.power':'开关'
}
write(assets/'lang/en_us.json',en)
write(assets/'lang/zh_cn.json',zh)
print('Ported 4 panel models, 4 card textures, 8 recipes, 4 loot tables, English and Chinese UI.')
