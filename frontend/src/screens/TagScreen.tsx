import { useCallback, useState } from "react";
import {
  View, Text, TextInput, Pressable, FlatList, StyleSheet, Alert, RefreshControl, Modal,
} from "react-native";
import LoadingScreen from "../components/LoadingScreen";
import ErrorBanner from "../components/ErrorBanner";
import { useFocusEffect } from "@react-navigation/native";
import { apiFetch } from "../api/client";

interface TagItem {
  id: number;
  name: string;
  _links: Record<string, { href: string }>;
}

interface PageInfo {
  number: number;
  totalPages: number;
  totalElements: number;
}

export default function TagScreen() {
  const [tags, setTags] = useState<TagItem[]>([]);
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState<PageInfo>({ number: 0, totalPages: 0, totalElements: 0 });
  const [name, setName] = useState("");
  const [refreshing, setRefreshing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [renameTag, setRenameTag] = useState<TagItem | null>(null);
  const [renameName, setRenameName] = useState("");

  const loadTags = useCallback(async (p = page) => {
    try {
      setError(null);
      const data = await apiFetch(`/api/tags?page=${p}&size=20`);
      setTags(data?._embedded?.tags || []);
      setPageInfo(data?.page || { number: 0, totalPages: 0, totalElements: 0 });
    } catch (err: any) {
      setError(err.message ?? "Failed to load tags");
    } finally {
      setLoading(false);
    }
  }, [page]);

  useFocusEffect(useCallback(() => { loadTags(page); }, [page]));

  const onRefresh = async () => {
    setRefreshing(true);
    await loadTags(page);
    setRefreshing(false);
  };

  const handleCreate = async () => {
    if (!name.trim()) return;
    try {
      await apiFetch("/api/tags", { method: "POST", body: JSON.stringify({ name }) });
      setName("");
      setPage(0);
      loadTags(0);
    } catch (err: any) {
      Alert.alert("Error", err.message);
    }
  };

  const handleDelete = async (tag: TagItem) => {
    try {
      await apiFetch(tag._links.delete.href, { method: "DELETE" });
      loadTags(page);
    } catch (err: any) {
      Alert.alert("Error", err.message ?? "Failed to delete tag");
    }
  };

  const openRenameModal = (tag: TagItem) => {
    setRenameTag(tag);
    setRenameName(tag.name);
  };

  const handleRenameSubmit = async () => {
    if (!renameTag || !renameName.trim()) return;
    try {
      await apiFetch(renameTag._links.update.href, {
        method: "PUT",
        body: JSON.stringify({ name: renameName.trim() }),
      });
      setRenameTag(null);
      setRenameName("");
      loadTags(page);
    } catch (err: any) {
      Alert.alert("Error", err.message ?? "Failed to rename tag");
    }
  };

  const renderTag = ({ item }: { item: TagItem }) => (
    <View style={styles.tagRow}>
      <Text style={styles.tagName}>{item.name}</Text>
      <View style={styles.actions}>
        <Pressable style={styles.actionBtn} onPress={() => openRenameModal(item)}>
          <Text style={styles.actionText}>Rename</Text>
        </Pressable>
        <Pressable style={[styles.actionBtn, styles.deleteBtn]} onPress={() => handleDelete(item)}>
          <Text style={[styles.actionText, styles.deleteText]}>Delete</Text>
        </Pressable>
      </View>
    </View>
  );

  if (loading) {
    return <LoadingScreen />;
  }

  return (
    <View style={styles.container}>
      {error && (
        <ErrorBanner message={error} onRetry={() => { setLoading(true); loadTags(page); }} />
      )}

      <View style={styles.formRow}>
        <TextInput style={styles.input} placeholder="New tag name" value={name} onChangeText={setName} />
        <Pressable style={styles.addBtn} onPress={handleCreate}>
          <Text style={styles.addBtnText}>Add</Text>
        </Pressable>
      </View>

      <FlatList
        data={tags}
        keyExtractor={(item) => String(item.id)}
        renderItem={renderTag}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        ListEmptyComponent={<Text style={styles.empty}>No tags yet.</Text>}
        contentContainerStyle={tags.length === 0 ? styles.emptyContainer : undefined}
      />

      {pageInfo.totalPages > 1 && (
        <View style={styles.pagination}>
          <Pressable disabled={page === 0} onPress={() => setPage(page - 1)} style={[styles.pageBtn, page === 0 && styles.pageBtnDisabled]}>
            <Text style={styles.pageBtnText}>Previous</Text>
          </Pressable>
          <Text style={styles.pageInfoText}>{page + 1} / {pageInfo.totalPages}</Text>
          <Pressable disabled={page >= pageInfo.totalPages - 1} onPress={() => setPage(page + 1)} style={[styles.pageBtn, page >= pageInfo.totalPages - 1 && styles.pageBtnDisabled]}>
            <Text style={styles.pageBtnText}>Next</Text>
          </Pressable>
        </View>
      )}

      <Modal visible={!!renameTag} transparent animationType="fade" onRequestClose={() => setRenameTag(null)}>
        <Pressable style={styles.modalOverlay} onPress={() => setRenameTag(null)}>
          <Pressable style={styles.modalCard} onPress={() => {}}>
            <Text style={styles.modalTitle}>Rename Tag</Text>
            <TextInput
              style={styles.modalInput}
              value={renameName}
              onChangeText={setRenameName}
              autoFocus
              selectTextOnFocus
            />
            <View style={styles.modalButtons}>
              <Pressable style={styles.modalCancelBtn} onPress={() => setRenameTag(null)}>
                <Text style={styles.modalCancelText}>Cancel</Text>
              </Pressable>
              <Pressable style={styles.modalSaveBtn} onPress={handleRenameSubmit}>
                <Text style={styles.modalSaveText}>Save</Text>
              </Pressable>
            </View>
          </Pressable>
        </Pressable>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f5f5f5" },
  formRow: { flexDirection: "row", padding: 16, gap: 10 },
  input: { flex: 1, borderWidth: 1, borderColor: "#ddd", borderRadius: 8, padding: 12, fontSize: 16, backgroundColor: "#fff" },
  addBtn: { backgroundColor: "#007bff", borderRadius: 8, paddingHorizontal: 20, justifyContent: "center" },
  addBtnText: { color: "#fff", fontWeight: "600", fontSize: 16 },
  tagRow: { flexDirection: "row", backgroundColor: "#fff", marginHorizontal: 16, marginVertical: 4, padding: 14, borderRadius: 10, elevation: 1, shadowColor: "#000", shadowOpacity: 0.05, shadowRadius: 4, shadowOffset: { width: 0, height: 1 }, alignItems: "center" },
  tagName: { flex: 1, fontSize: 16, fontWeight: "500" },
  actions: { flexDirection: "row", gap: 6 },
  actionBtn: { borderWidth: 1, borderColor: "#6c757d", borderRadius: 6, paddingHorizontal: 10, paddingVertical: 6 },
  actionText: { color: "#6c757d", fontSize: 12, fontWeight: "600" },
  deleteBtn: { borderColor: "#dc3545" },
  deleteText: { color: "#dc3545" },
  empty: { textAlign: "center", color: "#888", fontSize: 16 },
  emptyContainer: { flex: 1, justifyContent: "center" },
  pagination: { flexDirection: "row", justifyContent: "center", alignItems: "center", paddingVertical: 12, gap: 16 },
  pageBtn: { backgroundColor: "#007bff", borderRadius: 6, paddingHorizontal: 16, paddingVertical: 8 },
  pageBtnDisabled: { opacity: 0.4 },
  pageBtnText: { color: "#fff", fontWeight: "600" },
  pageInfoText: { fontSize: 14, fontWeight: "600" },
  modalOverlay: { flex: 1, backgroundColor: "rgba(0,0,0,0.4)", justifyContent: "center", alignItems: "center" },
  modalCard: { backgroundColor: "#fff", borderRadius: 12, padding: 24, width: "80%", maxWidth: 360, elevation: 5, shadowColor: "#000", shadowOpacity: 0.15, shadowRadius: 12, shadowOffset: { width: 0, height: 4 } },
  modalTitle: { fontSize: 18, fontWeight: "700", marginBottom: 16 },
  modalInput: { borderWidth: 1, borderColor: "#ddd", borderRadius: 8, padding: 12, fontSize: 16, marginBottom: 20 },
  modalButtons: { flexDirection: "row", justifyContent: "flex-end", gap: 10 },
  modalCancelBtn: { borderWidth: 1, borderColor: "#6c757d", borderRadius: 8, paddingHorizontal: 16, paddingVertical: 10 },
  modalCancelText: { color: "#6c757d", fontWeight: "600" },
  modalSaveBtn: { backgroundColor: "#007bff", borderRadius: 8, paddingHorizontal: 16, paddingVertical: 10 },
  modalSaveText: { color: "#fff", fontWeight: "600" },
});
