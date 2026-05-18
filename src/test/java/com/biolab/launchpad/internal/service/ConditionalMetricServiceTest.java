package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ConditionalMetricRepository;
import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
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
@DisplayName("ConditionalMetricService Unit Tests")
class ConditionalMetricServiceTest {

    @Mock
    private ConditionalMetricRepository conditionalMetricRepository;

    @InjectMocks
    private ConditionalMetricService conditionalMetricService;

    private ConditionalMetric cm1Input;
    private ConditionalMetric cm1;
    private ConditionalMetric cm2;

    @BeforeEach
    void setUp() {
        cm1Input = ConditionalMetric.builder()
                .name("Exit velocity from T")
                .conditionId(1)
                .metricId(1)
                .build();

        cm1 = ConditionalMetric.builder()
                .id(1)
                .name("Exit velocity from T")
                .conditionId(1)
                .metricId(1)
                .build();

        cm2 = ConditionalMetric.builder()
                .id(2)
                .name("Spin rate pitching machine")
                .conditionId(2)
                .metricId(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(conditionalMetricRepository.save(cm1Input)).thenReturn(cm1);

            ConditionalMetric result = conditionalMetricService.create(cm1Input);

            assertThat(result).isSameAs(cm1);
            verify(conditionalMetricRepository).save(cm1Input);
            verifyNoMoreInteractions(conditionalMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(conditionalMetricRepository.save(cm1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> conditionalMetricService.create(cm1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(conditionalMetricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all conditionalMetrics from repository")
        void findAll_returns_list_from_repo() {
            when(conditionalMetricRepository.findAll()).thenReturn(List.of(cm1, cm2));

            List<ConditionalMetric> all = conditionalMetricService.findAll();

            assertThat(all).containsExactly(cm1, cm2);
            verify(conditionalMetricRepository).findAll();
            verifyNoMoreInteractions(conditionalMetricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return conditionalMetric when found")
        void findById_ok_returns_optional() {
            when(conditionalMetricRepository.findById(1)).thenReturn(Optional.of(cm1));

            Optional<ConditionalMetric> result = conditionalMetricService.findById(1);

            assertThat(result).contains(cm1);
            verify(conditionalMetricRepository).findById(1);
            verifyNoMoreInteractions(conditionalMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(conditionalMetricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> conditionalMetricService.findById(1));
            verifyNoMoreInteractions(conditionalMetricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete conditionalMetric when it exists")
        void deleteById_when_exists_deletes() {
            when(conditionalMetricRepository.existsById(1)).thenReturn(true);

            conditionalMetricService.deleteById(1);

            verify(conditionalMetricRepository).existsById(1);
            verify(conditionalMetricRepository).deleteById(1);
            verifyNoMoreInteractions(conditionalMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when conditionalMetric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(conditionalMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> conditionalMetricService.deleteById(1));
            verify(conditionalMetricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(conditionalMetricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(conditionalMetricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(conditionalMetricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> conditionalMetricService.deleteById(1));
            verifyNoMoreInteractions(conditionalMetricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return conditionalMetric when it exists")
        void update_when_exists_saves_and_returns() {
            when(conditionalMetricRepository.existsById(1)).thenReturn(true);
            when(conditionalMetricRepository.save(cm1)).thenReturn(cm1);

            ConditionalMetric result = conditionalMetricService.update(cm1);

            assertEquals(1, result.getId());
            verify(conditionalMetricRepository).existsById(1);
            verify(conditionalMetricRepository).save(cm1);
            verifyNoMoreInteractions(conditionalMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when conditionalMetric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(conditionalMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> conditionalMetricService.update(cm1));
            verify(conditionalMetricRepository).existsById(1);
            verifyNoMoreInteractions(conditionalMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(conditionalMetricRepository.existsById(1)).thenReturn(true);
            when(conditionalMetricRepository.save(cm1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> conditionalMetricService.update(cm1));
            verify(conditionalMetricRepository).existsById(1);
            verify(conditionalMetricRepository).save(cm1);
            verifyNoMoreInteractions(conditionalMetricRepository);
        }
    }
}
