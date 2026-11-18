package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.TaskRequest;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.exception.NotFoundException;
import com.taskflow.taskflow.model.Task;
import com.taskflow.taskflow.model.TaskStatus;
import com.taskflow.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    public TaskService(TaskRepository taskRepository, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByProject(Long projectId) {
        projectService.getOrThrow(projectId);
        return taskRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request) {
        Task task = new Task();
        task.setProject(projectService.getOrThrow(projectId));
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() != null ? request.status() : TaskStatus.TODO);
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getOrThrow(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(Long id) {
        taskRepository.delete(getOrThrow(id));
    }

    private Task getOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada: " + id));
    }
}
