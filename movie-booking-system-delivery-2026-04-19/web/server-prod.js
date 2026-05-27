import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const distDir = path.join(__dirname, 'dist');
const indexFile = path.join(distDir, 'index.html');

const mimeTypes = {
  '.css': 'text/css; charset=utf-8',
  '.gif': 'image/gif',
  '.html': 'text/html; charset=utf-8',
  '.ico': 'image/x-icon',
  '.jpeg': 'image/jpeg',
  '.jpg': 'image/jpeg',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.map': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.svg': 'image/svg+xml; charset=utf-8',
  '.txt': 'text/plain; charset=utf-8',
  '.webp': 'image/webp',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2'
};

function getArgValue(flag) {
  const index = process.argv.indexOf(flag);
  if (index >= 0 && process.argv[index + 1]) {
    return process.argv[index + 1];
  }
  return null;
}

function isInsideDist(targetPath) {
  const relativePath = path.relative(distDir, targetPath);
  return relativePath && !relativePath.startsWith('..') && !path.isAbsolute(relativePath);
}

async function fileExists(targetPath) {
  try {
    const stats = await fs.promises.stat(targetPath);
    return stats.isFile();
  } catch (error) {
    return false;
  }
}

async function resolveFilePath(urlPathname) {
  const pathname = decodeURIComponent(urlPathname || '/');
  const normalizedPath = pathname === '/' ? '/index.html' : path.normalize(pathname);
  const directPath = path.resolve(distDir, `.${normalizedPath}`);

  if (!isInsideDist(directPath)) {
    return false;
  }

  if (await fileExists(directPath)) {
    return directPath;
  }

  if (!path.extname(directPath)) {
    const nestedIndexFile = path.join(directPath, 'index.html');
    if (await fileExists(nestedIndexFile)) {
      return nestedIndexFile;
    }

    return indexFile;
  }

  return null;
}

function sendFile(response, targetPath) {
  const extension = path.extname(targetPath).toLowerCase();
  const contentType = mimeTypes[extension] || 'application/octet-stream';
  const headers = {
    'Content-Type': contentType
  };

  if (targetPath === indexFile || targetPath.endsWith(`${path.sep}runtime-config.js`)) {
    headers['Cache-Control'] = 'no-store';
  }

  const stream = fs.createReadStream(targetPath);
  stream.on('open', () => {
    response.writeHead(200, headers);
    stream.pipe(response);
  });
  stream.on('error', () => {
    response.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
    response.end('Internal Server Error');
  });
}

const host = getArgValue('--host') || process.env.HOST || '0.0.0.0';
const port = Number(getArgValue('--port') || process.env.PORT || '5173');

if (!fs.existsSync(indexFile)) {
  console.error(`[ERROR] dist/index.html was not found in ${distDir}`);
  process.exit(1);
}

const server = http.createServer(async (request, response) => {
  const url = new URL(request.url || '/', `http://${request.headers.host || '127.0.0.1'}`);
  const targetPath = await resolveFilePath(url.pathname);

  if (targetPath === false) {
    response.writeHead(403, { 'Content-Type': 'text/plain; charset=utf-8' });
    response.end('Forbidden');
    return;
  }

  if (!targetPath || !await fileExists(targetPath)) {
    response.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
    response.end('Not Found');
    return;
  }

  sendFile(response, targetPath);
});

server.listen(port, host, () => {
  console.log(`[INFO] Static frontend server running at http://${host}:${port}`);
});
