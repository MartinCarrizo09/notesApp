import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useState, useEffect } from "react";
import Navbar from "./components/Navbar";
import LoginPage from "./pages/LoginPage";
import NotesPage from "./pages/NotesPage";
import ArchivedPage from "./pages/ArchivedPage";

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem("token"));

  // Escuchar cambios en el localStorage
  useEffect(() => {
    const checkAuth = () => {
      setIsAuthenticated(!!localStorage.getItem("token"));
    };
    
    window.addEventListener("storage", checkAuth);
    return () => window.removeEventListener("storage", checkAuth);
  }, []);

  return (
    <BrowserRouter>
      {isAuthenticated && <Navbar />}
      <Routes>
        <Route
          path="/login"
          element={isAuthenticated ? <Navigate to="/notes" /> : <LoginPage />}
        />
        <Route
          path="/notes"
          element={isAuthenticated ? <NotesPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/archived"
          element={isAuthenticated ? <ArchivedPage /> : <Navigate to="/login" />}
        />
        <Route path="*" element={<Navigate to={isAuthenticated ? "/notes" : "/login"} />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
