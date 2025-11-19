package com.ensolvers.notes.repository;

import com.ensolvers.notes.model.Note;
import com.ensolvers.notes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for accessing and managing Note entities.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Find all notes belonging to a specific user
    List<Note> findByUser(User user);

    // Find notes by user and archive state
    List<Note> findByUserAndArchived(User user, boolean archived);
}
