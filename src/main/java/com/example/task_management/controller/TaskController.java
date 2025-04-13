package com.example.task_management.controller;

import com.example.task_management.dto.TaskDto;
import com.example.task_management.model.Task;
import com.example.task_management.model.User;
import com.example.task_management.repository.TaskRepository;
import com.example.task_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(Task.TaskStatus.valueOf(taskDto.getStatus()));
        task.setCreatedBy(user);

        Task savedTask = taskRepository.save(task);

        return new ResponseEntity<>(convertToDto(savedTask), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Task> tasks;

        // If ADMIN, get all tasks, otherwise get only the user's tasks
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByCreatedBy(user);
        }

        List<TaskDto> taskDtos = tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Verify user has permission (is admin or owner)
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = task.getCreatedBy().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Update task
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(Task.TaskStatus.valueOf(taskDto.getStatus()));

        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(convertToDto(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Verify user has permission (is admin or owner)
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = task.getCreatedBy().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        taskRepository.delete(task);

        return ResponseEntity.noContent().build();
    }

    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        dto.setCreatedBy(task.getCreatedBy().getUsername());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}