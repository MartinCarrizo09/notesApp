package com.ensolvers.notes.controller;

import com.ensolvers.notes.config.jwt.JwtUtil;
import com.ensolvers.notes.model.Note;
import com.ensolvers.notes.model.User;
import com.ensolvers.notes.service.NoteService;
import com.ensolvers.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Handles note-related REST endpoints.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public NoteController(NoteService noteService, UserService userService, JwtUtil jwtUtil) {
        this.noteService = noteService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private User getAuthenticatedUser(String token) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return user.get();
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveNotes(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            User user = getAuthenticatedUser(actualToken);
            List<Note> notes = noteService.getActiveNotes(user);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/archived")
    public ResponseEntity<?> getArchivedNotes(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            User user = getAuthenticatedUser(actualToken);
            List<Note> notes = noteService.getArchivedNotes(user);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, Object> payload) {
        try {
            String actualToken = token.replace("Bearer ", "");
            User user = getAuthenticatedUser(actualToken);

            String title = (String) payload.get("title");
            String content = (String) payload.get("content");
            List<String> tagNames = (List<String>) payload.get("tags");

            Note note = noteService.createNote(user, title, content, tagNames);
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(@RequestHeader("Authorization") String token,
                                      @PathVariable Long noteId,
                                      @RequestBody Map<String, Object> payload) {
        try {
            String actualToken = token.replace("Bearer ", "");
            User user = getAuthenticatedUser(actualToken);

            String title = (String) payload.get("title");
            String content = (String) payload.get("content");
            List<String> tagNames = (List<String>) payload.get("tags");

            Note note = noteService.updateNote(noteId, user, title, content, tagNames);
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{noteId}/archive")
    public ResponseEntity<?> toggleArchiveStatus(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long noteId) {
        try {
            String actualToken = token.replace("Bearer ", "");
            User user = getAuthenticatedUser(actualToken);
            Note note = noteService.toggleArchiveStatus(noteId, user);
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@RequestHeader("Authorization") String token,
                                        @PathVariable Long noteId) {
        try {
            String actualToken = token.replace("Bearer ", "");
            User user = getAuthenticatedUser(actualToken);
            noteService.deleteNoteById(noteId, user);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Note deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
