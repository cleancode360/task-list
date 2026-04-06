import { ActivityIndicator, StyleSheet, View } from "react-native";

interface Props {
  color?: string;
}

export default function LoadingScreen({ color = "#007bff" }: Props) {
  return (
    <View style={styles.centered}>
      <ActivityIndicator size="large" color={color} />
    </View>
  );
}

const styles = StyleSheet.create({
  centered: { flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#f5f5f5" },
});
