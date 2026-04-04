import { makeRedirectUri } from "expo-auth-session";
import { Platform } from "react-native";

const COGNITO_DOMAIN = process.env.EXPO_PUBLIC_COGNITO_DOMAIN ?? "";
const COGNITO_CLIENT_ID = process.env.EXPO_PUBLIC_COGNITO_CLIENT_ID ?? "";

export const cognitoClientId = COGNITO_CLIENT_ID;

export const discovery = {
  authorizationEndpoint: `https://${COGNITO_DOMAIN}/oauth2/authorize`,
  tokenEndpoint: `https://${COGNITO_DOMAIN}/oauth2/token`,
  revocationEndpoint: `https://${COGNITO_DOMAIN}/oauth2/revoke`,
};

export const logoutUrl = `https://${COGNITO_DOMAIN}/logout`;

export function getRedirectUri(): string {
  return makeRedirectUri({
    scheme: "todoapp",
    path: "auth/callback",
    ...(Platform.OS === "web" ? { preferLocalhost: true } : {}),
  });
}
