package com.taskmanager.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TaskRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task savedTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        Task task = Task.builder()
                .title("Initial Task")
                .description("Initial Description")
                .dueDate(LocalDate.now().plusDays(5))
                .status(TaskStatus.PENDING)
                .build();
        savedTask = taskRepository.save(task);
    }

    @Test
    @DisplayName("GET /api/tasks should return list of tasks")
    void getAllTasks_ShouldReturnTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Initial Task")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return task when exists")
    void getTaskById_WhenExists_ShouldReturnTask() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedTask.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Initial Task")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 404 when not exists")
    void getTaskById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("POST /api/tasks should create a new task")
    void createTask_ShouldCreateTask() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .dueDate(LocalDate.now().plusDays(10))
                .status(TaskStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    @DisplayName("POST /api/tasks should return 400 when validation fails")
    void createTask_WhenInvalidData_ShouldReturn400() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("") // Blank title
                .status(TaskStatus.PENDING)
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Title is required")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} should update existing task")
    void updateTask_ShouldUpdateTask() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .dueDate(LocalDate.now().plusDays(7))
                .status(TaskStatus.COMPLETED)
                .build();

        mockMvc.perform(put("/api/tasks/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} should delete task")
    void deleteTask_ShouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status should update status")
    void updateTaskStatus_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(patch("/api/tasks/{id}/status", savedTask.getId())
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }
}
