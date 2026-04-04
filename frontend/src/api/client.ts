import { Platform } from "react-native";
import {
  getToken, setToken, getRefreshToken, setRefreshToken,
  getIdToken, setIdToken, clearAllTokens,
} from "./tokenStorage";
import { discovery, cognitoClientId, getRedirectUri } from "./cognitoConfig";

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
    const response = await fetch(discovery.tokenEndpoint, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({
        grant_type: "refresh_token",
        client_id: cognitoClientId,
        refresh_token: refreshToken,
      }).toString(),
    });
    if (!response.ok) {
      await clearAllTokens();
      return false;
    }
    const data = await response.json();
    await setToken(data.access_token);
    if (data.id_token) await setIdToken(data.id_token);
    return true;
  } catch {
    await clearAllTokens();
    return false;
  }
}

export async function apiFetch(path: string, options: RequestInit = {}): Promise<any> {
  let url: string;
  if (path.startsWith("http")) {
    const parsed = new URL(path);
    url = `${API_BASE_URL}${parsed.pathname}${parsed.search}`;
  } else {
    url = `${API_BASE_URL}${path}`;
  }
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

export async function storeTokensFromCodeExchange(tokenResponse: {
  accessToken: string;
  idToken?: string;
  refreshToken?: string;
}): Promise<void> {
  await setToken(tokenResponse.accessToken);
  if (tokenResponse.idToken) await setIdToken(tokenResponse.idToken);
  if (tokenResponse.refreshToken) await setRefreshToken(tokenResponse.refreshToken);
}

export const auth = {
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
