package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
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
@DisplayName("AssessmentMetricService Unit Tests")
class AssessmentMetricServiceTest {

    @Mock
    private AssessmentMetricRepository assessmentMetricRepository;

    @InjectMocks
    private AssessmentMetricService assessmentMetricService;

    private AssessmentMetric assessmentMetric1Input;
    private AssessmentMetric assessmentMetric1;
    private AssessmentMetric assessmentMetric2;

    @BeforeEach
    void setUp() {
        assessmentMetric1Input = AssessmentMetric.builder()
                .build();

        assessmentMetric1 = AssessmentMetric.builder()
                .id(1)
                .build();

        assessmentMetric2 = AssessmentMetric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessmentMetricRepository.save(assessmentMetric1Input)).thenReturn(assessmentMetric1);

            AssessmentMetric result = assessmentMetricService.create(assessmentMetric1Input);

            assertThat(result).isSameAs(assessmentMetric1);
            verify(assessmentMetricRepository).save(assessmentMetric1Input);
            verifyNoMoreInteractions(assessmentMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessmentMetricRepository.save(assessmentMetric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessmentMetricService.create(assessmentMetric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessmentMetricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessment_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(assessmentMetricRepository.findAll()).thenReturn(List.of(assessmentMetric1, assessmentMetric2));

            List<AssessmentMetric> all = assessmentMetricService.findAll();

            assertThat(all).containsExactly(assessmentMetric1, assessmentMetric2);
            verify(assessmentMetricRepository).findAll();
            verifyNoMoreInteractions(assessmentMetricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment_metric when found")
        void findById_ok_returns_optional() {
            when(assessmentMetricRepository.findById(1)).thenReturn(Optional.of(assessmentMetric1));

            Optional<AssessmentMetric> result = assessmentMetricService.findById(1);

            assertThat(result).contains(assessmentMetric1);
            verify(assessmentMetricRepository).findById(1);
            verifyNoMoreInteractions(assessmentMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessmentMetricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessmentMetricService.findById(1));
            verifyNoMoreInteractions(assessmentMetricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(assessmentMetricRepository.existsById(1)).thenReturn(true);

            assessmentMetricService.deleteById(1);

            verify(assessmentMetricRepository).existsById(1);
            verify(assessmentMetricRepository).deleteById(1);
            verifyNoMoreInteractions(assessmentMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessmentMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentMetricService.deleteById(1));
            verify(assessmentMetricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessmentMetricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessmentMetricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessmentMetricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessmentMetricService.deleteById(1));
            verifyNoMoreInteractions(assessmentMetricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessmentMetricRepository.existsById(1)).thenReturn(true);
            when(assessmentMetricRepository.save(assessmentMetric1)).thenReturn(assessmentMetric1);

            AssessmentMetric result = assessmentMetricService.update(assessmentMetric1);

            assertEquals(1, result.getId().intValue());
            verify(assessmentMetricRepository).existsById(1);
            verify(assessmentMetricRepository).save(assessmentMetric1);
            verifyNoMoreInteractions(assessmentMetricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessmentMetricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentMetricService.update(assessmentMetric1));
            verify(assessmentMetricRepository).existsById(1);
            verifyNoMoreInteractions(assessmentMetricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessmentMetricRepository.existsById(1)).thenReturn(true);
            when(assessmentMetricRepository.save(assessmentMetric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessmentMetricService.update(assessmentMetric1));
            verify(assessmentMetricRepository).existsById(1);
            verify(assessmentMetricRepository).save(assessmentMetric1);
            verifyNoMoreInteractions(assessmentMetricRepository);
        }
    }
}