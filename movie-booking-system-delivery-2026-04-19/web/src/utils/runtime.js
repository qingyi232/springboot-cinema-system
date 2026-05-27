function getWindowRuntimeConfig() {
  if (typeof window === 'undefined') {
    return {};
  }
  return window.__APP_RUNTIME_CONFIG__ || {};
}

function getDefaultApiBaseUrl() {
  if (typeof window !== 'undefined' && window.location?.hostname) {
    return `${window.location.protocol}//${window.location.hostname}:1000`;
  }
  return 'http://127.0.0.1:1000';
}

export function getApiBaseUrl() {
  const runtimeApiBaseUrl = getWindowRuntimeConfig().apiBaseUrl;
  if (runtimeApiBaseUrl) {
    return runtimeApiBaseUrl;
  }

  const envApiBaseUrl = import.meta.env.VITE_APP_API_URL;
  if (envApiBaseUrl && envApiBaseUrl !== '/api') {
    return envApiBaseUrl;
  }

  return getDefaultApiBaseUrl();
}

export function joinApiUrl(path = '') {
  const baseUrl = getApiBaseUrl().replace(/\/+$/, '');
  if (!path) {
    return baseUrl;
  }
  return `${baseUrl}/${String(path).replace(/^\/+/, '')}`;
}
