import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    
    try {
      const res = await api.post("/auth/login", { username, password });
      localStorage.setItem("token", res.data.token);
      // Forzar recarga de la página para actualizar el estado de autenticación
      window.location.href = "/notes";
    } catch (err) {
      setError(err.response?.data?.message || "Invalid credentials");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-logo">
          <div className="logo-icon">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="6" y="8" width="28" height="32" rx="3" fill="url(#gradient1)"/>
              <rect x="6" y="8" width="28" height="32" rx="3" fill="url(#gradient2)" opacity="0.8"/>
              <path d="M14 18h12M14 24h12M14 30h8" stroke="white" strokeWidth="2" strokeLinecap="round"/>
              <defs>
                <linearGradient id="gradient1" x1="0" y1="0" x2="40" y2="40">
                  <stop offset="0%" stopColor="#667eea"/>
                  <stop offset="100%" stopColor="#764ba2"/>
                </linearGradient>
                <linearGradient id="gradient2" x1="0" y1="0" x2="40" y2="40">
                  <stop offset="0%" stopColor="#764ba2"/>
                  <stop offset="100%" stopColor="#667eea"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>Notes App</h2>
        </div>
        <p className="login-subtitle">Sign in to manage your notes</p>
        
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>
        
        {error && <p className="error-message">{error}</p>}
        
        <div className="login-hint">
          <p>💡 Default credentials: <strong>admin / admin123</strong></p>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
