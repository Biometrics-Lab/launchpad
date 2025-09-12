package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Template_metricRepository;
import com.biolab.launchpad.internal.repository.model.Template_metric;
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
@DisplayName("Template_metricService Unit Tests")
class Template_metricServiceTest {

    @Mock
    private Template_metricRepository template_metricRepository;

    @InjectMocks
    private Template_metricService template_metricService;

    private Template_metric template_metric1Input;
    private Template_metric template_metric1;
    private Template_metric template_metric2;

    @BeforeEach
    void setUp() {
        template_metric1Input = Template_metric.builder()
                .build();

        template_metric1 = Template_metric.builder()
                .id(1)
                .build();

        template_metric2 = Template_metric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(template_metricRepository.save(template_metric1Input)).thenReturn(template_metric1);

            Template_metric result = template_metricService.create(template_metric1Input);

            assertThat(result).isSameAs(template_metric1);
            verify(template_metricRepository).save(template_metric1Input);
            verifyNoMoreInteractions(template_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(template_metricRepository.save(template_metric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> template_metricService.create(template_metric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(template_metricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all template_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(template_metricRepository.findAll()).thenReturn(List.of(template_metric1, template_metric2));

            List<Template_metric> all = template_metricService.findAll();

            assertThat(all).containsExactly(template_metric1, template_metric2);
            verify(template_metricRepository).findAll();
            verifyNoMoreInteractions(template_metricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return template_metric when found")
        void findById_ok_returns_optional() {
            when(template_metricRepository.findById(1)).thenReturn(Optional.of(template_metric1));

            Optional<Template_metric> result = template_metricService.findById(1);

            assertThat(result).contains(template_metric1);
            verify(template_metricRepository).findById(1);
            verifyNoMoreInteractions(template_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(template_metricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> template_metricService.findById(1));
            verifyNoMoreInteractions(template_metricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete template_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(template_metricRepository.existsById(1)).thenReturn(true);

            template_metricService.deleteById(1);

            verify(template_metricRepository).existsById(1);
            verify(template_metricRepository).deleteById(1);
            verifyNoMoreInteractions(template_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when template_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(template_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> template_metricService.deleteById(1));
            verify(template_metricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(template_metricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(template_metricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(template_metricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> template_metricService.deleteById(1));
            verifyNoMoreInteractions(template_metricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return template_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(template_metricRepository.existsById(1)).thenReturn(true);
            when(template_metricRepository.save(template_metric1)).thenReturn(template_metric1);

            Template_metric result = template_metricService.update(template_metric1);

            assertEquals(1, result.getId().intValue());
            verify(template_metricRepository).existsById(1);
            verify(template_metricRepository).save(template_metric1);
            verifyNoMoreInteractions(template_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when template_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(template_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> template_metricService.update(template_metric1));
            verify(template_metricRepository).existsById(1);
            verifyNoMoreInteractions(template_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(template_metricRepository.existsById(1)).thenReturn(true);
            when(template_metricRepository.save(template_metric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> template_metricService.update(template_metric1));
            verify(template_metricRepository).existsById(1);
            verify(template_metricRepository).save(template_metric1);
            verifyNoMoreInteractions(template_metricRepository);
        }
    }
}