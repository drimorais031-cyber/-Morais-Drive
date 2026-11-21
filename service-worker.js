
const CACHE_NAME = "morais-drive-v37";

const FILES = [
  "./onix_preto.jpg",
  "./onix_prata.jpg",
  "./onix_vermelho.jpg",
  "./gol_branco.jpg",
  "./gol_vermelho.jpg",
  "./gol_preto.jpg",
  "./gol_cinza.jpg",
  "./hb20_branco.jpg",
  "./hb20_preto.jpg",
  "./hb20_vermelho.jpg",
  "./argo_branco.png",
  "./argo_cinza.png",
  "./argo_preto.png",
  "./argo_vermelho.png",
  "./hb20_cinza.png",
  "./kwid_branco.png",
  "./kwid_cinza.png",
  "./kwid_preto.png",
  "./kwid_vermelho.png",
  "./mobi_branco.png",
  "./mobi_cinza.png",
  "./mobi_preto.png",
  "./mobi_vermelho.png",
  "./file_000000007074820e94f75f049f6ee1fe.png",
  "./file_00000000abac820eba582335ab5c8e6f.png",
  "./index.html",
  "./onix-garage.webp",
  "./manifest.json",
  "./icon.svg",
  "./icon-192.svg",
  "./icon-512.svg",
  "./morais-md-192.png",
  "./morais-md-512(1).png"
];

self.addEventListener("install", event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(FILES))
  );
  self.skipWaiting();
});

self.addEventListener("activate", event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(
        keys
          .filter(key => key !== CACHE_NAME)
          .map(key => caches.delete(key))
      )
    )
  );
  self.clients.claim();
});

self.addEventListener("fetch", event => {
  if (event.request.method !== "GET") return;

  // Para páginas HTML, sempre tenta buscar a versão publicada antes do cache antigo.
  if (event.request.mode === "navigate") {
    event.respondWith(
      fetch(event.request, { cache: "no-store" })
        .then(response => {
          const copy = response.clone();
          caches.open(CACHE_NAME).then(cache => cache.put("./index.html", copy)).catch(() => {});
          return response;
        })
        .catch(() => caches.match("./index.html"))
    );
    return;
  }

  event.respondWith(
    caches.match(event.request).then(cached => cached || fetch(event.request))
  );
});
