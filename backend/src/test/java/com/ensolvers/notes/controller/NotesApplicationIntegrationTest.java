package com.ensolvers.notes.controller;

import com.ensolvers.notes.config.jwt.JwtUtil;
import com.ensolvers.notes.dto.AuthRequest;
import com.ensolvers.notes.model.User;
import com.ensolvers.notes.repository.NoteRepository;
import com.ensolvers.notes.repository.TagRepository;
import com.ensolvers.notes.repository.UserRepository;
import com.ensolvers.notes.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotesApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String testUserToken;
    private User testUser;

    @BeforeEach
    public void setup() {
        noteRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userService.registerUser("testuser", "password123");
        testUserToken = jwtUtil.generateToken("testuser");
    }

    // ==================== AUTH TESTS (1-10) ====================

    @Test
    public void test_001_register_new_user_should_return_token() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("newuser")
                .password("newpass123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    public void test_002_register_with_empty_username_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test_003_register_with_empty_password_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("newuser")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test_004_register_duplicate_username_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("testuser")
                .password("newpass123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_005_login_with_valid_credentials_should_return_token() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    public void test_006_login_with_invalid_password_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void test_007_login_with_nonexistent_user_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void test_008_login_with_empty_username_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test_009_login_with_empty_password_should_fail() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("testuser")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test_010_password_should_be_hashed() {
        Optional<User> user = userRepository.findByUsername("testuser");
        assertTrue(user.isPresent());
        assertNotEquals("password123", user.get().getPassword());
        assertTrue(passwordEncoder.matches("password123", user.get().getPassword()));
    }

    // ==================== NOTES TESTS (11-30) ====================

    @Test
    public void test_011_get_active_notes_without_token_should_fail() throws Exception {
        mockMvc.perform(get("/api/notes/active"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_012_get_active_notes_with_token_should_return_empty_list() throws Exception {
        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test_013_create_note_should_return_created_note() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Test Note");
        payload.put("content", "Test Content");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Note"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    public void test_014_create_note_without_token_should_fail() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Test Note");
        payload.put("content", "Test Content");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_015_get_active_notes_after_creation_should_return_one() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Active Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));

        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void test_016_get_archived_notes_should_return_empty() throws Exception {
        mockMvc.perform(get("/api/notes/archived")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test_017_archive_note_should_change_status() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Note to Archive");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        MvcResult createResult = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        Long noteId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(put("/api/notes/" + noteId + "/archive")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }

    @Test
    public void test_018_archived_note_appears_in_archived_list() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Archived Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        MvcResult createResult = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        Long noteId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(put("/api/notes/" + noteId + "/archive")
                .header("Authorization", "Bearer " + testUserToken));

        mockMvc.perform(get("/api/notes/archived")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void test_019_delete_note_should_remove_it() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Note to Delete");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        MvcResult createResult = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        Long noteId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(delete("/api/notes/" + noteId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test_020_create_note_with_tags() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Tagged Note");
        payload.put("content", "Content");
        payload.put("tags", List.of("urgent", "work"));

        MvcResult result = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        int tagCount = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("tags").size();
        assertEquals(2, tagCount);
    }

    @Test
    public void test_021_toggle_archive_twice_returns_to_active() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Toggle Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        MvcResult createResult = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        Long noteId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(put("/api/notes/" + noteId + "/archive")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$.archived").value(true));

        mockMvc.perform(put("/api/notes/" + noteId + "/archive")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    public void test_022_multiple_users_notes_are_isolated() throws Exception {
        User anotherUser = userService.registerUser("another", "pass123");
        String anotherToken = jwtUtil.generateToken("another");

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "User1 Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));

        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + anotherToken))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test_023_create_multiple_notes_lists_all() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", "Note " + i);
            payload.put("content", "Content " + i);
            payload.put("tags", List.of());

            mockMvc.perform(post("/api/notes/create")
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));
        }

        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void test_024_archive_one_of_three_notes_correct_counts() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", "Note " + i);
            payload.put("content", "Content " + i);
            payload.put("tags", List.of());

            mockMvc.perform(post("/api/notes/create")
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));
        }

        MvcResult activeResult = mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andReturn();

        Long firstNoteId = objectMapper.readTree(activeResult.getResponse().getContentAsString())
                .get(0).get("id").asLong();

        mockMvc.perform(put("/api/notes/" + firstNoteId + "/archive")
                .header("Authorization", "Bearer " + testUserToken));

        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/notes/archived")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void test_025_note_has_creation_timestamp() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Timestamped Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        MvcResult result = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("createdAt"));
    }

    @Test
    public void test_026_create_note_with_long_content() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Long Note");
        payload.put("content", "Content ".repeat(100));
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_027_create_note_with_many_tags() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Many Tags");
        payload.put("content", "Content");
        payload.put("tags", List.of("tag1", "tag2", "tag3", "tag4", "tag5"));

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_028_delete_note_without_token_fails() throws Exception {
        mockMvc.perform(delete("/api/notes/999"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_029_archive_without_token_fails() throws Exception {
        mockMvc.perform(put("/api/notes/999/archive"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_030_note_content_with_special_chars() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Special Chars");
        payload.put("content", "Content with @#$%^&*()");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    // ==================== TAGS TESTS (31-40) ====================

    @Test
    public void test_031_get_all_tags_returns_empty_initially() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test_032_create_tag_returns_created_tag() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "urgent");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("urgent"));
    }

    @Test
    public void test_033_create_tag_with_empty_name_fails() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_034_create_duplicate_tag_returns_existing() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "work");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("work"));
    }

    @Test
    public void test_035_get_all_tags_after_creation() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "personal");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void test_036_create_multiple_tags() throws Exception {
        String[] tags = {"urgent", "work", "personal", "later"};

        for (String tag : tags) {
            Map<String, String> payload = new HashMap<>();
            payload.put("name", tag);

            mockMvc.perform(post("/api/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));
        }

        mockMvc.perform(get("/api/tags"))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void test_037_delete_tag() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "deleteme");

        MvcResult createResult = mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        Long tagId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(delete("/api/tags/" + tagId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tags"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test_038_delete_nonexistent_tag_fails() throws Exception {
        mockMvc.perform(delete("/api/tags/9999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_039_note_creates_tags_automatically() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Note");
        payload.put("content", "Content");
        payload.put("tags", List.of("newtag1", "newtag2"));

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));

        mockMvc.perform(get("/api/tags"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void test_040_reusing_tags_no_duplication() throws Exception {
        Map<String, Object> note1 = new HashMap<>();
        note1.put("title", "Note1");
        note1.put("content", "Content");
        note1.put("tags", List.of("shared"));

        Map<String, Object> note2 = new HashMap<>();
        note2.put("title", "Note2");
        note2.put("content", "Content");
        note2.put("tags", List.of("shared"));

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(note1)));

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(note2)));

        mockMvc.perform(get("/api/tags"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ==================== ADDITIONAL TESTS (41-50) ====================

    @Test
    public void test_041_json_response_has_correct_content_type() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void test_042_register_and_login_flow() throws Exception {
        AuthRequest registerReq = AuthRequest.builder()
                .username("flowuser")
                .password("flowpass123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        AuthRequest loginReq = AuthRequest.builder()
                .username("flowuser")
                .password("flowpass123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    public void test_043_concurrent_note_creation() throws Exception {
        for (int i = 0; i < 5; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", "Concurrent " + i);
            payload.put("content", "Content " + i);
            payload.put("tags", List.of());

            mockMvc.perform(post("/api/notes/create")
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));
        }

        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void test_044_missing_authorization_header_fails() throws Exception {
        mockMvc.perform(get("/api/notes/active"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_045_malformed_authorization_header_fails() throws Exception {
        mockMvc.perform(get("/api/notes/active")
                .header("Authorization", "InvalidFormat token"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_046_note_title_persistence() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Persistent Title");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(jsonPath("$.title").value("Persistent Title"));
    }

    @Test
    public void test_047_note_archived_status_defaults_to_false() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "New Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    public void test_048_tag_name_case_sensitivity() throws Exception {
        Map<String, String> tag1 = new HashMap<>();
        tag1.put("name", "Work");

        Map<String, String> tag2 = new HashMap<>();
        tag2.put("name", "work");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag1)));

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag2)));

        mockMvc.perform(get("/api/tags"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void test_049_empty_tags_list_on_note_creation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "No Tags Note");
        payload.put("content", "Content");
        payload.put("tags", List.of());

        MvcResult result = mockMvc.perform(post("/api/notes/create")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        int tagCount = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("tags").size();
        assertEquals(0, tagCount);
    }

    @Test
    public void test_050_get_archived_notes_without_token_fails() throws Exception {
        mockMvc.perform(get("/api/notes/archived"))
                .andExpect(status().isForbidden());
    }
}
