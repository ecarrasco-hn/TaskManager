package com.taskmanager.service;

import com.taskmanager.dto.TaskDTO;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.TaskStatus;

import java.util.List;
import java.util.Map;

public interface TaskService {
    List<TaskDTO> getAllTasks();
    List<TaskDTO> getTasksByStatus(TaskStatus status);
    TaskDTO getTaskById(Long id);
    TaskDTO createTask(TaskRequest request);
    TaskDTO updateTask(Long id, TaskRequest request);
    void deleteTask(Long id);
    TaskDTO updateTaskStatus(Long id, TaskStatus status);
    Map<String, Long> getTaskStatistics();
}
