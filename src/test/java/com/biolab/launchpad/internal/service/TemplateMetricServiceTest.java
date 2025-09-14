package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.TemplateMetricRepository;
import com.biolab.launchpad.internal.repository.model.TemplateMetric;
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
@DisplayName("TemplateMetricService Unit Tests")
class TemplateMetricServiceTest {

    @Mock
    private TemplateMetricRepository templateMetricRepository;

    @InjectMocks
    private TemplateMetricService templateMetricService;

    private TemplateMetric templateMetric1Input;
    private TemplateMetric templateMetric1;
    private TemplateMetric templateMetric2;

    @BeforeEach
    void setUp() {
        templateMetric1Input = TemplateMetric.builder()
                .build();

        templateMetric1 = TemplateMetric.builder()
                .id(1)
                .build();

        templateMetric2 = TemplateMetric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(templateMetricRepository.save(templateMetric1Input)).thenReturn(templateMetric1);

            TemplateMetric result = templateMetricService.create(templateMetric1Input);

            assertThat(result).isSameAs(templateMetric1);
            verify(templateMetricRepository).save(templateMetric1Input);
            verifyNoMoreInteractions(templateMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(templateMetricRepository.save(templateMetric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> templateMetricService.create(templateMetric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(templateMetricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all template_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(templateMetricRepository.findAll()).thenReturn(List.of(templateMetric1, templateMetric2));

            List<TemplateMetric> all = templateMetricService.findAll();

            assertThat(all).containsExactly(templateMetric1, templateMetric2);
            verify(templateMetricRepository).findAll();
            verifyNoMoreInteractions(templateMetricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return template_metric when found")
        void findById_ok_returns_optional() {
            when(templateMetricRepository.findById(1)).thenReturn(Optional.of(templateMetric1));

            Optional<TemplateMetric> result = templateMetricService.findById(1);

            assertThat(result).contains(templateMetric1);
            verify(templateMetricRepository).findById(1);
            verifyNoMoreInteractions(templateMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(templateMetricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> templateMetricService.findById(1));
            verifyNoMoreInteractions(templateMetricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete template_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(templateMetricRepository.existsById(1)).thenReturn(true);

            templateMetricService.deleteById(1);

            verify(templateMetricRepository).existsById(1);
            verify(templateMetricRepository).deleteById(1);
            verifyNoMoreInteractions(templateMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when template_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(templateMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> templateMetricService.deleteById(1));
            verify(templateMetricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(templateMetricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(templateMetricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(templateMetricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> templateMetricService.deleteById(1));
            verifyNoMoreInteractions(templateMetricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return template_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(templateMetricRepository.existsById(1)).thenReturn(true);
            when(templateMetricRepository.save(templateMetric1)).thenReturn(templateMetric1);

            TemplateMetric result = templateMetricService.update(templateMetric1);

            assertEquals(1, result.getId().intValue());
            verify(templateMetricRepository).existsById(1);
            verify(templateMetricRepository).save(templateMetric1);
            verifyNoMoreInteractions(templateMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when template_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(templateMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> templateMetricService.update(templateMetric1));
            verify(templateMetricRepository).existsById(1);
            verifyNoMoreInteractions(templateMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(templateMetricRepository.existsById(1)).thenReturn(true);
            when(templateMetricRepository.save(templateMetric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> templateMetricService.update(templateMetric1));
            verify(templateMetricRepository).existsById(1);
            verify(templateMetricRepository).save(templateMetric1);
            verifyNoMoreInteractions(templateMetricRepository);
        }
    }
}