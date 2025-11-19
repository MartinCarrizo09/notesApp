package com.ensolvers.notes.service;

import com.ensolvers.notes.model.Tag;
import com.ensolvers.notes.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Handles CRUD operations for tags.
 */
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag createTag(String name) {
        String tagName = Objects.requireNonNull(name, "Tag name is required");
        Tag existing = tagRepository.findByName(tagName).orElse(null);
        if (existing != null) {
            return existing;
        }
        Tag newTag = Tag.builder().name(tagName).build();
        Tag persisted = tagRepository.save(Objects.requireNonNull(newTag));
        return persisted;
    }

    public void deleteTag(Long id) {
        Long tagId = Objects.requireNonNull(id, "Tag id is required");
        if (!tagRepository.existsById(tagId)) {
            throw new IllegalArgumentException("Tag not found");
        }
        try {
            tagRepository.deleteById(tagId);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede borrar el tag ya que tiene notas asociadas");
        }
    }
}
