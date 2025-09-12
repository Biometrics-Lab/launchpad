package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Assessment_metricRepository;
import com.biolab.launchpad.internal.repository.model.Assessment_metric;
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
@DisplayName("Assessment_metricService Unit Tests")
class Assessment_metricServiceTest {

    @Mock
    private Assessment_metricRepository assessment_metricRepository;

    @InjectMocks
    private Assessment_metricService assessment_metricService;

    private Assessment_metric assessment_metric1Input;
    private Assessment_metric assessment_metric1;
    private Assessment_metric assessment_metric2;

    @BeforeEach
    void setUp() {
        assessment_metric1Input = Assessment_metric.builder()
                .build();

        assessment_metric1 = Assessment_metric.builder()
                .id(1)
                .build();

        assessment_metric2 = Assessment_metric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessment_metricRepository.save(assessment_metric1Input)).thenReturn(assessment_metric1);

            Assessment_metric result = assessment_metricService.create(assessment_metric1Input);

            assertThat(result).isSameAs(assessment_metric1);
            verify(assessment_metricRepository).save(assessment_metric1Input);
            verifyNoMoreInteractions(assessment_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessment_metricRepository.save(assessment_metric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessment_metricService.create(assessment_metric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessment_metricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessment_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(assessment_metricRepository.findAll()).thenReturn(List.of(assessment_metric1, assessment_metric2));

            List<Assessment_metric> all = assessment_metricService.findAll();

            assertThat(all).containsExactly(assessment_metric1, assessment_metric2);
            verify(assessment_metricRepository).findAll();
            verifyNoMoreInteractions(assessment_metricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment_metric when found")
        void findById_ok_returns_optional() {
            when(assessment_metricRepository.findById(1)).thenReturn(Optional.of(assessment_metric1));

            Optional<Assessment_metric> result = assessment_metricService.findById(1);

            assertThat(result).contains(assessment_metric1);
            verify(assessment_metricRepository).findById(1);
            verifyNoMoreInteractions(assessment_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessment_metricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessment_metricService.findById(1));
            verifyNoMoreInteractions(assessment_metricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(assessment_metricRepository.existsById(1)).thenReturn(true);

            assessment_metricService.deleteById(1);

            verify(assessment_metricRepository).existsById(1);
            verify(assessment_metricRepository).deleteById(1);
            verifyNoMoreInteractions(assessment_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessment_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessment_metricService.deleteById(1));
            verify(assessment_metricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessment_metricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessment_metricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessment_metricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessment_metricService.deleteById(1));
            verifyNoMoreInteractions(assessment_metricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessment_metricRepository.existsById(1)).thenReturn(true);
            when(assessment_metricRepository.save(assessment_metric1)).thenReturn(assessment_metric1);

            Assessment_metric result = assessment_metricService.update(assessment_metric1);

            assertEquals(1, result.getId().intValue());
            verify(assessment_metricRepository).existsById(1);
            verify(assessment_metricRepository).save(assessment_metric1);
            verifyNoMoreInteractions(assessment_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessment_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessment_metricService.update(assessment_metric1));
            verify(assessment_metricRepository).existsById(1);
            verifyNoMoreInteractions(assessment_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessment_metricRepository.existsById(1)).thenReturn(true);
            when(assessment_metricRepository.save(assessment_metric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessment_metricService.update(assessment_metric1));
            verify(assessment_metricRepository).existsById(1);
            verify(assessment_metricRepository).save(assessment_metric1);
            verifyNoMoreInteractions(assessment_metricRepository);
        }
    }
}