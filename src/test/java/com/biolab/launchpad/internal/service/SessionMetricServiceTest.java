package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.SessionMetricRepository;
import com.biolab.launchpad.internal.repository.model.SessionMetric;
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
@DisplayName("Session_metricService Unit Tests")
class SessionMetricServiceTest {

    @Mock
    private SessionMetricRepository sessionMetricRepository;

    @InjectMocks
    private SessionMetricService sessionMetricService;

    private SessionMetric sessionMetric1Input;
    private SessionMetric sessionMetric1;
    private SessionMetric sessionMetric2;

    @BeforeEach
    void setUp() {
        sessionMetric1Input = SessionMetric.builder()
                .build();

        sessionMetric1 = SessionMetric.builder()
                .id(1)
                .build();

        sessionMetric2 = SessionMetric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(sessionMetricRepository.save(sessionMetric1Input)).thenReturn(sessionMetric1);

            SessionMetric result = sessionMetricService.create(sessionMetric1Input);

            assertThat(result).isSameAs(sessionMetric1);
            verify(sessionMetricRepository).save(sessionMetric1Input);
            verifyNoMoreInteractions(sessionMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(sessionMetricRepository.save(sessionMetric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> sessionMetricService.create(sessionMetric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(sessionMetricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all session_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(sessionMetricRepository.findAll()).thenReturn(List.of(sessionMetric1, sessionMetric2));

            List<SessionMetric> all = sessionMetricService.findAll();

            assertThat(all).containsExactly(sessionMetric1, sessionMetric2);
            verify(sessionMetricRepository).findAll();
            verifyNoMoreInteractions(sessionMetricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return session_metric when found")
        void findById_ok_returns_optional() {
            when(sessionMetricRepository.findById(1)).thenReturn(Optional.of(sessionMetric1));

            Optional<SessionMetric> result = sessionMetricService.findById(1);

            assertThat(result).contains(sessionMetric1);
            verify(sessionMetricRepository).findById(1);
            verifyNoMoreInteractions(sessionMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(sessionMetricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> sessionMetricService.findById(1));
            verifyNoMoreInteractions(sessionMetricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete session_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(sessionMetricRepository.existsById(1)).thenReturn(true);

            sessionMetricService.deleteById(1);

            verify(sessionMetricRepository).existsById(1);
            verify(sessionMetricRepository).deleteById(1);
            verifyNoMoreInteractions(sessionMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(sessionMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sessionMetricService.deleteById(1));
            verify(sessionMetricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(sessionMetricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(sessionMetricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(sessionMetricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> sessionMetricService.deleteById(1));
            verifyNoMoreInteractions(sessionMetricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return session_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(sessionMetricRepository.existsById(1)).thenReturn(true);
            when(sessionMetricRepository.save(sessionMetric1)).thenReturn(sessionMetric1);

            SessionMetric result = sessionMetricService.update(sessionMetric1);

            assertEquals(1, result.getId().intValue());
            verify(sessionMetricRepository).existsById(1);
            verify(sessionMetricRepository).save(sessionMetric1);
            verifyNoMoreInteractions(sessionMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(sessionMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sessionMetricService.update(sessionMetric1));
            verify(sessionMetricRepository).existsById(1);
            verifyNoMoreInteractions(sessionMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(sessionMetricRepository.existsById(1)).thenReturn(true);
            when(sessionMetricRepository.save(sessionMetric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> sessionMetricService.update(sessionMetric1));
            verify(sessionMetricRepository).existsById(1);
            verify(sessionMetricRepository).save(sessionMetric1);
            verifyNoMoreInteractions(sessionMetricRepository);
        }
    }
}