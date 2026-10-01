"""Validate accepted v6 evidence separately from retained failures."""
from pathlib import Path
import json,hashlib,struct
root=Path(__file__).resolve().parents[1];base=root/'docs/evidence/runtime-v6'
expected={
 'fabric-inventory':{'server':11,'editor':18,'observer':4},
 'fabric-inventory-reload':{'server':10,'client':3},
 'fabric-inventory-offhand-reload':{'server':10,'editor':9},
 'neoforge-inventory':{'server':11,'editor':18,'observer':4},
 'neoforge-inventory-offhand-reload':{'server':10,'editor':9},
 'fabric-slopes':{'server':14,'editor':12,'observer':6},
 'neoforge-slopes':{'server':14,'editor':12,'observer':6},
 'fabric-slopes-normal-reload':{'editor':4},
 'neoforge-slopes-normal-reload':{'editor':4},
 'neoforge-ie-kits':{'server':8,'client':4},
}
result={'accepted_runs':[],'excluded_attempts':[],'assertion_executions':0,'original_pngs':0}
for name,roles in expected.items():
 d=base/name;row={'name':name,'assertions':{},'screenshots':[]}
 for role,n in roles.items():
  t=(d/(role+'.txt')).read_text();assert 'FAIL ' not in t and '_DONE' in t,(name,role)
  count=sum(l.startswith('PASS ') for l in t.splitlines());assert count==n,(name,role,count,n);row['assertions'][role]=count;result['assertion_executions']+=count
 server=(d/'server.log').read_text(errors='replace');assert 'All dimensions are saved' in server,name
 if name.endswith('normal-reload'):assert '[EC-QA]' not in server,name
 for p in sorted(d.glob('*.png')):
  b=p.read_bytes();assert b[:8]==b'\x89PNG\r\n\x1a\n';width,height=struct.unpack('>II',b[16:24]);assert(width,height)==(1280,720)
  row['screenshots'].append({'name':p.name,'bytes':len(b),'sha256':hashlib.sha256(b).hexdigest(),'width':width,'height':height});result['original_pngs']+=1
 result['accepted_runs'].append(row)
for p in sorted(base.iterdir()):
 if p.is_dir() and p.name not in expected:result['excluded_attempts'].append(p.name)
assert result['assertion_executions']==201,result['assertion_executions']
assert result['original_pngs']==58,result['original_pngs']
(base/'summary.json').write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
print(json.dumps({k:v for k,v in result.items() if k!='accepted_runs'},indent=2))
