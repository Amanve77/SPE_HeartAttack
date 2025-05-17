import axios from 'axios';

const API = axios.create({ baseURL: 'http://localhost:8081/api' });

// Request interceptor for API calls
API.interceptors.request.use((req) => {
  const token = localStorage.getItem('token');
  if (token) {
    // The token already includes 'Bearer ' prefix when stored
    req.headers.Authorization = token;
  }
  return req;
});

// Response interceptor for API calls
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear token and redirect to login if unauthorized
      localStorage.clear();
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

export default API;
