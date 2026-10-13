import { writeFileSync } from 'node:fs';

const production = process.argv.includes('--production');
const value = process.env.API_URL?.trim() || (production ? '' : 'http://localhost:8080');
if (!value) throw new Error('Defina API_URL com o endereco HTTPS do backend antes do build.');
const url = new URL(value);
if (url.username || url.password || url.search || url.hash || url.pathname !== '/') {
  throw new Error('API_URL deve conter apenas a origem da API, sem credenciais, caminho ou parametros.');
}
if (production ? url.protocol !== 'https:' : !['http:', 'https:'].includes(url.protocol)) {
  throw new Error('API_URL deve usar HTTPS em producao.');
}
writeFileSync(new URL('../src/app/core/config/api.generated.ts', import.meta.url),
  `// Gerado pelo script de configuracao antes de iniciar ou compilar.\nexport const apiUrl = ${JSON.stringify(url.origin)};\n`);
