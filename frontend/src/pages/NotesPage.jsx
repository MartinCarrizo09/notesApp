import { useEffect, useState } from "react";
import api from "../services/api";

function NotesPage() {
  const [notes, setNotes] = useState([]);
  const [allNotes, setAllNotes] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);
  const [editingNote, setEditingNote] = useState(null);
  const [editTitle, setEditTitle] = useState("");
  const [editContent, setEditContent] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [editSelectedTags, setEditSelectedTags] = useState([]);
  const [newTagName, setNewTagName] = useState("");
  const [editNewTagName, setEditNewTagName] = useState("");
  const [filterTagId, setFilterTagId] = useState(null);

  const fetchNotes = async () => {
    try {
      setError("");
      const res = await api.get("/notes/active");
      setAllNotes(res.data);
      applyFilter(res.data);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to load notes";
      setError(errorMsg);
      console.error("Error fetching notes:", err);
    }
  };

  const fetchTags = async () => {
    try {
      const res = await api.get("/tags");
      setTags(res.data);
    } catch (err) {
      console.error("Error fetching tags:", err);
    }
  };

  const applyFilter = (notesToFilter) => {
    if (filterTagId === null) {
      setNotes(notesToFilter);
    } else {
      const filtered = notesToFilter.filter(note => 
        note.tags && note.tags.some(tag => tag.id === filterTagId)
      );
      setNotes(filtered);
    }
  };

  const createTag = async (tagName, addToSelection = true, isEditMode = false) => {
    if (!tagName.trim()) return null;
    try {
      const res = await api.post("/tags", { name: tagName.trim() });
      await fetchTags();
      // Agregar automáticamente el nuevo tag a la selección correspondiente
      const newTag = res.data;
      if (newTag && newTag.id && addToSelection) {
        if (isEditMode) {
          setEditSelectedTags([...editSelectedTags, newTag.id]);
        } else {
          setSelectedTags([...selectedTags, newTag.id]);
        }
      }
      return newTag;
    } catch (err) {
      console.error("Error creating tag:", err);
      return null;
    }
  };

  const deleteTag = async (tagId) => {
    if (!confirm("¿Estás seguro de que quieres eliminar este tag? Se eliminará de todas las notas.")) return;
    try {
      await api.delete(`/tags/${tagId}`);
      await fetchTags();
      // Remover el tag de las selecciones si estaba seleccionado
      setSelectedTags(selectedTags.filter(id => id !== tagId));
      setEditSelectedTags(editSelectedTags.filter(id => id !== tagId));
      setSuccess("Tag eliminado correctamente");
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to delete tag";
      setError(errorMsg);
      console.error("Error deleting tag:", err);
    }
  };

  const createNote = async () => {
    if (!title.trim() || !content.trim()) return;
    
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const tagNames = selectedTags.map(tagId => {
        const tag = tags.find(t => t.id === tagId);
        return tag ? tag.name : null;
      }).filter(Boolean);

      // Crear nuevo tag si se ingresó uno (ya se agregó a selectedTags en createTag)
      if (newTagName.trim()) {
        const newTag = await createTag(newTagName, true, false);
        if (newTag) {
          // El tag ya está en selectedTags, solo agregamos el nombre
          tagNames.push(newTag.name);
        }
        setNewTagName("");
      }

      await api.post("/notes/create", { title, content, tags: tagNames });
      setTitle("");
      setContent("");
      setSelectedTags([]);
      setNewTagName("");
      setSuccess("Note created successfully!");
      fetchNotes();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to create note";
      setError(errorMsg);
      console.error("Error creating note:", err);
    } finally {
      setLoading(false);
    }
  };

  const toggleArchive = async (id) => {
    try {
      setError("");
      await api.put(`/notes/${id}/archive`);
      setSuccess("Note archived successfully!");
      fetchNotes();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to archive note";
      setError(errorMsg);
      console.error("Error archiving note:", err);
    }
  };

  const deleteNote = async (id) => {
    if (!confirm("Are you sure you want to delete this note?")) return;
    
    try {
      setError("");
      await api.delete(`/notes/${id}`);
      setSuccess("Note deleted successfully!");
      fetchNotes();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to delete note";
      setError(errorMsg);
      console.error("Error deleting note:", err);
    }
  };

  const startEdit = (note) => {
    setEditingNote(note.id);
    setEditTitle(note.title);
    setEditContent(note.content);
    // Cargar tags de la nota
    const noteTagIds = note.tags ? note.tags.map(tag => tag.id) : [];
    setEditSelectedTags(noteTagIds);
    setEditNewTagName("");
  };

  const cancelEdit = () => {
    setEditingNote(null);
    setEditTitle("");
    setEditContent("");
    setEditSelectedTags([]);
    setEditNewTagName("");
  };

  const saveEdit = async () => {
    if (!editTitle.trim() || !editContent.trim()) return;
    
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const tagNames = editSelectedTags.map(tagId => {
        const tag = tags.find(t => t.id === tagId);
        return tag ? tag.name : null;
      }).filter(Boolean);

      // Crear nuevo tag si se ingresó uno (ya se agregó a editSelectedTags en createTag)
      if (editNewTagName.trim()) {
        const newTag = await createTag(editNewTagName, true, true);
        if (newTag) {
          // El tag ya está en editSelectedTags, solo agregamos el nombre
          tagNames.push(newTag.name);
        }
        setEditNewTagName("");
      }

      await api.put(`/notes/${editingNote}`, {
        title: editTitle,
        content: editContent,
        tags: tagNames
      });
      setSuccess("Note updated successfully!");
      cancelEdit();
      fetchNotes();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.message || "Failed to update note";
      setError(errorMsg);
      console.error("Error updating note:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotes();
    fetchTags();
  }, []);

  useEffect(() => {
    applyFilter(allNotes);
  }, [filterTagId, allNotes]);

  return (
    <div className="notes-container">
      <header className="notes-header">
        <div>
          <h2>Active Notes</h2>
          <p className="page-subtitle">
            {notes.length} active note{notes.length !== 1 ? "s" : ""}
          </p>
        </div>
        <span className="notes-hint">Keep ideas organized and archive what you're done with.</span>
      </header>

      <section className="filter-section">
        <div className="filter-controls">
          <label htmlFor="tag-filter">Filter by Tag:</label>
          <select
            id="tag-filter"
            value={filterTagId || ""}
            onChange={(e) => setFilterTagId(e.target.value ? parseInt(e.target.value) : null)}
            className="tag-filter-select"
          >
            <option value="">All Notes</option>
            {tags.map(tag => (
              <option key={tag.id} value={tag.id}>{tag.name}</option>
            ))}
          </select>
        </div>
      </section>

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

      <section className="create-note-form">
        <div className="form-heading">
          <h3>Create New Note</h3>
          <p>Title and content are required. You can always edit later.</p>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="note-title">Title</label>
            <input
              id="note-title"
              type="text"
              placeholder="e.g. Sprint planning ideas"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label htmlFor="note-content">Content</label>
            <textarea
              id="note-content"
              placeholder="Capture tasks, ideas, or reminders here..."
              value={content}
              onChange={(e) => setContent(e.target.value)}
              rows="4"
            />
          </div>
        </div>
        <div className="form-group">
          <label htmlFor="note-tags">Tags</label>
          <div className="tags-selector">
            <div className="tags-checkboxes">
              {tags.map(tag => (
                <label 
                  key={tag.id} 
                  className={`tag-checkbox ${selectedTags.includes(tag.id) ? 'tag-checked' : ''}`}
                >
                  <input
                    type="checkbox"
                    checked={selectedTags.includes(tag.id)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedTags([...selectedTags, tag.id]);
                      } else {
                        setSelectedTags(selectedTags.filter(id => id !== tag.id));
                      }
                    }}
                  />
                  <span className="tag-checkbox-label">{tag.name}</span>
                  <button
                    type="button"
                    className="tag-delete-btn"
                    onClick={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                      deleteTag(tag.id);
                    }}
                    title="Eliminar tag"
                  >
                    🗑️
                  </button>
                </label>
              ))}
            </div>
            <div className="new-tag-input">
              <input
                type="text"
                placeholder="Create new tag..."
                value={newTagName}
                onChange={(e) => setNewTagName(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    if (newTagName.trim()) {
                      createTag(newTagName, true, false);
                      setNewTagName("");
                    }
                  }
                }}
              />
              {newTagName.trim() && (
                <button
                  type="button"
                  onClick={() => {
                    createTag(newTagName, true, false);
                    setNewTagName("");
                  }}
                  className="btn-tag-add"
                >
                  Add Tag
                </button>
              )}
            </div>
          </div>
        </div>
        <div className="form-actions">
          <button
            onClick={createNote}
            className="btn-primary"
            disabled={loading || !title.trim() || !content.trim()}
          >
            {loading ? "Creating..." : "Create Note"}
          </button>
        </div>
      </section>

      <section className="notes-grid">
        {notes.length === 0 ? (
          <div className="empty-state">
            <p>📝 No active notes yet. Create your first note above!</p>
          </div>
        ) : (
          notes.map((note) => (
            <article key={note.id} className="note-card">
              <header className="note-card-header">
                <h3>{note.title}</h3>
                <span className="note-date">
                  {new Date(note.createdAt).toLocaleDateString()}
                </span>
              </header>
              <p className="note-content">{note.content}</p>
              {note.tags && note.tags.length > 0 && (
                <div className="note-tags">
                  {note.tags.map(tag => (
                    <span key={tag.id} className="tag-badge">{tag.name}</span>
                  ))}
                </div>
              )}
              <footer className="note-actions">
                <button
                  onClick={() => startEdit(note)}
                  className="btn-edit"
                >
                  ✏️ Edit
                </button>
                <button
                  onClick={() => toggleArchive(note.id)}
                  className="btn-secondary"
                >
                  📦 Archive
                </button>
                <button
                  onClick={() => deleteNote(note.id)}
                  className="btn-danger"
                >
                  🗑️ Delete
                </button>
              </footer>
            </article>
          ))
        )}
      </section>

      {editingNote && (
        <div className="modal-overlay" onClick={cancelEdit}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Edit Note</h3>
              <button className="modal-close" onClick={cancelEdit}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label htmlFor="edit-title">Title</label>
                <input
                  id="edit-title"
                  type="text"
                  value={editTitle}
                  onChange={(e) => setEditTitle(e.target.value)}
                />
              </div>
              <div className="form-group">
                <label htmlFor="edit-content">Content</label>
                <textarea
                  id="edit-content"
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows="6"
                />
              </div>
              <div className="form-group">
                <label htmlFor="edit-tags">Tags</label>
                <div className="tags-selector">
                  <div className="tags-checkboxes">
                    {tags.map(tag => (
                      <label 
                        key={tag.id} 
                        className={`tag-checkbox ${editSelectedTags.includes(tag.id) ? 'tag-checked' : ''}`}
                      >
                        <input
                          type="checkbox"
                          checked={editSelectedTags.includes(tag.id)}
                          onChange={(e) => {
                            if (e.target.checked) {
                              setEditSelectedTags([...editSelectedTags, tag.id]);
                            } else {
                              setEditSelectedTags(editSelectedTags.filter(id => id !== tag.id));
                            }
                          }}
                        />
                        <span className="tag-checkbox-label">{tag.name}</span>
                        <button
                          type="button"
                          className="tag-delete-btn"
                          onClick={(e) => {
                            e.preventDefault();
                            e.stopPropagation();
                            deleteTag(tag.id);
                          }}
                          title="Eliminar tag"
                        >
                          🗑️
                        </button>
                      </label>
                    ))}
                  </div>
                  <div className="new-tag-input">
                    <input
                      type="text"
                      placeholder="Create new tag..."
                      value={editNewTagName}
                      onChange={(e) => setEditNewTagName(e.target.value)}
                      onKeyPress={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          if (editNewTagName.trim()) {
                            createTag(editNewTagName, true, true);
                            setEditNewTagName("");
                          }
                        }
                      }}
                    />
                    {editNewTagName.trim() && (
                      <button
                        type="button"
                        onClick={() => {
                          createTag(editNewTagName, true, true);
                          setEditNewTagName("");
                        }}
                        className="btn-tag-add"
                      >
                        Add Tag
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button onClick={cancelEdit} className="btn-secondary">Cancel</button>
              <button
                onClick={saveEdit}
                className="btn-primary"
                disabled={loading || !editTitle.trim() || !editContent.trim()}
              >
                {loading ? "Saving..." : "Save Changes"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default NotesPage;
