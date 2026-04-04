import { useEffect, useState } from "react";
import {
  View, Text, Pressable, StyleSheet, ActivityIndicator,
} from "react-native";
import * as AuthSession from "expo-auth-session";
import * as WebBrowser from "expo-web-browser";
import { discovery, cognitoClientId, getRedirectUri } from "../api/cognitoConfig";
import { storeTokensFromCodeExchange } from "../api/client";

WebBrowser.maybeCompleteAuthSession();

type Props = {
  onLoginSuccess: () => void;
};

export default function AuthScreen({ onLoginSuccess }: Props) {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const redirectUri = getRedirectUri();

  const [request, response, promptAsync] = AuthSession.useAuthRequest(
    {
      clientId: cognitoClientId,
      responseType: AuthSession.ResponseType.Code,
      scopes: ["openid", "profile", "email"],
      redirectUri,
      usePKCE: true,
    },
    discovery,
  );

  useEffect(() => {
    if (response?.type === "success" && response.params.code) {
      handleCodeExchange(response.params.code);
    } else if (response?.type === "error") {
      setError(response.error?.message ?? "Authentication failed");
      setLoading(false);
    } else if (response?.type === "dismiss") {
      setLoading(false);
    }
  }, [response]);

  const handleCodeExchange = async (code: string) => {
    try {
      const tokenResponse = await AuthSession.exchangeCodeAsync(
        {
          clientId: cognitoClientId,
          code,
          redirectUri,
          extraParams: {
            code_verifier: request?.codeVerifier ?? "",
          },
        },
        discovery,
      );

      await storeTokensFromCodeExchange({
        accessToken: tokenResponse.accessToken,
        idToken: tokenResponse.idToken ?? undefined,
        refreshToken: tokenResponse.refreshToken ?? undefined,
      });

      onLoginSuccess();
    } catch (err: any) {
      setError(err.message ?? "Token exchange failed");
    } finally {
      setLoading(false);
    }
  };

  const handleSignIn = () => {
    setError(null);
    setLoading(true);
    promptAsync();
  };

  return (
    <View style={styles.container}>
      <View style={styles.card}>
        <Text style={styles.title}>Welcome</Text>
        <Text style={styles.subtitle}>Sign in to manage your tasks</Text>
        {error && <Text style={styles.error}>{error}</Text>}
        <Pressable
          style={[styles.button, (loading || !request) && styles.buttonDisabled]}
          onPress={handleSignIn}
          disabled={loading || !request}
        >
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.buttonText}>Sign in</Text>
          )}
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: "center", padding: 24, backgroundColor: "#f5f5f5" },
  card: {
    backgroundColor: "#fff", borderRadius: 12, padding: 32, alignItems: "center",
    elevation: 3, shadowColor: "#000", shadowOpacity: 0.1, shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
  },
  title: { fontSize: 28, fontWeight: "700", marginBottom: 8 },
  subtitle: { fontSize: 16, color: "#666", marginBottom: 32 },
  button: { backgroundColor: "#007bff", borderRadius: 8, padding: 14, alignItems: "center", width: "100%" },
  buttonDisabled: { opacity: 0.6 },
  buttonText: { color: "#fff", fontSize: 16, fontWeight: "600" },
  error: {
    backgroundColor: "#f8d7da", color: "#721c24", padding: 12, borderRadius: 8,
    marginBottom: 16, overflow: "hidden", width: "100%", textAlign: "center",
  },
});
