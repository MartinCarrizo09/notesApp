package com.ensolvers.notes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tag that can be associated with multiple notes.
 */
@Entity
@Table(name = "tags")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Many-to-many relation with notes
    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    @JsonIgnore
    private Set<Note> notes = new HashSet<>();
}
