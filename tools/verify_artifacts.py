"""Inspect real deliverables without launching Minecraft. Fails on missing content."""
from pathlib import Path
import hashlib
import io
import json
import zipfile
import xml.etree.ElementTree as ET

root=Path(__file__).resolve().parents[1]
version=next(line.split('=',1)[1] for line in (root/'gradle.properties').read_text().splitlines() if line.startswith('mod_version='))
result={'version':version,'runtime_evidence':'docs/evidence/runtime-v3 (isolated gameplay; see QA-V3.md)','artifacts':[],'tests':[]}
for platform in ('fabric','neoforge'):
    jar=root/platform/'build/libs'/f'energycontrol-{platform}-{version}.jar'
    source=jar.with_name(jar.stem+'-sources.jar')
    with zipfile.ZipFile(jar) as archive:
        names=set(archive.namelist())
        assert archive.testzip() is None
        assert 'LICENSE' in names
        assert not any('/qa/' in n or 'QaNeoForge' in n for n in names), 'QA classes leaked into release'
        assert 'com/zuxelus/energycontrol/port/block/PanelBlockEntity.class' in names
        assert 'com/zuxelus/energycontrol/port/client/PanelRenderer.class' in names
        assert 'com/zuxelus/energycontrol/port/network/PanelEditPayload.class' in names
        assert 'com/zuxelus/energycontrol/port/network/PortableDataPayload.class' in names
        assert 'com/zuxelus/energycontrol/port/fluid/FluidProbe.class' in names
        assert 'com/zuxelus/energycontrol/port/menu/PortableMenu.class' in names
        assert not any(n.startswith(('net/minecraft/','dev/architectury/')) for n in names), 'Do not redistribute Minecraft or shade Architectury'
        for name in names:
            if name.endswith('.class') and name.startswith('com/zuxelus/energycontrol/port/'):
                assert int.from_bytes(archive.read(name)[6:8],'big')==65, f'Not Java 21: {name}'
            if name.endswith('.json'):
                data=json.loads(archive.read(name))
                if name.startswith('assets/energycontrol/models/'):
                    parent=data.get('parent','')
                    if parent.startswith('energycontrol:'):
                        assert 'assets/energycontrol/models/'+parent.split(':')[1]+'.json' in names, (name,parent)
                    for texture in data.get('textures',{}).values():
                        if texture.startswith('energycontrol:'):
                            assert 'assets/energycontrol/textures/'+texture.split(':')[1]+'.png' in names,(name,texture)
        recipes=[n for n in names if n.startswith('data/energycontrol/recipe/') and n.endswith('.json')]
        assert len(recipes)==17
        for name in recipes:
            assert json.loads(archive.read(name))['result']['id'].startswith('energycontrol:')
        if platform=='fabric':
            metadata=json.loads(archive.read('fabric.mod.json'))
            assert metadata['version']==version
            assert 'QaFabric' not in str(metadata)
            assert metadata['depends']['minecraft']=='1.21.1'
            for classes in metadata['entrypoints'].values():
                for entry in classes: assert entry.replace('.','/')+'.class' in names
            included=[n for n in names if n.startswith('META-INF/jars/') and n.endswith('.jar')]
            assert len(included)==1
            with zipfile.ZipFile(io.BytesIO(archive.read(included[0]))) as energy:
                embedded=json.loads(energy.read('fabric.mod.json'))
                assert embedded['id']=='team_reborn_energy'
                assert embedded['version']=='4.1.0'
                assert any('license' in n.lower() for n in energy.namelist()), 'Nested library license missing'
        else:
            metadata=archive.read('META-INF/neoforge.mods.toml').decode()
            assert version in metadata and '${version}' not in metadata
            assert 'energycontrolqa' not in metadata
            assert 'com/zuxelus/energycontrol/port/neoforge/EnergyControlNeoForge.class' in names
    with zipfile.ZipFile(source) as archive:
        assert archive.testzip() is None
        names=set(archive.namelist())
        assert not any('/qa/' in n for n in names), 'QA sources leaked into release source jar'
        assert 'com/zuxelus/energycontrol/port/block/PanelBlockEntity.java' in names
        assert any(n.endswith('.java') and f'/port/{platform}/' in n for n in names)
    for file in (jar,source):
        result['artifacts'].append({'path':file.relative_to(root).as_posix(),'bytes':file.stat().st_size,'sha256':hashlib.sha256(file.read_bytes()).hexdigest()})
for file in sorted((root/'common/build/test-results/test').glob('TEST-*.xml')):
    suite=ET.parse(file).getroot()
    assert int(suite.attrib['failures'])==0 and int(suite.attrib['errors'])==0
    result['tests'].append({key:suite.attrib[key] for key in ('name','tests','failures','errors')})
assert sum(int(s['tests']) for s in result['tests'])==31
output=root/'docs/evidence/artifact-verification.json'
output.write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
print(json.dumps(result,indent=2))
