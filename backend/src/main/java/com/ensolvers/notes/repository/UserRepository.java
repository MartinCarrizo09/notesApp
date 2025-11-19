package com.ensolvers.notes.repository;

import com.ensolvers.notes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for accessing and managing User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom finder to support authentication
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
