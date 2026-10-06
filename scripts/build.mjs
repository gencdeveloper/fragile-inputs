#!/usr/bin/env node
/**
 * Build step. Single source of truth: data/fragile-inputs.json.
 *  - inlines the dataset into web/index.html (from web/_template.html)
 *  - copies the dataset into the npm and pip packages
 * Run: node scripts/build.mjs
 */
import { readFileSync, writeFileSync, copyFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const dataPath = join(root, 'data', 'fragile-inputs.json');
const raw = readFileSync(dataPath, 'utf8');
const data = JSON.parse(raw);

// --- 1. inline into the web page ---
// Escape every character that would be unsafe inside an inline <script> string
// literal: "<" (so "</script>" can never close the tag) and all invisible/
// line-separator code points.
const INVISIBLE = [0x200B,0x200C,0x200D,0x2060,0xFEFF,0x00A0,0x00AD,0x202A,0x202B,
  0x202C,0x202D,0x202E,0x200E,0x200F,0x202F,0x3000,0x000B,0x3164,0x2028,0x2029,0x037E,0xFE0F];
let jsonText = JSON.stringify(data).replace(/</g, '\\u003c');
for (const cp of INVISIBLE) {
  jsonText = jsonText.split(String.fromCharCode(cp))
    .join('\\u' + cp.toString(16).toUpperCase().padStart(4, '0'));
}

const adapter = `const FI_DATA = ${jsonText};
const NOW=new Date(); const _pad=n=>String(n).padStart(2,"0");
const _ymd=d=>d.getFullYear()+"-"+_pad(d.getMonth()+1)+"-"+_pad(d.getDate());
function _resolve(x){
  const dy=x.dynamic; if(!dy) return x.value;
  const b=new Date(NOW.getFullYear(),NOW.getMonth(),NOW.getDate());
  if(dy.kind==="card-expiry"){const d=new Date(b.getFullYear(),b.getMonth()+(dy.monthOffset||0),1);return _pad(d.getMonth()+1)+"/"+String(d.getFullYear()).slice(2);}
  const d=new Date(b.getFullYear(),b.getMonth(),b.getDate());
  if(dy.years)d.setFullYear(d.getFullYear()+dy.years);
  if(dy.months)d.setMonth(d.getMonth()+dy.months);
  if(dy.days)d.setDate(d.getDate()+dy.days);
  return _ymd(d);
}
const _short={gen:"General",ind:"Industry",a11y:"Access"};
const GROUPS=FI_DATA.groups.map(g=>({id:g.id,name:g.name,desc:g.description,short:_short[g.id]||g.name}));
const CATS=FI_DATA.categories.map(c=>({id:c.id,g:c.group,name:c.name,desc:c.description,warn:c.warning}));
const D=FI_DATA.inputs.map(x=>({c:x.category,t:x.title,v:_resolve(x),test:x.tests,br:x.breaks,ex:x.expected,w:x.wcag,rv:x.hasInvisible?1:0,sp:x.spacesVisible?1:0}));`;

const template = readFileSync(join(root, 'web', '_template.html'), 'utf8');
if (!template.includes('/*__FI_ADAPTER__*/')) throw new Error('template placeholder missing');
const html = template.replace('/*__FI_ADAPTER__*/', adapter);
writeFileSync(join(root, 'web', 'index.html'), html);

// --- 2. sync dataset into packages and web ---
copyFileSync(dataPath, join(root, 'packages', 'npm', 'data.json'));
copyFileSync(dataPath, join(root, 'packages', 'pip', 'src', 'fragile_inputs', 'data.json'));
copyFileSync(dataPath, join(root, 'web', 'fragile-inputs.json'));
copyFileSync(dataPath, join(root, 'packages', 'java', 'src', 'main', 'resources', 'fragile-inputs.json'));

console.log(`built web/index.html (${(html.length/1024).toFixed(0)} KB), synced data.json to npm + pip + java + web`);
console.log(`dataset: ${data.inputs.length} inputs, ${data.categories.length} categories`);
