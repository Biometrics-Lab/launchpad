package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.security.exceptions.BadRequestException;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.security.exceptions.PersistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService Unit Tests")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private AssessmentMetricRepository assessmentMetricRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session1Input;
    private Session session1;
    private Session session2;

    @BeforeEach
    void setUp() {
        session1Input = Session.builder()
                .assessmentId(1)
                .build();

        session1 = Session.builder()
                .id(1)
                .assessmentId(1)
                .build();

        session2 = Session.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessmentMetricRepository.findAllByAssessmentId(1))
                .thenReturn(List.of(AssessmentMetric.builder().id(1).build()));
            when(sessionRepository.save(session1Input)).thenReturn(session1);

            Session result = sessionService.create(session1Input);

            assertThat(result).isSameAs(session1);
            verify(sessionRepository).save(session1Input);
        }

        @Test
        @DisplayName("should throw BadRequestException when assessment has no metrics")
        void create_throws_BadRequestException_whenNoMetrics() {
            when(assessmentMetricRepository.findAllByAssessmentId(1)).thenReturn(List.of());

            assertThrows(BadRequestException.class, () -> sessionService.create(session1Input));
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessmentMetricRepository.findAllByAssessmentId(1))
                .thenReturn(List.of(AssessmentMetric.builder().id(1).build()));
            when(sessionRepository.save(session1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> sessionService.create(session1Input));
            assertTrue(ex.getMessage().contains("db down"));
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all sessions from repository")
        void findAll_returns_list_from_repo() {
            when(sessionRepository.findAll()).thenReturn(List.of(session1, session2));

            List<Session> all = sessionService.findAll();

            assertThat(all).containsExactly(session1, session2);
            verify(sessionRepository).findAll();
            verifyNoMoreInteractions(sessionRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return session when found")
        void findById_ok_returns_optional() {
            when(sessionRepository.findById(1)).thenReturn(Optional.of(session1));

            Optional<Session> result = sessionService.findById(1);

            assertThat(result).contains(session1);
            verify(sessionRepository).findById(1);
            verifyNoMoreInteractions(sessionRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(sessionRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> sessionService.findById(1));
            verifyNoMoreInteractions(sessionRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete session when it exists")
        void deleteById_when_exists_deletes() {
            when(sessionRepository.existsById(1)).thenReturn(true);

            sessionService.deleteById(1);

            verify(sessionRepository).existsById(1);
            verify(sessionRepository).deleteById(1);
            verifyNoMoreInteractions(sessionRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(sessionRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sessionService.deleteById(1));
            verify(sessionRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(sessionRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(sessionRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(sessionRepository).deleteById(1);

            assertThrows(PersistException.class, () -> sessionService.deleteById(1));
            verifyNoMoreInteractions(sessionRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return session when it exists")
        void update_when_exists_saves_and_returns() {
            when(sessionRepository.existsById(1)).thenReturn(true);
            when(sessionRepository.save(session1)).thenReturn(session1);

            Session result = sessionService.update(session1);

            assertEquals(1, result.getId().intValue());
            verify(sessionRepository).existsById(1);
            verify(sessionRepository).save(session1);
            verifyNoMoreInteractions(sessionRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(sessionRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sessionService.update(session1));
            verify(sessionRepository).existsById(1);
            verifyNoMoreInteractions(sessionRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(sessionRepository.existsById(1)).thenReturn(true);
            when(sessionRepository.save(session1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> sessionService.update(session1));
            verify(sessionRepository).existsById(1);
            verify(sessionRepository).save(session1);
            verifyNoMoreInteractions(sessionRepository);
        }
    }
}
