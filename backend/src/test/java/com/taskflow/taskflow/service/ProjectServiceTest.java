package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.ProjectRequest;
import com.taskflow.taskflow.dto.ProjectResponse;
import com.taskflow.taskflow.exception.NotFoundException;
import com.taskflow.taskflow.model.Project;
import com.taskflow.taskflow.repository.ProjectRepository;
import com.taskflow.taskflow.repository.UserRepository;
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

    private static final Long USER_ID = 1L;

    @Mock
    private ProjectRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService service;

    @Test
    void createShouldSaveAndReturnProject() {
        when(repository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectResponse response = service.create(USER_ID, new ProjectRequest("Projeto A", "Descrição"));

        assertEquals("Projeto A", response.name());
        assertEquals("Descrição", response.description());
    }

    @Test
    void findByIdShouldThrowWhenNotFoundForThisUser() {
        when(repository.findByIdAndOwnerId(99L, USER_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.findById(USER_ID, 99L));

        assertEquals("Projeto não encontrado: 99", ex.getMessage());
    }

    @Test
    void deleteShouldNotCallRepositoryWhenNotFound() {
        when(repository.findByIdAndOwnerId(99L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(USER_ID, 99L));

        verify(repository, never()).delete(any(Project.class));
    }
}
