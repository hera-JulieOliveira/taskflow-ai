package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.ProjectRequest;
import com.taskflow.taskflow.dto.ProjectResponse;
import com.taskflow.taskflow.exception.NotFoundException;
import com.taskflow.taskflow.model.Project;
import com.taskflow.taskflow.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @InjectMocks
    private ProjectService service;

    @Test
    void createShouldSaveAndReturnProject() {
        when(repository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectResponse response = service.create(new ProjectRequest("Projeto A", "Descrição"));

        assertEquals("Projeto A", response.name());
        assertEquals("Descrição", response.description());
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.findById(99L));

        assertEquals("Projeto não encontrado: 99", ex.getMessage());
    }

    @Test
    void deleteShouldNotCallRepositoryWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(99L));

        verify(repository, never()).delete(any(Project.class));
    }
}
