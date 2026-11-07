import axios from 'axios';

/**
 * Instancia de axios preconfigurada para consumir la API del backend.
 * La URL base '/api' es redirigida por el proxy de Vite hacia
 * http://localhost:8080 durante el desarrollo.
 */
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

export default api;
