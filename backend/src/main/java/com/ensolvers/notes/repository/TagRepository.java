package com.ensolvers.notes.repository;

import com.ensolvers.notes.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for accessing and managing Tag entities.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Find a tag by its name (to prevent duplicates)
    Optional<Tag> findByName(String name);

    boolean existsByName(String name);
}
