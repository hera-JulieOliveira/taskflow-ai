package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.TaskRequest;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.exception.NotFoundException;
import com.taskflow.taskflow.model.Project;
import com.taskflow.taskflow.model.Task;
import com.taskflow.taskflow.model.TaskStatus;
import com.taskflow.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService service;

    private Project project() {
        Project project = new Project();
        project.setId(5L);
        project.setName("Projeto");
        return project;
    }

    @Test
    void createShouldDefaultStatusToTodo() {
        when(projectService.getOrThrow(USER_ID, 5L)).thenReturn(project());
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.create(USER_ID, 5L, new TaskRequest("Tarefa", null, null));

        assertEquals(TaskStatus.TODO, response.status());
        assertEquals(5L, response.projectId());
    }

    @Test
    void updateShouldKeepStatusWhenRequestHasNone() {
        Task task = new Task();
        task.setId(10L);
        task.setTitle("Antigo");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setProject(project());
        when(taskRepository.findByIdAndProjectOwnerId(10L, USER_ID)).thenReturn(Optional.of(task));

        TaskResponse response = service.update(USER_ID, 10L, new TaskRequest("Novo", "desc", null));

        assertEquals("Novo", response.title());
        assertEquals(TaskStatus.IN_PROGRESS, response.status());
    }

    @Test
    void deleteShouldNotCallRepositoryWhenTaskBelongsToSomeoneElse() {
        when(taskRepository.findByIdAndProjectOwnerId(10L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(USER_ID, 10L));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void findByProjectShouldNotQueryTasksWhenProjectIsNotAccessible() {
        when(projectService.getOrThrow(USER_ID, 99L))
                .thenThrow(new NotFoundException("Projeto não encontrado: 99"));

        assertThrows(NotFoundException.class, () -> service.findByProject(USER_ID, 99L));

        verify(taskRepository, never()).findByProjectIdOrderByCreatedAtAsc(anyLong());
    }
}
