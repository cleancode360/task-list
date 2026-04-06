import { useEffect, useState } from "react";
import {
  View, Text, TextInput, Pressable, Switch, ScrollView, StyleSheet, Alert, Platform,
} from "react-native";
import LoadingScreen from "../components/LoadingScreen";
import ErrorBanner from "../components/ErrorBanner";
import ErrorScreen from "../components/ErrorScreen";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { TaskStackParamList } from "../../App";
import { apiFetch } from "../api/client";

type Props = NativeStackScreenProps<TaskStackParamList, "TaskDetail">;

interface TaskData {
  id: number;
  title: string;
  description: string;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
  tags: { id: number; name: string }[];
  _links: Record<string, { href: string }>;
}

export default function TaskDetailScreen({ route, navigation }: Props) {
  const { id } = route.params;
  const [task, setTask] = useState<TaskData | null>(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [completed, setCompleted] = useState(false);
  const [tags, setTags] = useState("");
  const [error, setError] = useState<string | null>(null);

  const loadTask = async () => {
    try {
      const data = await apiFetch(`/api/tasks/${id}`);
      setTask(data);
      setTitle(data.title || "");
      setDescription(data.description || "");
      setCompleted(data.completed || false);
      setTags(data.tags?.map((t: any) => t.name).join(", ") || "");
    } catch (err: any) {
      setError(err.message);
    }
  };

  useEffect(() => { loadTask(); }, [id]);

  const handleSave = async () => {
    setError(null);
    try {
      const tagNames = tags ? tags.split(",").map((s) => s.trim()).filter(Boolean) : null;
      await apiFetch(task!._links.update.href, {
        method: "PUT",
        body: JSON.stringify({ title, description, completed, tagNames }),
      });
      loadTask();
      Alert.alert("Saved", "Task updated.");
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleDelete = async () => {
    const doDelete = async () => {
      try {
        await apiFetch(task!._links.delete.href, { method: "DELETE" });
        navigation.goBack();
      } catch (err: any) {
        setError(err.message ?? "Failed to delete task");
      }
    };

    if (Platform.OS === "web") {
      if (window.confirm("Are you sure you want to delete this task?")) {
        await doDelete();
      }
    } else {
      Alert.alert("Delete", "Are you sure?", [
        { text: "Cancel", style: "cancel" },
        { text: "Delete", style: "destructive", onPress: doDelete },
      ]);
    }
  };

  const handleToggle = async () => {
    try {
      await apiFetch(task!._links.toggle.href, { method: "POST" });
      loadTask();
    } catch (err: any) {
      setError(err.message ?? "Failed to toggle task");
    }
  };

  if (!task) {
    if (error) {
      return <ErrorScreen message={error} onRetry={loadTask} />;
    }
    return <LoadingScreen />;
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      {error && <ErrorBanner message={error} onDismiss={() => setError(null)} />}

      {task.tags?.length > 0 && (
        <View style={styles.tagsRow}>
          {task.tags.map((tag) => (
            <View key={tag.id} style={styles.badge}>
              <Text style={styles.badgeText}>{tag.name}</Text>
            </View>
          ))}
        </View>
      )}

      <View style={styles.card}>
        <Text style={styles.label}>Title</Text>
        <TextInput style={styles.input} value={title} onChangeText={setTitle} />

        <Text style={styles.label}>Description</Text>
        <TextInput style={styles.input} value={description} onChangeText={setDescription} multiline numberOfLines={3} />

        <View style={styles.switchRow}>
          <Text style={styles.label}>Completed</Text>
          <Switch value={completed} onValueChange={setCompleted} />
        </View>

        <Text style={styles.label}>Tags (comma separated names)</Text>
        <TextInput style={styles.input} value={tags} onChangeText={setTags} placeholder="e.g. work, urgent" />

        <View style={styles.buttonRow}>
          <Pressable style={styles.saveBtn} onPress={handleSave}>
            <Text style={styles.saveBtnText}>Save</Text>
          </Pressable>
          <Pressable style={styles.toggleBtn} onPress={handleToggle}>
            <Text style={styles.toggleBtnText}>Toggle Status</Text>
          </Pressable>
        </View>
      </View>

      <View style={styles.card}>
        <Text style={styles.metaLabel}>Created: {task.createdAt}</Text>
        <Text style={styles.metaLabel}>Updated: {task.updatedAt}</Text>
      </View>

      <Pressable style={styles.deleteBtn} onPress={handleDelete}>
        <Text style={styles.deleteBtnText}>Delete Task</Text>
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f5f5f5" },
  content: { padding: 16 },
  card: { backgroundColor: "#fff", borderRadius: 12, padding: 16, marginBottom: 12, elevation: 2, shadowColor: "#000", shadowOpacity: 0.08, shadowRadius: 6, shadowOffset: { width: 0, height: 1 } },
  label: { fontSize: 14, fontWeight: "600", marginBottom: 4, marginTop: 12 },
  input: { borderWidth: 1, borderColor: "#ddd", borderRadius: 8, padding: 12, fontSize: 16 },
  switchRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 12 },
  buttonRow: { flexDirection: "row", gap: 10, marginTop: 20 },
  saveBtn: { flex: 1, backgroundColor: "#007bff", borderRadius: 8, padding: 14, alignItems: "center" },
  saveBtnText: { color: "#fff", fontWeight: "600", fontSize: 16 },
  toggleBtn: { flex: 1, borderWidth: 1, borderColor: "#6c757d", borderRadius: 8, padding: 14, alignItems: "center" },
  toggleBtnText: { color: "#6c757d", fontWeight: "600" },
  deleteBtn: { borderWidth: 1, borderColor: "#dc3545", borderRadius: 8, padding: 14, alignItems: "center", marginTop: 4 },
  deleteBtnText: { color: "#dc3545", fontWeight: "600", fontSize: 16 },
  tagsRow: { flexDirection: "row", flexWrap: "wrap", marginBottom: 12 },
  badge: { backgroundColor: "#6c757d", borderRadius: 12, paddingHorizontal: 10, paddingVertical: 4, marginRight: 6, marginBottom: 4 },
  badgeText: { color: "#fff", fontSize: 12, fontWeight: "600" },
  metaLabel: { fontSize: 13, color: "#888", marginBottom: 4 },
});
