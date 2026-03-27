import { useCallback, useEffect, useState } from "react";
import {
  View, Text, TextInput, Pressable, FlatList, StyleSheet, Alert, RefreshControl,
} from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { TaskStackParamList } from "../../App";
import { apiFetch, auth } from "../api/client";

type Props = NativeStackScreenProps<TaskStackParamList, "TaskList">;

interface TaskSummary {
  id: number;
  title: string;
  completed: boolean;
  tags: { id: number; name: string }[];
  _links: Record<string, { href: string }>;
}

interface PageInfo {
  number: number;
  totalPages: number;
  totalElements: number;
}

export default function TaskListScreen({ navigation }: Props) {
  const [tasks, setTasks] = useState<TaskSummary[]>([]);
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState<PageInfo>({ number: 0, totalPages: 0, totalElements: 0 });
  const [refreshing, setRefreshing] = useState(false);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [tags, setTags] = useState("");
  const [showForm, setShowForm] = useState(false);

  const loadTasks = useCallback(async (p = page) => {
    try {
      const data = await apiFetch(`/api/tasks?page=${p}&size=20&sort=createdAt,desc`);
      setTasks(data?._embedded?.tasks || []);
      setPageInfo(data?.page || { number: 0, totalPages: 0, totalElements: 0 });
    } catch {
      // handled by apiFetch
    }
  }, [page]);

  useEffect(() => { loadTasks(page); }, [page]);

  const onRefresh = async () => {
    setRefreshing(true);
    await loadTasks(page);
    setRefreshing(false);
  };

  const handleCreate = async () => {
    if (!title.trim()) return;
    try {
      const tagNames = tags ? tags.split(",").map((s) => s.trim()).filter(Boolean) : null;
      await apiFetch("/api/tasks", {
        method: "POST",
        body: JSON.stringify({ title, description: description || null, tagNames }),
      });
      setTitle("");
      setDescription("");
      setTags("");
      setShowForm(false);
      setPage(0);
      loadTasks(0);
    } catch (err: any) {
      Alert.alert("Error", err.message);
    }
  };

  const handleToggle = async (task: TaskSummary) => {
    await apiFetch(task._links.toggle.href, { method: "POST" });
    loadTasks(page);
  };

  const handleDelete = async (task: TaskSummary) => {
    await apiFetch(task._links.delete.href, { method: "DELETE" });
    loadTasks(page);
  };

  const handleLogout = async () => {
    await auth.logout();
  };

  const renderTask = ({ item }: { item: TaskSummary }) => (
    <Pressable style={styles.taskRow} onPress={() => navigation.navigate("TaskDetail", { id: item.id })}>
      <View style={{ flex: 1 }}>
        <Text style={[styles.taskTitle, item.completed && styles.completed]}>{item.title}</Text>
        <Text style={styles.status}>{item.completed ? "Completed" : "Open"}</Text>
        {item.tags?.length > 0 && (
          <View style={styles.tagsRow}>
            {item.tags.map((tag) => (
              <View key={tag.id} style={styles.badge}>
                <Text style={styles.badgeText}>{tag.name}</Text>
              </View>
            ))}
          </View>
        )}
      </View>
      <View style={styles.actions}>
        <Pressable style={styles.actionBtn} onPress={() => handleToggle(item)}>
          <Text style={styles.actionText}>Toggle</Text>
        </Pressable>
        <Pressable style={[styles.actionBtn, styles.deleteBtn]} onPress={() => handleDelete(item)}>
          <Text style={[styles.actionText, styles.deleteText]}>Delete</Text>
        </Pressable>
      </View>
    </Pressable>
  );

  return (
    <View style={styles.container}>
      <Pressable style={styles.addButton} onPress={() => setShowForm(!showForm)}>
        <Text style={styles.addButtonText}>{showForm ? "Cancel" : "+ New Task"}</Text>
      </Pressable>

      {showForm && (
        <View style={styles.formCard}>
          <TextInput style={styles.input} placeholder="Title" value={title} onChangeText={setTitle} />
          <TextInput style={styles.input} placeholder="Description" value={description} onChangeText={setDescription} multiline />
          <TextInput style={styles.input} placeholder="Tags (comma separated)" value={tags} onChangeText={setTags} />
          <Pressable style={styles.submitBtn} onPress={handleCreate}>
            <Text style={styles.submitText}>Add Task</Text>
          </Pressable>
        </View>
      )}

      <FlatList
        data={tasks}
        keyExtractor={(item) => String(item.id)}
        renderItem={renderTask}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        ListEmptyComponent={<Text style={styles.empty}>No tasks yet.</Text>}
        contentContainerStyle={tasks.length === 0 ? styles.emptyContainer : undefined}
      />

      {pageInfo.totalPages > 1 && (
        <View style={styles.pagination}>
          <Pressable disabled={page === 0} onPress={() => setPage(page - 1)} style={[styles.pageBtn, page === 0 && styles.pageBtnDisabled]}>
            <Text style={styles.pageBtnText}>Previous</Text>
          </Pressable>
          <Text style={styles.pageInfo}>{page + 1} / {pageInfo.totalPages}</Text>
          <Pressable disabled={page >= pageInfo.totalPages - 1} onPress={() => setPage(page + 1)} style={[styles.pageBtn, page >= pageInfo.totalPages - 1 && styles.pageBtnDisabled]}>
            <Text style={styles.pageBtnText}>Next</Text>
          </Pressable>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f5f5f5" },
  addButton: { margin: 16, marginBottom: 8, backgroundColor: "#007bff", borderRadius: 8, padding: 12, alignItems: "center" },
  addButtonText: { color: "#fff", fontWeight: "600", fontSize: 16 },
  formCard: { marginHorizontal: 16, marginBottom: 8, backgroundColor: "#fff", borderRadius: 12, padding: 16, elevation: 2, shadowColor: "#000", shadowOpacity: 0.08, shadowRadius: 6, shadowOffset: { width: 0, height: 1 } },
  input: { borderWidth: 1, borderColor: "#ddd", borderRadius: 8, padding: 12, fontSize: 16, marginBottom: 10 },
  submitBtn: { backgroundColor: "#007bff", borderRadius: 8, padding: 12, alignItems: "center" },
  submitText: { color: "#fff", fontWeight: "600" },
  taskRow: { flexDirection: "row", backgroundColor: "#fff", marginHorizontal: 16, marginVertical: 4, padding: 14, borderRadius: 10, elevation: 1, shadowColor: "#000", shadowOpacity: 0.05, shadowRadius: 4, shadowOffset: { width: 0, height: 1 } },
  taskTitle: { fontSize: 16, fontWeight: "600" },
  completed: { textDecorationLine: "line-through", color: "#888" },
  status: { fontSize: 12, color: "#888", marginTop: 2 },
  tagsRow: { flexDirection: "row", flexWrap: "wrap", marginTop: 6 },
  badge: { backgroundColor: "#6c757d", borderRadius: 12, paddingHorizontal: 8, paddingVertical: 3, marginRight: 4, marginBottom: 2 },
  badgeText: { color: "#fff", fontSize: 11, fontWeight: "600" },
  actions: { justifyContent: "center", gap: 6 },
  actionBtn: { borderWidth: 1, borderColor: "#007bff", borderRadius: 6, paddingHorizontal: 10, paddingVertical: 6 },
  actionText: { color: "#007bff", fontSize: 12, fontWeight: "600" },
  deleteBtn: { borderColor: "#dc3545" },
  deleteText: { color: "#dc3545" },
  empty: { textAlign: "center", color: "#888", fontSize: 16 },
  emptyContainer: { flex: 1, justifyContent: "center" },
  pagination: { flexDirection: "row", justifyContent: "center", alignItems: "center", paddingVertical: 12, gap: 16 },
  pageBtn: { backgroundColor: "#007bff", borderRadius: 6, paddingHorizontal: 16, paddingVertical: 8 },
  pageBtnDisabled: { opacity: 0.4 },
  pageBtnText: { color: "#fff", fontWeight: "600" },
  pageInfo: { fontSize: 14, fontWeight: "600" },
});
