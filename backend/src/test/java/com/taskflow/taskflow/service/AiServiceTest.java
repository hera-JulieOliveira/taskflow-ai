package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.AiTaskRequest;
import com.taskflow.taskflow.dto.ProjectResponse;
import com.taskflow.taskflow.exception.AiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private AiClient client;

    @Mock
    private TaskService taskService;

    @Mock
    private ProjectService projectService;

    private AiService service;

    @BeforeEach
    void setUp() {
        service = new AiService(client, taskService, projectService, 20);
    }

    @Test
    void suggestSubtasksShouldCleanBulletsAndNumbering() {
        when(client.complete(anyString(), anyString(), anyInt()))
                .thenReturn("- Passo um\n2. Passo dois\n\n* Passo três");

        List<String> items = service.suggestSubtasks(USER_ID, new AiTaskRequest("Título", null)).items();

        assertEquals(List.of("Passo um", "Passo dois", "Passo três"), items);
    }

    @Test
    void shouldRejectCallsAboveHourlyLimit() {
        when(client.complete(anyString(), anyString(), anyInt())).thenReturn("ok");
        AiTaskRequest request = new AiTaskRequest("Título", null);

        for (int i = 0; i < 20; i++) {
            service.generateDescription(USER_ID, request);
        }

        AiException ex = assertThrows(AiException.class,
                () -> service.generateDescription(USER_ID, request));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, ex.getStatus());
    }

    @Test
    void summaryOfEmptyProjectShouldNotCallTheModel() {
        when(projectService.findById(USER_ID, 5L))
                .thenReturn(new ProjectResponse(5L, "Vazio", null, LocalDateTime.now()));
        when(taskService.findByProject(USER_ID, 5L)).thenReturn(List.of());

        String text = service.summarizeProject(USER_ID, 5L).text();

        assertEquals("Este projeto ainda não tem tarefas.", text);
        verify(client, never()).complete(anyString(), anyString(), anyInt());
    }
}
