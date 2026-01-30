import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch } from "../api/client.js";

const emptyForm = { title: "", description: "", tagIds: "" };

export default function TaskListPage() {
  const [tasks, setTasks] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(null);

  const loadTasks = async () => {
    try {
      const data = await apiFetch("/api/tasks");
      const items = data?._embedded?.tasks || [];
      setTasks(items);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadTasks();
  }, []);

  const handleToggle = async (task) => {
    await apiFetch(task._links.toggle.href, { method: "POST" });
    loadTasks();
  };

  const handleDelete = async (task) => {
    await apiFetch(task._links.delete.href, { method: "DELETE" });
    loadTasks();
  };

  const handleCreate = async (event) => {
    event.preventDefault();
    setError(null);
    try {
      const tagIds = form.tagIds
        ? form.tagIds.split(",").map((id) => Number(id.trim())).filter(Boolean)
        : null;
      await apiFetch("/api/tasks", {
        method: "POST",
        body: JSON.stringify({ title: form.title, description: form.description, tagIds }),
      });
      setForm(emptyForm);
      loadTasks();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2 className="mb-0">Tasks</h2>
        <Link className="btn btn-outline-secondary" to="/tags">
          Manage tags
        </Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card mb-4">
        <div className="card-body">
          <h5 className="card-title">Create task</h5>
          <form onSubmit={handleCreate}>
            <div className="mb-3">
              <label className="form-label">Title</label>
              <input
                className="form-control"
                value={form.title}
                onChange={(event) => setForm({ ...form, title: event.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Description</label>
              <textarea
                className="form-control"
                rows="2"
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Tag IDs (comma separated)</label>
              <input
                className="form-control"
                value={form.tagIds}
                onChange={(event) => setForm({ ...form, tagIds: event.target.value })}
              />
            </div>
            <button className="btn btn-primary">Add task</button>
          </form>
        </div>
      </div>

      <div className="list-group">
        {tasks.length === 0 && <div className="text-muted">No tasks yet.</div>}
        {tasks.map((task) => (
          <div key={task.id} className="list-group-item d-flex justify-content-between">
            <div>
              <Link to={`/tasks/${task.id}`} className="fw-semibold text-decoration-none">
                {task.title}
              </Link>
              <div className="small text-muted">
                {task.completed ? "Completed" : "Open"}
              </div>
              <div className="mt-1">
                {task.tags?.map((tag) => (
                  <span key={tag.id} className="badge bg-secondary me-1">
                    {tag.name}
                  </span>
                ))}
              </div>
            </div>
            <div className="d-flex gap-2">
              <button className="btn btn-outline-primary btn-sm" onClick={() => handleToggle(task)}>
                Toggle
              </button>
              <button className="btn btn-outline-danger btn-sm" onClick={() => handleDelete(task)}>
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
