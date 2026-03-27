import { useEffect, useState } from "react";
import { apiFetch } from "../api/client.js";
import Pagination from "../components/Pagination.jsx";

export default function TagPage() {
  const [tags, setTags] = useState([]);
  const [name, setName] = useState("");
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalPages: 0, totalElements: 0 });

  const loadTags = async (requestedPage = page) => {
    try {
      const data = await apiFetch(`/api/tags?page=${requestedPage}&size=20`);
      setTags(data?._embedded?.tags || []);
      setPageInfo(data?.page || { totalPages: 0, totalElements: 0 });
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadTags(page);
  }, [page]);

  const handleCreate = async (event) => {
    event.preventDefault();
    setError(null);
    try {
      await apiFetch("/api/tags", {
        method: "POST",
        body: JSON.stringify({ name }),
      });
      setName("");
      setPage(0);
      loadTags(0);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (tag) => {
    await apiFetch(tag._links.delete.href, { method: "DELETE" });
    loadTags(page);
  };

  const handleRename = async (tag, newName) => {
    await apiFetch(tag._links.update.href, {
      method: "PUT",
      body: JSON.stringify({ name: newName }),
    });
    loadTags(page);
  };

  return (
    <div>
      <h2 className="mb-3">Tags</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="card mb-4">
        <div className="card-body">
          <h5 className="card-title">Create tag</h5>
          <form onSubmit={handleCreate}>
            <div className="input-group">
              <input
                className="form-control"
                value={name}
                onChange={(event) => setName(event.target.value)}
                placeholder="New tag name"
                required
              />
              <button className="btn btn-primary">Add</button>
            </div>
          </form>
        </div>
      </div>

      <div className="list-group">
        {tags.length === 0 && <div className="text-muted">No tags yet.</div>}
        {tags.map((tag) => (
          <TagRow key={tag.id} tag={tag} onDelete={handleDelete} onRename={handleRename} />
        ))}
      </div>

      {pageInfo.totalElements > 0 && (
        <div className="text-muted text-center small mt-2">
          {pageInfo.totalElements} tag{pageInfo.totalElements !== 1 && "s"} total
        </div>
      )}
      <Pagination page={page} totalPages={pageInfo.totalPages} onPageChange={setPage} />
    </div>
  );
}

function TagRow({ tag, onDelete, onRename }) {
  const [editValue, setEditValue] = useState(tag.name);
  const [editing, setEditing] = useState(false);

  const handleSave = () => {
    onRename(tag, editValue);
    setEditing(false);
  };

  return (
    <div className="list-group-item d-flex justify-content-between align-items-center">
      {editing ? (
        <input
          className="form-control me-2"
          value={editValue}
          onChange={(event) => setEditValue(event.target.value)}
        />
      ) : (
        <span>{tag.name}</span>
      )}
      <div className="d-flex gap-2">
        {editing ? (
          <button className="btn btn-outline-primary btn-sm" onClick={handleSave}>
            Save
          </button>
        ) : (
          <button className="btn btn-outline-secondary btn-sm" onClick={() => setEditing(true)}>
            Rename
          </button>
        )}
        <button className="btn btn-outline-danger btn-sm" onClick={() => onDelete(tag)}>
          Delete
        </button>
      </div>
    </div>
  );
}
