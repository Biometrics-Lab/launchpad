package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ModelMetricRepository;
import com.biolab.launchpad.internal.repository.model.ModelMetric;
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
@DisplayName("ModelMetricService Unit Tests")
class ModelMetricServiceTest {

    @Mock
    private ModelMetricRepository modelMetricRepository;

    @InjectMocks
    private ModelMetricService modelMetricService;

    private ModelMetric modelMetric1Input;
    private ModelMetric modelMetric1;
    private ModelMetric modelMetric2;

    @BeforeEach
    void setUp() {
        modelMetric1Input = ModelMetric.builder()
                .build();

        modelMetric1 = ModelMetric.builder()
                .id(1)
                .build();

        modelMetric2 = ModelMetric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(modelMetricRepository.save(modelMetric1Input)).thenReturn(modelMetric1);

            ModelMetric result = modelMetricService.create(modelMetric1Input);

            assertThat(result).isSameAs(modelMetric1);
            verify(modelMetricRepository).save(modelMetric1Input);
            verifyNoMoreInteractions(modelMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(modelMetricRepository.save(modelMetric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> modelMetricService.create(modelMetric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(modelMetricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all model_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(modelMetricRepository.findAll()).thenReturn(List.of(modelMetric1, modelMetric2));

            List<ModelMetric> all = modelMetricService.findAll();

            assertThat(all).containsExactly(modelMetric1, modelMetric2);
            verify(modelMetricRepository).findAll();
            verifyNoMoreInteractions(modelMetricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return model_metric when found")
        void findById_ok_returns_optional() {
            when(modelMetricRepository.findById(1)).thenReturn(Optional.of(modelMetric1));

            Optional<ModelMetric> result = modelMetricService.findById(1);

            assertThat(result).contains(modelMetric1);
            verify(modelMetricRepository).findById(1);
            verifyNoMoreInteractions(modelMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(modelMetricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> modelMetricService.findById(1));
            verifyNoMoreInteractions(modelMetricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete model_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(modelMetricRepository.existsById(1)).thenReturn(true);

            modelMetricService.deleteById(1);

            verify(modelMetricRepository).existsById(1);
            verify(modelMetricRepository).deleteById(1);
            verifyNoMoreInteractions(modelMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when model_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(modelMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> modelMetricService.deleteById(1));
            verify(modelMetricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(modelMetricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(modelMetricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(modelMetricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> modelMetricService.deleteById(1));
            verifyNoMoreInteractions(modelMetricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return model_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(modelMetricRepository.existsById(1)).thenReturn(true);
            when(modelMetricRepository.save(modelMetric1)).thenReturn(modelMetric1);

            ModelMetric result = modelMetricService.update(modelMetric1);

            assertEquals(1, result.getId().intValue());
            verify(modelMetricRepository).existsById(1);
            verify(modelMetricRepository).save(modelMetric1);
            verifyNoMoreInteractions(modelMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when model_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(modelMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> modelMetricService.update(modelMetric1));
            verify(modelMetricRepository).existsById(1);
            verifyNoMoreInteractions(modelMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(modelMetricRepository.existsById(1)).thenReturn(true);
            when(modelMetricRepository.save(modelMetric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> modelMetricService.update(modelMetric1));
            verify(modelMetricRepository).existsById(1);
            verify(modelMetricRepository).save(modelMetric1);
            verifyNoMoreInteractions(modelMetricRepository);
        }
    }
}