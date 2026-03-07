import { Link } from "react-router-dom";

export default function NavBar({ auth }) {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" to="/tasks">
          TodoApp
        </Link>
        <div className="collapse navbar-collapse show">
          {auth.isAuthenticated && (
            <ul className="navbar-nav me-auto">
              <li className="nav-item">
                <Link className="nav-link" to="/tasks">
                  Tasks
                </Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/tags">
                  Tags
                </Link>
              </li>
            </ul>
          )}
          <div className="ms-auto d-flex align-items-center gap-3">
            {auth.isAuthenticated ? (
              <>
                <span className="text-light">{auth.username}</span>
                <button className="btn btn-outline-light" onClick={auth.onLogout}>
                  Logout
                </button>
              </>
            ) : (
              <Link className="btn btn-outline-light" to="/login">
                Login
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
