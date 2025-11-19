package com.ensolvers.notes.controller;

import com.ensolvers.notes.model.Tag;
import com.ensolvers.notes.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles tag-related REST endpoints.
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTags() {
        try {
            List<Tag> tags = tagService.findAll();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            if (name == null || name.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Tag name is required");
                return ResponseEntity.badRequest().body(error);
            }

            Tag tag = tagService.createTag(name);
            return ResponseEntity.ok(tag);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        try {
            tagService.deleteTag(tagId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Tag deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
