import { Platform } from "react-native";
import {
  getToken, setToken, getRefreshToken, setRefreshToken, clearAllTokens,
} from "./tokenStorage";

function resolveBaseUrl(): string {
  const envUrl = process.env.EXPO_PUBLIC_API_BASE_URL;
  if (envUrl) return envUrl;
  switch (Platform.OS) {
    case "android":
      return "http://10.0.2.2:8080";
    default:
      return "http://localhost:8080";
  }
}

const API_BASE_URL = resolveBaseUrl();

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

async function tryRefresh(): Promise<boolean> {
  const refreshToken = await getRefreshToken();
  if (!refreshToken) return false;

  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });
    if (!response.ok) {
      await clearAllTokens();
      return false;
    }
    const data = await response.json();
    await setToken(data.token);
    await setRefreshToken(data.refreshToken);
    return true;
  } catch {
    await clearAllTokens();
    return false;
  }
}

export async function apiFetch(path: string, options: RequestInit = {}): Promise<any> {
  const url = path.startsWith("http") ? path : `${API_BASE_URL}${path}`;
  const token = await getToken();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string> || {}),
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(url, { ...options, headers });

  if (response.status === 204) return null;

  if (response.status === 401) {
    if (!isRefreshing) {
      isRefreshing = true;
      refreshPromise = tryRefresh().finally(() => { isRefreshing = false; });
    }
    const refreshed = await refreshPromise;
    if (refreshed) {
      const newToken = await getToken();
      headers["Authorization"] = `Bearer ${newToken}`;
      const retry = await fetch(url, { ...options, headers });
      if (retry.status === 204) return null;
      if (!retry.ok) {
        await clearAllTokens();
        throw new Error("Unauthorized");
      }
      return retry.json();
    }
    await clearAllTokens();
    throw new Error("Unauthorized");
  }

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.message || "Request failed");
  }
  return data;
}

export const auth = {
  login: async (username: string, password: string) => {
    const data = await apiFetch("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });
    if (data.token) {
      await setToken(data.token);
    }
    if (data.refreshToken) {
      await setRefreshToken(data.refreshToken);
    }
    return data;
  },

  register: (username: string, password: string) =>
    apiFetch("/api/auth/register", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    }),

  logout: async () => {
    await clearAllTokens();
  },

  me: async () => {
    try {
      return await apiFetch("/api/auth/me");
    } catch {
      return null;
    }
  },

  hasToken: async () => {
    const token = await getToken();
    return !!token;
  },
};
