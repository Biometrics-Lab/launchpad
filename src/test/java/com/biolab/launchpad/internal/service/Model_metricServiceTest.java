package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Model_metricRepository;
import com.biolab.launchpad.internal.repository.model.Model_metric;
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
@DisplayName("Model_metricService Unit Tests")
class Model_metricServiceTest {

    @Mock
    private Model_metricRepository model_metricRepository;

    @InjectMocks
    private Model_metricService model_metricService;

    private Model_metric model_metric1Input;
    private Model_metric model_metric1;
    private Model_metric model_metric2;

    @BeforeEach
    void setUp() {
        model_metric1Input = Model_metric.builder()
                .build();

        model_metric1 = Model_metric.builder()
                .id(1)
                .build();

        model_metric2 = Model_metric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(model_metricRepository.save(model_metric1Input)).thenReturn(model_metric1);

            Model_metric result = model_metricService.create(model_metric1Input);

            assertThat(result).isSameAs(model_metric1);
            verify(model_metricRepository).save(model_metric1Input);
            verifyNoMoreInteractions(model_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(model_metricRepository.save(model_metric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> model_metricService.create(model_metric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(model_metricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all model_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(model_metricRepository.findAll()).thenReturn(List.of(model_metric1, model_metric2));

            List<Model_metric> all = model_metricService.findAll();

            assertThat(all).containsExactly(model_metric1, model_metric2);
            verify(model_metricRepository).findAll();
            verifyNoMoreInteractions(model_metricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return model_metric when found")
        void findById_ok_returns_optional() {
            when(model_metricRepository.findById(1)).thenReturn(Optional.of(model_metric1));

            Optional<Model_metric> result = model_metricService.findById(1);

            assertThat(result).contains(model_metric1);
            verify(model_metricRepository).findById(1);
            verifyNoMoreInteractions(model_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(model_metricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> model_metricService.findById(1));
            verifyNoMoreInteractions(model_metricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete model_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(model_metricRepository.existsById(1)).thenReturn(true);

            model_metricService.deleteById(1);

            verify(model_metricRepository).existsById(1);
            verify(model_metricRepository).deleteById(1);
            verifyNoMoreInteractions(model_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when model_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(model_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> model_metricService.deleteById(1));
            verify(model_metricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(model_metricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(model_metricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(model_metricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> model_metricService.deleteById(1));
            verifyNoMoreInteractions(model_metricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return model_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(model_metricRepository.existsById(1)).thenReturn(true);
            when(model_metricRepository.save(model_metric1)).thenReturn(model_metric1);

            Model_metric result = model_metricService.update(model_metric1);

            assertEquals(1, result.getId().intValue());
            verify(model_metricRepository).existsById(1);
            verify(model_metricRepository).save(model_metric1);
            verifyNoMoreInteractions(model_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when model_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(model_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> model_metricService.update(model_metric1));
            verify(model_metricRepository).existsById(1);
            verifyNoMoreInteractions(model_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(model_metricRepository.existsById(1)).thenReturn(true);
            when(model_metricRepository.save(model_metric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> model_metricService.update(model_metric1));
            verify(model_metricRepository).existsById(1);
            verify(model_metricRepository).save(model_metric1);
            verifyNoMoreInteractions(model_metricRepository);
        }
    }
}