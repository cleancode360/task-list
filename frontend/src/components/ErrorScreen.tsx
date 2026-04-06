import { Pressable, StyleSheet, Text, View } from "react-native";

interface Props {
  message: string;
  onRetry: () => void;
}

export default function ErrorScreen({ message, onRetry }: Props) {
  return (
    <View style={styles.centered}>
      <Text style={styles.message}>{message}</Text>
      <Pressable style={styles.retryBtn} onPress={onRetry}>
        <Text style={styles.retryBtnText}>Retry</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  centered: { flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#f5f5f5", paddingHorizontal: 32 },
  message: { backgroundColor: "#f8d7da", color: "#721c24", padding: 12, borderRadius: 8, overflow: "hidden", textAlign: "center" },
  retryBtn: { marginTop: 16, backgroundColor: "#007bff", borderRadius: 8, paddingHorizontal: 24, paddingVertical: 10 },
  retryBtnText: { color: "#fff", fontWeight: "600" },
});
