import { useEffect, useState } from "react";
import api from "../services/api";

function ArchivedPage() {
  const [notes, setNotes] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const fetchArchived = async () => {
    try {
      setError("");
      const res = await api.get("/notes/archived");
      setNotes(res.data);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to load archived notes";
      setError(errorMsg);
      console.error("Error fetching archived notes:", err);
    }
  };

  const unarchive = async (id) => {
    try {
      setError("");
      await api.put(`/notes/${id}/archive`);
      setSuccess("Note restored successfully!");
      fetchArchived();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to restore note";
      setError(errorMsg);
      console.error("Error unarchiving note:", err);
    }
  };

  const deleteNote = async (id) => {
    if (!confirm("Are you sure you want to permanently delete this note?")) return;

    try {
      setError("");
      await api.delete(`/notes/${id}`);
      setSuccess("Note deleted permanently!");
      fetchArchived();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to delete note";
      setError(errorMsg);
      console.error("Error deleting note:", err);
    }
  };

  useEffect(() => {
    fetchArchived();
  }, []);

  return (
    <div className="notes-container">
      <header className="notes-header">
        <div>
          <h2>📦 Archived Notes</h2>
          <p className="page-subtitle">
            {notes.length} archived note{notes.length !== 1 ? "s" : ""}
          </p>
        </div>
        <span className="notes-hint">
          Restore notes as soon as you need to work on them again or clean them up forever.
        </span>
      </header>

      {error && (
        <div className="alert alert-error" onClick={() => setError("")}>
          <span>⚠️</span>
          <span>{error}</span>
          <button className="alert-close">×</button>
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          <span>✅</span>
          <span>{success}</span>
        </div>
      )}

      <section className="notes-grid">
        {notes.length === 0 ? (
          <div className="empty-state">
            <p>📦 No archived notes. Archive notes from the active list to see them here.</p>
          </div>
        ) : (
          notes.map((note) => (
            <article key={note.id} className="note-card archived">
              <header className="note-card-header">
                <h3>{note.title}</h3>
                <div className="note-meta">
                  <span className="note-date">
                    {new Date(note.createdAt).toLocaleDateString()}
                  </span>
                  <span className="archived-badge">Archived</span>
                </div>
              </header>
              <p className="note-content">{note.content}</p>
              <footer className="note-actions">
                <button onClick={() => unarchive(note.id)} className="btn-secondary">
                  ↩️ Restore
                </button>
                <button onClick={() => deleteNote(note.id)} className="btn-danger">
                  🗑️ Delete
                </button>
              </footer>
            </article>
          ))
        )}
      </section>
    </div>
  );
}

export default ArchivedPage;
