import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../api/client.js";

export default function TaskDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [task, setTask] = useState(null);
  const [form, setForm] = useState({ title: "", description: "", completed: false, tags: "" });
  const [error, setError] = useState(null);

  const loadTask = async () => {
    try {
      const data = await apiFetch(`/api/tasks/${id}`);
      setTask(data);
      setForm({
        title: data.title || "",
        description: data.description || "",
        completed: data.completed || false,
        tags: data.tags?.map((tag) => tag.name).join(", ") || "",
      });
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadTask();
  }, [id]);

  const handleUpdate = async (event) => {
    event.preventDefault();
    setError(null);
    try {
      const tagNames = form.tags
        ? form.tags.split(",").map((s) => s.trim()).filter(Boolean)
        : null;
      await apiFetch(task._links.update.href, {
        method: "PUT",
        body: JSON.stringify({
          title: form.title,
          description: form.description,
          completed: form.completed,
          tagNames,
        }),
      });
      loadTask();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async () => {
    await apiFetch(task._links.delete.href, { method: "DELETE" });
    navigate("/tasks");
  };

  const handleToggle = async () => {
    await apiFetch(task._links.toggle.href, { method: "POST" });
    loadTask();
  };

  if (!task) {
    return <div>{error ? <div className="alert alert-danger">{error}</div> : "Loading..."}</div>;
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2 className="mb-0">Task details</h2>
        <button className="btn btn-outline-danger" onClick={handleDelete}>
          Delete
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {task.tags?.length > 0 && (
        <div className="mb-4">
          <strong className="me-2">Tags:</strong>
          {task.tags.map((tag) => (
            <span key={tag.id} className="badge bg-secondary me-1">
              {tag.name}
            </span>
          ))}
        </div>
      )}

      <div className="card mb-4">
        <div className="card-body">
          <h5 className="card-title">Edit task</h5>
          <form onSubmit={handleUpdate}>
            <div className="mb-3">
              <label className="form-label">Title</label>
              <input
                className="form-control"
                value={form.title}
                onChange={(event) => setForm({ ...form, title: event.target.value })}
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Description</label>
              <textarea
                className="form-control"
                rows="3"
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </div>
            <div className="form-check mb-3">
              <input
                className="form-check-input"
                type="checkbox"
                checked={form.completed}
                onChange={(event) => setForm({ ...form, completed: event.target.checked })}
                id="completedToggle"
              />
              <label className="form-check-label" htmlFor="completedToggle">
                Completed
              </label>
            </div>
            <div className="mb-3">
              <label className="form-label">Tags (comma separated names)</label>
              <input
                className="form-control"
                value={form.tags}
                onChange={(event) => setForm({ ...form, tags: event.target.value })}
                placeholder="e.g. work, urgent"
              />
            </div>
            <button className="btn btn-primary me-2">Save</button>
            <button type="button" className="btn btn-outline-secondary" onClick={handleToggle}>
              Toggle status
            </button>
          </form>
        </div>
      </div>

      <div className="card">
        <div className="card-body">
          <h5 className="card-title">Metadata</h5>
          <div className="small text-muted">Created at: {task.createdAt}</div>
          <div className="small text-muted">Updated at: {task.updatedAt}</div>
        </div>
      </div>
    </div>
  );
}
