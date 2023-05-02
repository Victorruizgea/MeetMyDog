// Nombre del caché
const CACHE_NAME = 'pwa-cache-v1';

// Archivos a cachear
const urlsToCache = [
  '/',
  '/css/styles.css',
  '/js/main.js',
  '/images/logo.png',
  'https://cdn.jsdelivr.net/npm/vue@2.6.11/dist/vue.min.js'
];

// Instalación del Service Worker
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(urlsToCache))
  );
});

// Intercepta las solicitudes y maneja la respuesta
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        // Devuelve la respuesta desde caché si está disponible
        if (response) {
          return response;
        }

        // Si la respuesta no está en caché, solicita la respuesta desde la red
        return fetch(event.request);
      })
  );
});