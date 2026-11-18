package com.taskflow.taskflow.controller;

import com.taskflow.taskflow.dto.TaskRequest;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> list(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId) {
        return service.findByProject(userId(jwt), projectId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId,
                               @Valid @RequestBody TaskRequest request) {
        return service.create(userId(jwt), projectId, request);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                               @Valid @RequestBody TaskRequest request) {
        return service.update(userId(jwt), id, request);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.delete(userId(jwt), id);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
