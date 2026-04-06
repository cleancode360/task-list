import { Pressable, StyleSheet, Text, View } from "react-native";

interface Props {
  message: string;
  onRetry?: () => void;
  onDismiss?: () => void;
}

export default function ErrorBanner({ message, onRetry, onDismiss }: Props) {
  return (
    <View style={styles.banner}>
      <Text style={styles.text}>{message}</Text>
      {onRetry && (
        <Pressable onPress={onRetry}>
          <Text style={styles.retry}>Retry</Text>
        </Pressable>
      )}
      {onDismiss && (
        <Pressable onPress={onDismiss}>
          <Text style={styles.dismiss}>✕</Text>
        </Pressable>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  banner: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", backgroundColor: "#f8d7da", marginHorizontal: 16, marginTop: 12, padding: 12, borderRadius: 8 },
  text: { color: "#721c24", flex: 1, fontWeight: "500" },
  retry: { color: "#007bff", fontWeight: "600", marginLeft: 12 },
  dismiss: { color: "#721c24", fontWeight: "600", marginLeft: 12, fontSize: 16 },
});
