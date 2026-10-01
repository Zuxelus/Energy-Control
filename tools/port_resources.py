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

# Additional implemented storage, upgrade, portable and holographic resources.
for name,original in {'card_liquid':'card_liquid','card_energy_array':'card_energy_array','card_liquid_array':'card_liquid_array','upgrade_range':'upgrade_range','upgrade_capacity':'upgrade_color','upgrade_precision':'upgrade_touch','portable_panel':'portable_panel'}.items():
    copy(Path(f'textures/item/{original}.png'))
    write(assets/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'energycontrol:item/{original}'}})
for file in (source/'textures/block/holo_panel').glob('*.png'):copy(file.relative_to(source))
copy(Path('models/block/plate_box.json'))
for block in ('holo_panel','holo_panel_extender'):
    for mode in ('off','on'):copy(Path(f'models/block/{block}_{mode}.json'))
    copy(Path(f'models/item/{block}.json'))
    write(assets/f'blockstates/{block}.json',{'variants':{f'facing={face}':{'model':f'energycontrol:block/{block}_on',**rot} for face,rot in rotations.items()}})
    write(target/f'data/energycontrol/loot_table/blocks/{block}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':f'energycontrol:{block}'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
    blocks.append(block)
extra_recipes={
 'card_liquid':['minecraft:paper','minecraft:glass_bottle'],
 'card_energy_array':['energycontrol:card_energy','minecraft:comparator'],
 'card_liquid_array':['energycontrol:card_liquid','minecraft:comparator'],
 'upgrade_range':['minecraft:ender_pearl','minecraft:copper_ingot'],
 'upgrade_capacity':['minecraft:chest','minecraft:gold_ingot'],
 'upgrade_precision':['minecraft:quartz','minecraft:gold_ingot'],
 'portable_panel':['energycontrol:info_panel','minecraft:leather'],
 'holo_panel':['energycontrol:info_panel_advanced','minecraft:amethyst_shard'],
 'holo_panel_extender':['energycontrol:info_panel_advanced_extender','minecraft:amethyst_shard']}
for name,ingredients in extra_recipes.items():write(target/f'data/energycontrol/recipe/{name}.json',{'type':'minecraft:crafting_shapeless','ingredients':[{'item':i} for i in ingredients],'result':{'id':f'energycontrol:{name}','count':1}})
write(target/'data/minecraft/tags/block/mineable/pickaxe.json',{'replace':False,'values':[f'energycontrol:{b}' for b in blocks]})
for key,english,chinese in [
 ('item.energycontrol.card_liquid','Fluid Sensor Card','流体传感卡'),('item.energycontrol.card_energy_array','Energy Array Card','能源阵列卡'),('item.energycontrol.card_liquid_array','Fluid Array Card','流体阵列卡'),
 ('item.energycontrol.upgrade_range','Range Upgrade','距离升级'),('item.energycontrol.upgrade_capacity','Array Capacity Upgrade','阵列容量升级'),('item.energycontrol.upgrade_precision','Precision Upgrade','精度升级'),('item.energycontrol.portable_panel','Portable Information Panel','便携信息面板'),('block.energycontrol.holo_panel','Holographic Panel','全息信息面板'),('block.energycontrol.holo_panel_extender','Holographic Extender','全息扩展块'),
 ('tooltip.energycontrol.card.fluid','Sneak-use a tank face to bind. Read-only.','潜行右键储罐表面绑定，只读。'),('tooltip.energycontrol.card.energy_array','Bind up to 16 targets. Repeat a face to remove.','最多绑定16个目标，再次点击同一面移除。'),('tooltip.energycontrol.card.fluid_array','Bind up to 16 tanks. Fluids stay separate.','最多绑定16个储罐，不同流体分别汇总。'),
 ('tooltip.energycontrol.upgrade.range','0/1/2/3 upgrades: range 64/128/256/512 blocks.','0/1/2/3个升级：距离64/128/256/512格。'),('tooltip.energycontrol.upgrade.capacity','0/1/2/3 upgrades: 4/8/12/16 active array targets.','0/1/2/3个升级：4/8/12/16个生效阵列目标。'),('tooltip.energycontrol.upgrade.precision','0/1/2/3 upgrades: 0/1/2/3 decimal places.','0/1/2/3个升级：显示0/1/2/3位小数。')]:en[key]=english;zh[key]=chinese
write(assets/'lang/en_us.json',en);write(assets/'lang/zh_cn.json',zh)
print('Storage/portable/holographic resources: 6 blocks, 11 items, 17 recipes.')

# 0.5: state-backed case thickness. Original GPL texture coordinates are retained.
for block in ('info_panel_advanced','info_panel_advanced_extender'):
    original=json.loads((assets/f'models/block/{block}.json').read_text(encoding='utf-8'))
    full=json.loads((assets/'models/block/full_box.json').read_text(encoding='utf-8'))
    for thickness in range(1,17):
        model=json.loads(json.dumps(full))
        model['textures']=original['textures']
        model['elements'][0]['from'][2]=16-thickness
        write(assets/f'models/block/{block}_thickness_{thickness}.json',model)
    write(assets/f'blockstates/{block}.json',{'variants':{
        f'facing={face},thickness={thickness}':{'model':f'energycontrol:block/{block}_thickness_{thickness}',**rot}
        for face,rot in rotations.items() for thickness in range(1,17)}})
write(assets/'models/item/card_machine.json',{'parent':'minecraft:item/generated','textures':{'layer0':'energycontrol:item/card_energy'}})
write(target/'data/energycontrol/recipe/card_machine.json',{'type':'minecraft:crafting_shapeless','ingredients':[{'item':'energycontrol:card_energy'},{'item':'minecraft:quartz'}],'result':{'id':'energycontrol:card_machine','count':1}})
for lang,name,tooltip in [('en_us','Machine Data Card','Sneak-use on a supported machine; specialized read-only data.'),('zh_cn','机器数据卡','潜行右键绑定受支持机器；读取专用数据。')]:
    path=assets/f'lang/{lang}.json';data=json.loads(path.read_text(encoding='utf-8'))
    data['item.energycontrol.card_machine']=name;data['tooltip.energycontrol.card.machine']=tooltip;write(path,data)

print('0.5 resources: 18 recipes, 32 case models and 192 thickness/facing variants; machine card registered on both loaders.')

# Inventory/holder/generic kits: all assets are original upstream GPL resources.
for name,original,en_name,zh_name,ingredients in [
 ('card_inventory','card_inventory','Inventory Sensor Card','物品库存卡',['minecraft:paper','minecraft:chest']),
 ('card_holder','card_holder','Card Holder','卡片收纳夹',['minecraft:leather','minecraft:paper']),
 ('kit_inventory','kit_inventory','Inventory Sensor Kit','物品监测套件',['minecraft:iron_ingot','minecraft:chest']),
 ('kit_energy','kit_energy','Energy Sensor Kit','能源监测套件',['minecraft:iron_ingot','minecraft:redstone']),
 ('kit_liquid','kit_liquid','Fluid Sensor Kit','流体监测套件',['minecraft:iron_ingot','minecraft:glass_bottle']),
 ('kit_redstone','kit_redstone','Redstone Sensor Kit','红石监测套件',['minecraft:iron_ingot','minecraft:redstone_torch']),
 ('kit_machine','kit_energy','Machine Data Kit','机器数据套件',['minecraft:iron_ingot','minecraft:quartz'])]:
    copy(Path(f'textures/item/{original}.png'))
    write(assets/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'energycontrol:item/{original}'}})
    write(target/f'data/energycontrol/recipe/{name}.json',{'type':'minecraft:crafting_shapeless','ingredients':[{'item':i} for i in ingredients],'result':{'id':f'energycontrol:{name}','count':1}})
    for lang,label in [('en_us',en_name),('zh_cn',zh_name)]:
        path=assets/f'lang/{lang}.json';data=json.loads(path.read_text(encoding='utf-8'));data['item.energycontrol.'+name]=label;write(path,data)
for lang,tooltip in [('en_us','Sneak-use an inventory face to bind; Fields selects displayed information.'),('zh_cn','潜行右键库存表面绑定；在字段设置中选择显示信息。')]:
    path=assets/f'lang/{lang}.json';data=json.loads(path.read_text(encoding='utf-8'));data['tooltip.energycontrol.card.inventory']=tooltip;write(path,data)
print('Inventory/holder and five kits:25 recipes total.')
