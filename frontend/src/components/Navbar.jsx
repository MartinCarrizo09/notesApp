import { useState } from "react";
import { Link } from "react-router-dom";

function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  return (
    <nav className="navbar">
      <div className="nav-brand">
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
        <span className="logo-text">Notes App</span>
      </div>
      <button 
        className="hamburger-btn" 
        onClick={toggleMenu}
        aria-label="Toggle menu"
        aria-expanded={isMenuOpen}
      >
        <span className={`hamburger-line ${isMenuOpen ? 'open' : ''}`}></span>
        <span className={`hamburger-line ${isMenuOpen ? 'open' : ''}`}></span>
        <span className={`hamburger-line ${isMenuOpen ? 'open' : ''}`}></span>
      </button>
      <div className={`nav-links ${isMenuOpen ? 'open' : ''}`}>
        <Link to="/notes" className="nav-link" onClick={closeMenu}>Active Notes</Link>
        <Link to="/archived" className="nav-link" onClick={closeMenu}>Archived</Link>
        <button onClick={handleLogout} className="btn-logout">Logout</button>
      </div>
      {isMenuOpen && <div className="menu-overlay" onClick={closeMenu}></div>}
    </nav>
  );
}

export default Navbar;
