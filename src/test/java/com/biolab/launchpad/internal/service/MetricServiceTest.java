package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.MetricRepository;
import com.biolab.launchpad.internal.repository.model.Metric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.security.exceptions.PersistException;
import com.biolab.launchpad.internal.service.MetricService;
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
@DisplayName("MetricService Unit Tests")
class MetricServiceTest {

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private MetricService metricService;

    private Metric metric1Input;
    private Metric metric1;
    private Metric metric2;

    @BeforeEach
    void setUp() {
        metric1Input = Metric.builder()
                .name("avg_exit_velo")
                .build();

        metric1 = Metric.builder()
                .id(1)
                .name("avg_exit_velo")
                .build();

        metric2 = Metric.builder()
                .id(2)
                .name("max_spin_rate")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(metricRepository.save(metric1Input)).thenReturn(metric1);

            Metric result = metricService.create(metric1Input);

            assertThat(result).isSameAs(metric1);
            verify(metricRepository).save(metric1Input);
            verifyNoMoreInteractions(metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(metricRepository.save(metric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> metricService.create(metric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(metricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all metrics from repository")
        void findAll_returns_list_from_repo() {
            when(metricRepository.findAll()).thenReturn(List.of(metric1, metric2));

            List<Metric> all = metricService.findAll();

            assertThat(all).containsExactly(metric1, metric2);
            verify(metricRepository).findAll();
            verifyNoMoreInteractions(metricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return metric when found")
        void findById_ok_returns_optional() {
            when(metricRepository.findById(1)).thenReturn(Optional.of(metric1));

            Optional<Metric> result = metricService.findById(1);

            assertThat(result).contains(metric1);
            verify(metricRepository).findById(1);
            verifyNoMoreInteractions(metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(metricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> metricService.findById(1));
            verifyNoMoreInteractions(metricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete metric when it exists")
        void deleteById_when_exists_deletes() {
            when(metricRepository.existsById(1)).thenReturn(true);

            metricService.deleteById(1);

            verify(metricRepository).existsById(1);
            verify(metricRepository).deleteById(1);
            verifyNoMoreInteractions(metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> metricService.deleteById(1));
            verify(metricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(metricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(metricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(metricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> metricService.deleteById(1));
            verifyNoMoreInteractions(metricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(metricRepository.existsById(1)).thenReturn(true);
            when(metricRepository.save(metric1)).thenReturn(metric1);

            Metric result = metricService.update(metric1);

            assertEquals(1, result.getId());
            verify(metricRepository).existsById(1);
            verify(metricRepository).save(metric1);
            verifyNoMoreInteractions(metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> metricService.update(metric1));
            verify(metricRepository).existsById(1);
            verifyNoMoreInteractions(metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(metricRepository.existsById(1)).thenReturn(true);
            when(metricRepository.save(metric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> metricService.update(metric1));
            verify(metricRepository).existsById(1);
            verify(metricRepository).save(metric1);
            verifyNoMoreInteractions(metricRepository);
        }
    }
}