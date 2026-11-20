
const CACHE_NAME = "morais-drive-v18";

const FILES = [
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

  event.respondWith(
    caches.match(event.request).then(cached => {
      return cached || fetch(event.request);
    })
  );
});
