export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const getAuth = () => {
  const raw = localStorage.getItem("todoAuth");
  return raw ? JSON.parse(raw) : null;
};

export const setAuth = (username, password) => {
  localStorage.setItem("todoAuth", JSON.stringify({ username, password }));
};

export const clearAuth = () => {
  localStorage.removeItem("todoAuth");
};

const authHeader = () => {
  const auth = getAuth();
  if (!auth) {
    return {};
  }
  const token = btoa(`${auth.username}:${auth.password}`);
  return { Authorization: `Basic ${token}` };
};

export const apiFetch = async (path, options = {}) => {
  const url = path.startsWith("http") ? path : `${API_BASE_URL}${path}`;
  const response = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...authHeader(),
      ...(options.headers || {}),
    },
  });

  if (response.status === 204) {
    return null;
  }

  const data = await response.json();
  if (!response.ok) {
    const message = data?.message || "Request failed";
    throw new Error(message);
  }
  return data;
};
