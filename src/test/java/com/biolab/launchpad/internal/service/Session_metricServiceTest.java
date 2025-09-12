package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Session_metricRepository;
import com.biolab.launchpad.internal.repository.model.Session_metric;
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
class Session_metricServiceTest {

    @Mock
    private Session_metricRepository session_metricRepository;

    @InjectMocks
    private Session_metricService session_metricService;

    private Session_metric session_metric1Input;
    private Session_metric session_metric1;
    private Session_metric session_metric2;

    @BeforeEach
    void setUp() {
        session_metric1Input = Session_metric.builder()
                .build();

        session_metric1 = Session_metric.builder()
                .id(1)
                .build();

        session_metric2 = Session_metric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(session_metricRepository.save(session_metric1Input)).thenReturn(session_metric1);

            Session_metric result = session_metricService.create(session_metric1Input);

            assertThat(result).isSameAs(session_metric1);
            verify(session_metricRepository).save(session_metric1Input);
            verifyNoMoreInteractions(session_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(session_metricRepository.save(session_metric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> session_metricService.create(session_metric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(session_metricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all session_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(session_metricRepository.findAll()).thenReturn(List.of(session_metric1, session_metric2));

            List<Session_metric> all = session_metricService.findAll();

            assertThat(all).containsExactly(session_metric1, session_metric2);
            verify(session_metricRepository).findAll();
            verifyNoMoreInteractions(session_metricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return session_metric when found")
        void findById_ok_returns_optional() {
            when(session_metricRepository.findById(1)).thenReturn(Optional.of(session_metric1));

            Optional<Session_metric> result = session_metricService.findById(1);

            assertThat(result).contains(session_metric1);
            verify(session_metricRepository).findById(1);
            verifyNoMoreInteractions(session_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(session_metricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> session_metricService.findById(1));
            verifyNoMoreInteractions(session_metricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete session_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(session_metricRepository.existsById(1)).thenReturn(true);

            session_metricService.deleteById(1);

            verify(session_metricRepository).existsById(1);
            verify(session_metricRepository).deleteById(1);
            verifyNoMoreInteractions(session_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(session_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> session_metricService.deleteById(1));
            verify(session_metricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(session_metricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(session_metricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(session_metricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> session_metricService.deleteById(1));
            verifyNoMoreInteractions(session_metricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return session_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(session_metricRepository.existsById(1)).thenReturn(true);
            when(session_metricRepository.save(session_metric1)).thenReturn(session_metric1);

            Session_metric result = session_metricService.update(session_metric1);

            assertEquals(1, result.getId().intValue());
            verify(session_metricRepository).existsById(1);
            verify(session_metricRepository).save(session_metric1);
            verifyNoMoreInteractions(session_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(session_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> session_metricService.update(session_metric1));
            verify(session_metricRepository).existsById(1);
            verifyNoMoreInteractions(session_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(session_metricRepository.existsById(1)).thenReturn(true);
            when(session_metricRepository.save(session_metric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> session_metricService.update(session_metric1));
            verify(session_metricRepository).existsById(1);
            verify(session_metricRepository).save(session_metric1);
            verifyNoMoreInteractions(session_metricRepository);
        }
    }
}