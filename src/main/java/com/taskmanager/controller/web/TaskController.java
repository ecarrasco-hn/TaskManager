package com.taskmanager.controller.web;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/")
    public String index() {
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String listTasks(@RequestParam(required = false) TaskStatus status, Model model) {
        if (status != null) {
            model.addAttribute("tasks", taskService.getTasksByStatus(status));
            model.addAttribute("currentStatus", status);
        } else {
            model.addAttribute("tasks", taskService.getAllTasks());
        }
        model.addAttribute("stats", taskService.getTaskStatistics());
        return "tasks/list";
    }

    @GetMapping("/tasks/new")
    public String showCreateForm(Model model) {
        model.addAttribute("taskRequest", new TaskRequest());
        model.addAttribute("statuses", TaskStatus.values());
        return "tasks/form";
    }

    @PostMapping("/tasks/new")
    public String createTask(@Valid @ModelAttribute("taskRequest") TaskRequest request, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            return "tasks/form";
        }
        taskService.createTask(request);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var task = taskService.getTaskById(id);
        TaskRequest request = TaskRequest.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .build();
        model.addAttribute("taskRequest", request);
        model.addAttribute("taskId", id);
        model.addAttribute("statuses", TaskStatus.values());
        return "tasks/form";
    }

    @PostMapping("/tasks/edit/{id}")
    public String updateTask(@PathVariable Long id, 
                             @Valid @ModelAttribute("taskRequest") TaskRequest request, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("taskId", id);
            model.addAttribute("statuses", TaskStatus.values());
            return "tasks/form";
        }
        taskService.updateTask(id, request);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        taskService.updateTaskStatus(id, status);
        return "redirect:/tasks";
    }
}
