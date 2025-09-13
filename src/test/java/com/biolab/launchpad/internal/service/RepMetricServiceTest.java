package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.model.RepMetric;
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
@DisplayName("Rep_metricService Unit Tests")
class RepMetricServiceTest {

    @Mock
    private RepMetricRepository repMetricRepository;

    @InjectMocks
    private RepMetricService repMetricService;

    private RepMetric repMetric1Input;
    private RepMetric repMetric1;
    private RepMetric repMetric2;

    @BeforeEach
    void setUp() {
        repMetric1Input = RepMetric.builder()
                .build();

        repMetric1 = RepMetric.builder()
                .id(1)
                .build();

        repMetric2 = RepMetric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(repMetricRepository.save(repMetric1Input)).thenReturn(repMetric1);

            RepMetric result = repMetricService.create(repMetric1Input);

            assertThat(result).isSameAs(repMetric1);
            verify(repMetricRepository).save(repMetric1Input);
            verifyNoMoreInteractions(repMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(repMetricRepository.save(repMetric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> repMetricService.create(repMetric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(repMetricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all rep_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(repMetricRepository.findAll()).thenReturn(List.of(repMetric1, repMetric2));

            List<RepMetric> all = repMetricService.findAll();

            assertThat(all).containsExactly(repMetric1, repMetric2);
            verify(repMetricRepository).findAll();
            verifyNoMoreInteractions(repMetricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep_metric when found")
        void findById_ok_returns_optional() {
            when(repMetricRepository.findById(1)).thenReturn(Optional.of(repMetric1));

            Optional<RepMetric> result = repMetricService.findById(1);

            assertThat(result).contains(repMetric1);
            verify(repMetricRepository).findById(1);
            verifyNoMoreInteractions(repMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(repMetricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> repMetricService.findById(1));
            verifyNoMoreInteractions(repMetricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(repMetricRepository.existsById(1)).thenReturn(true);

            repMetricService.deleteById(1);

            verify(repMetricRepository).existsById(1);
            verify(repMetricRepository).deleteById(1);
            verifyNoMoreInteractions(repMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(repMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repMetricService.deleteById(1));
            verify(repMetricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(repMetricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(repMetricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(repMetricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> repMetricService.deleteById(1));
            verifyNoMoreInteractions(repMetricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(repMetricRepository.existsById(1)).thenReturn(true);
            when(repMetricRepository.save(repMetric1)).thenReturn(repMetric1);

            RepMetric result = repMetricService.update(repMetric1);

            assertEquals(1, result.getId().intValue());
            verify(repMetricRepository).existsById(1);
            verify(repMetricRepository).save(repMetric1);
            verifyNoMoreInteractions(repMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(repMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repMetricService.update(repMetric1));
            verify(repMetricRepository).existsById(1);
            verifyNoMoreInteractions(repMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(repMetricRepository.existsById(1)).thenReturn(true);
            when(repMetricRepository.save(repMetric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> repMetricService.update(repMetric1));
            verify(repMetricRepository).existsById(1);
            verify(repMetricRepository).save(repMetric1);
            verifyNoMoreInteractions(repMetricRepository);
        }
    }
}