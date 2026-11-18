package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.ProjectRequest;
import com.taskflow.taskflow.dto.ProjectResponse;
import com.taskflow.taskflow.exception.NotFoundException;
import com.taskflow.taskflow.model.Project;
import com.taskflow.taskflow.repository.ProjectRepository;
import com.taskflow.taskflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll(Long userId) {
        return repository.findByOwnerIdOrderByCreatedAtAsc(userId)
                .stream().map(ProjectResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long userId, Long id) {
        return ProjectResponse.from(getOrThrow(userId, id));
    }

    @Transactional
    public ProjectResponse create(Long userId, ProjectRequest request) {
        Project project = new Project();
        project.setOwner(userRepository.getReferenceById(userId));
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(repository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long userId, Long id, ProjectRequest request) {
        Project project = getOrThrow(userId, id);
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(project);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        repository.delete(getOrThrow(userId, id));
    }

    public Project getOrThrow(Long userId, Long id) {
        return repository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado: " + id));
    }
}
