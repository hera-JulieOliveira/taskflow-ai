package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.AiFeedbackRequest;
import com.taskflow.taskflow.model.AiFeedback;
import com.taskflow.taskflow.repository.AiFeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiFeedbackService {

    private final AiFeedbackRepository repository;

    public AiFeedbackService(AiFeedbackRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(Long userId, AiFeedbackRequest request) {
        AiFeedback feedback = new AiFeedback();
        feedback.setUserId(userId);
        feedback.setFeature(request.feature());
        feedback.setHelpful(request.helpful());
        feedback.setComment(request.comment());
        repository.save(feedback);
    }
}
