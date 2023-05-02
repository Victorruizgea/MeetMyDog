import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('/sw.js')
        .then(registration => {
          console.log('Service Worker registrado!', registration.scope);
        })
        .catch(error => {
          console.error('Error al registrar el Service Worker:', error);
        });
    });
}

ReactDOM.render(<App />, document.getElementById('root'));
