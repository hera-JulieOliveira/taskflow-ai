package com.taskflow.taskflow.controller;

import com.taskflow.taskflow.config.SecurityConfig;
import com.taskflow.taskflow.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService service;

    @Test
    void malformedJsonShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(jwt().jwt(j -> j.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{nome sem aspas"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição inválido"));
    }

    @Test
    void nonNumericIdShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/projects/abc")
                        .with(jwt().jwt(j -> j.subject("1"))))
                .andExpect(status().isBadRequest());
    }
}
