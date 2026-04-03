import axios from 'axios';
import { useAuthStore } from '../store/useAuthStore';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authService = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },
  register: async (email: string, password: string) => {
    const response = await api.post('/auth/register', { email, password });
    return response.data;
  },
};

export const portfolioService = {
  getPortfolios: async () => {
    const response = await api.get('/portfolios');
    return response.data;
  },
  createPortfolio: async (data: { name: string; baseCurrency: string }) => {
    const response = await api.post('/portfolios', data);
    return response.data;
  },
  getPositions: async (portfolioId: string | number) => {
    const response = await api.get(`/positions?portfolioId=${portfolioId}`);
    return response.data;
  },
  getTrades: async (portfolioId: string | number) => {
    const response = await api.get(`/trades?portfolioId=${portfolioId}`);
    return response.data;
  },
  addTrade: async (portfolioId: string | number, symbolId: string | number, data: any) => {
    const response = await api.post(`/trades?portfolioId=${portfolioId}&symbolId=${symbolId}`, data);
    return response.data;
  },
  deleteTrade: async (tradeId: string | number) => {
    await api.delete(`/trades/${tradeId}`);
  },
};

export const marketService = {
  getSymbols: async () => {
    const response = await api.get('/symbols');
    return response.data;
  },
  addSymbol: async (symbolData: any) => {
    const response = await api.post('/symbols', symbolData);
    return response.data;
  },
  searchSymbols: async (query: string) => {
    const response = await api.get(`/symbols/search?q=${query}`);
    return response.data;
  }
};

export const aiService = {
  sendVoiceCommand: async (text: string) => {
    const response = await api.post('/voice-command', { text });
    return response.data;
  },
};

export default api;
