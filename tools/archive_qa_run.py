"""Archive one task-owned actual QA run without editing screenshots or game logs."""
from pathlib import Path
import argparse,shutil,re
parser=argparse.ArgumentParser();parser.add_argument('loader');parser.add_argument('name');parser.add_argument('runs',nargs='+');args=parser.parse_args()
root=Path(__file__).resolve().parents[1];out=root/'docs/evidence/runtime-v5'/args.name;out.mkdir(parents=True,exist_ok=True)
for spec in args.runs:
 role,folder=spec.split(':',1)
 assert role in ('server','editor','observer','client') and '/' not in folder and chr(92) not in folder
 source=root/args.loader/'run'/folder
 text=(source/'qa-results.txt').read_text(encoding='utf-8');text=text[text.rfind('RUN_START'):]
 assert 'FAIL ' not in text, 'Failed assertions in '+str(source)
 if role!='server':assert 'ALL_' in text and '_DONE' in text, 'Client incomplete'
 (out/(role+'.txt')).write_text(text,encoding='utf-8');shutil.copy2(source/'logs/latest.log',out/(role+'.log'))
 for name in re.findall(r'Saved screenshot as ([^\r\n]+)',text):
  assert Path(name).name==name;shutil.copy2(source/'screenshots'/name,out/name)
 print(role, text.count('PASS '), 'PASS')
print(out)
