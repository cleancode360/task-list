import { Platform } from "react-native";
import { getToken, setToken, deleteToken } from "./tokenStorage";

function resolveBaseUrl(): string {
  const envUrl = process.env.EXPO_PUBLIC_API_BASE_URL;
  if (envUrl) return envUrl;
  if (!__DEV__) return "http://localhost:8080";
  switch (Platform.OS) {
    case "android":
      return "http://10.0.2.2:8080";
    case "web":
      return "http://localhost:8080";
    default: // ios
      return "http://localhost:8080";
  }
}

const API_BASE_URL = resolveBaseUrl();

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
    await deleteToken();
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
    return data;
  },

  register: (username: string, password: string) =>
    apiFetch("/api/auth/register", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    }),

  logout: async () => {
    try {
      await apiFetch("/api/auth/logout", { method: "POST" });
    } catch {
      // ignore
    }
    await deleteToken();
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
