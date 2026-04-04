import { Platform } from "react-native";
import * as SecureStore from "expo-secure-store";

const ACCESS_TOKEN_KEY = "auth_token";
const REFRESH_TOKEN_KEY = "refresh_token";
const ID_TOKEN_KEY = "id_token";

function useLocalStorage(): boolean {
  return Platform.OS === "web";
}

async function get(key: string): Promise<string | null> {
  if (useLocalStorage()) {
    return localStorage.getItem(key);
  }
  return SecureStore.getItemAsync(key);
}

async function set(key: string, value: string): Promise<void> {
  if (useLocalStorage()) {
    localStorage.setItem(key, value);
    return;
  }
  await SecureStore.setItemAsync(key, value);
}

async function remove(key: string): Promise<void> {
  if (useLocalStorage()) {
    localStorage.removeItem(key);
    return;
  }
  await SecureStore.deleteItemAsync(key);
}

export const getToken = () => get(ACCESS_TOKEN_KEY);
export const setToken = (token: string) => set(ACCESS_TOKEN_KEY, token);
export const deleteToken = () => remove(ACCESS_TOKEN_KEY);

export const getRefreshToken = () => get(REFRESH_TOKEN_KEY);
export const setRefreshToken = (token: string) => set(REFRESH_TOKEN_KEY, token);
export const deleteRefreshToken = () => remove(REFRESH_TOKEN_KEY);

export const getIdToken = () => get(ID_TOKEN_KEY);
export const setIdToken = (token: string) => set(ID_TOKEN_KEY, token);
export const deleteIdToken = () => remove(ID_TOKEN_KEY);

export async function clearAllTokens(): Promise<void> {
  await deleteToken();
  await deleteRefreshToken();
  await deleteIdToken();
}
