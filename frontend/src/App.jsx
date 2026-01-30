import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useMemo, useState } from "react";
import LoginPage from "./pages/LoginPage.jsx";
import TaskListPage from "./pages/TaskListPage.jsx";
import TaskDetailPage from "./pages/TaskDetailPage.jsx";
import TagPage from "./pages/TagPage.jsx";
import NavBar from "./components/NavBar.jsx";
import { clearAuth } from "./api/client.js";

const getStoredAuth = () => {
  const raw = localStorage.getItem("todoAuth");
  return !!raw;
};

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(getStoredAuth());

  const authContext = useMemo(
    () => ({
      isAuthenticated,
      onLogout: () => {
        clearAuth();
        setIsAuthenticated(false);
      },
      onLoginSuccess: () => setIsAuthenticated(true),
    }),
    [isAuthenticated]
  );

  return (
    <BrowserRouter>
      <NavBar auth={authContext} />
      <div className="container py-4">
        <Routes>
          <Route
            path="/"
            element={
              isAuthenticated ? <Navigate to="/tasks" replace /> : <LoginPage auth={authContext} />
            }
          />
          <Route
            path="/login"
            element={<LoginPage auth={authContext} />}
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
