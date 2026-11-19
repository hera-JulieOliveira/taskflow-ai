package com.taskflow.taskflow.controller;

import com.taskflow.taskflow.dto.AiListResponse;
import com.taskflow.taskflow.dto.AiTaskRequest;
import com.taskflow.taskflow.dto.AiTextResponse;
import com.taskflow.taskflow.service.AiService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService service;

    public AiController(AiService service) {
        this.service = service;
    }

    @PostMapping("/description")
    public AiTextResponse description(@AuthenticationPrincipal Jwt jwt,
                                      @Valid @RequestBody AiTaskRequest request) {
        return service.generateDescription(userId(jwt), request);
    }

    @PostMapping("/subtasks")
    public AiListResponse subtasks(@AuthenticationPrincipal Jwt jwt,
                                   @Valid @RequestBody AiTaskRequest request) {
        return service.suggestSubtasks(userId(jwt), request);
    }

    @GetMapping("/projects/{projectId}/summary")
    public AiTextResponse summary(@AuthenticationPrincipal Jwt jwt, @PathVariable Long projectId) {
        return service.summarizeProject(userId(jwt), projectId);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
