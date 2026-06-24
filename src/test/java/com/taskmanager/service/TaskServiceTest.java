package com.taskmanager.service;

import com.taskmanager.dto.TaskDTO;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(1))
                .status(TaskStatus.PENDING)
                .build();

        taskRequest = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(1))
                .status(TaskStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllTasks() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        // Act
        List<TaskDTO> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleTask.getTitle(), result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("Should return task by id when it exists")
    void getTaskById_Found() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // Act
        TaskDTO result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(sampleTask.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Should throw exception when task by id not found")
    void getTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    @DisplayName("Should create a new task")
    void createTask() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        TaskDTO result = taskService.createTask(taskRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleTask.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update existing task")
    void updateTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        TaskDTO result = taskService.updateTask(1L, taskRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleTask.getTitle(), result.getTitle());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task when it exists")
    void deleteTask_Success() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void deleteTask_NotFound() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository).existsById(1L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update task status")
    void updateTaskStatus() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskDTO result = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return correct task statistics")
    void getTaskStatistics() {
        // Arrange
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countByStatus(TaskStatus.PENDING)).thenReturn(5L);
        when(taskRepository.countByStatus(TaskStatus.IN_PROGRESS)).thenReturn(3L);
        when(taskRepository.countByStatus(TaskStatus.COMPLETED)).thenReturn(2L);

        // Act
        Map<String, Long> stats = taskService.getTaskStatistics();

        // Assert
        assertEquals(10L, stats.get("total"));
        assertEquals(5L, stats.get("pending"));
        assertEquals(3L, stats.get("inProgress"));
        assertEquals(2L, stats.get("completed"));
        verify(taskRepository).count();
        verify(taskRepository, times(3)).countByStatus(any(TaskStatus.class));
    }
}
