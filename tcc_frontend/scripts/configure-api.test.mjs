import { test } from 'node:test';
import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import { readFileSync, writeFileSync } from 'node:fs';

const script = new URL('./configure-api.mjs', import.meta.url);
const generated = new URL('../src/app/core/config/api.generated.ts', import.meta.url);
const original = readFileSync(generated, 'utf8');
const run = (value, production = true) => spawnSync(process.execPath,
  [fileURLToPath(script), ...(production ? ['--production'] : [])],
  { env: { ...process.env, API_URL: value }, encoding: 'utf8' });

test('configuracao de API por ambiente', () => {
  try {
    assert.notEqual(run('').status, 0);
    assert.notEqual(run('http://api.example.com').status, 0);
    assert.notEqual(run('https://user:secret@api.example.com').status, 0);
    assert.notEqual(run('https://api.example.com/tarefas').status, 0);
    assert.notEqual(run('https://api.example.com?key=secret').status, 0);
    assert.equal(run('https://api.example.com/').status, 0);
    assert.match(readFileSync(generated, 'utf8'), /https:\/\/api\.example\.com/);
    assert.equal(run('', false).status, 0);
    assert.match(readFileSync(generated, 'utf8'), /http:\/\/localhost:8080/);
  } finally {
    writeFileSync(generated, original);
  }
});
