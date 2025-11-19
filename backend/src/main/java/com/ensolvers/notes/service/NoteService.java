package com.ensolvers.notes.service;

import com.ensolvers.notes.model.Note;
import com.ensolvers.notes.model.Tag;
import com.ensolvers.notes.model.User;
import com.ensolvers.notes.repository.NoteRepository;
import com.ensolvers.notes.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles operations related to notes.
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    public List<Note> getActiveNotes(User user) {
        return noteRepository.findByUserAndArchived(user, false);
    }

    public List<Note> getArchivedNotes(User user) {
        return noteRepository.findByUserAndArchived(user, true);
    }

    public Note createNote(User user, String title, String content, List<String> tagNames) {
        Note note = new Note();
        note.setUser(user);
        note.setTitle(title);
        note.setContent(content);

        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(null, tagName, new HashSet<>())));
                tags.add(tag);
            }
        }
        note.setTags(tags);

        return noteRepository.save(note);
    }

    public Note toggleArchiveStatus(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        note.setArchived(!note.isArchived());
        return noteRepository.save(note);
    }

    public Note updateNote(Long noteId, User user, String title, String content, List<String> tagNames) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        note.setTitle(title);
        note.setContent(content);
        
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(null, tagName, new HashSet<>())));
                tags.add(tag);
            }
        }
        note.setTags(tags);
        
        return noteRepository.save(note);
    }

    public void deleteNoteById(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        noteRepository.deleteById(noteId);
    }
}
