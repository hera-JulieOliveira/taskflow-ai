package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.AiFeedbackRequest;
import com.taskflow.taskflow.model.AiFeedback;
import com.taskflow.taskflow.repository.AiFeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiFeedbackServiceTest {

    @Mock
    private AiFeedbackRepository repository;

    @InjectMocks
    private AiFeedbackService service;

    @Test
    void saveShouldStoreVoteFeatureAndUser() {
        service.save(7L, new AiFeedbackRequest("summary", false, "ruim"));

        ArgumentCaptor<AiFeedback> captor = ArgumentCaptor.forClass(AiFeedback.class);
        verify(repository).save(captor.capture());
        AiFeedback saved = captor.getValue();

        assertEquals(7L, saved.getUserId());
        assertEquals("summary", saved.getFeature());
        assertFalse(saved.isHelpful());
        assertEquals("ruim", saved.getComment());
    }
}
