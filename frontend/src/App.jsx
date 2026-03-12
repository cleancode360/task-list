import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useCallback, useEffect, useMemo, useState } from "react";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import TaskListPage from "./pages/TaskListPage.jsx";
import TaskDetailPage from "./pages/TaskDetailPage.jsx";
import TagPage from "./pages/TagPage.jsx";
import NavBar from "./components/NavBar.jsx";
import { auth } from "./api/client.js";

export default function App() {
  const [username, setUsername] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    auth.me().then((data) => {
      if (data?.username) {
        setUsername(data.username);
      }
      setLoading(false);
    });
  }, []);

  const isAuthenticated = !!username;

  const handleLogout = useCallback(async () => {
    try {
      await auth.logout();
    } catch {
      // ignore
    }
    setUsername(null);
  }, []);

  const handleLoginSuccess = useCallback((name) => {
    setUsername(name);
  }, []);

  const authContext = useMemo(
    () => ({
      isAuthenticated,
      username,
      onLogout: handleLogout,
    }),
    [isAuthenticated, username, handleLogout]
  );

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <NavBar auth={authContext} />
      <div className="container py-4">
        <Routes>
          <Route
            path="/"
            element={
              isAuthenticated ? <Navigate to="/tasks" replace /> : <Navigate to="/login" replace />
            }
          />
          <Route
            path="/login"
            element={
              isAuthenticated
                ? <Navigate to="/tasks" replace />
                : <LoginPage onLoginSuccess={handleLoginSuccess} />
            }
          />
          <Route
            path="/register"
            element={
              isAuthenticated ? <Navigate to="/tasks" replace /> : <RegisterPage />
            }
          />
          <Route
            path="/tasks"
            element={isAuthenticated ? <TaskListPage /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/tasks/:id"
            element={isAuthenticated ? <TaskDetailPage /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/tags"
            element={isAuthenticated ? <TagPage /> : <Navigate to="/login" replace />}
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}
