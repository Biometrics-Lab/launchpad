package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentRepository;
import com.biolab.launchpad.internal.repository.model.Assessment;
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
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssessmentService Unit Tests")
class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    private Assessment assessment1Input;
    private Assessment assessment1;
    private Assessment assessment2;

    @BeforeEach
    void setUp() {
        assessment1Input = Assessment.builder()
                .build();

        assessment1 = Assessment.builder()
                .id(1)
                .build();

        assessment2 = Assessment.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessmentRepository.save(assessment1Input)).thenReturn(assessment1);

            Assessment result = assessmentService.create(assessment1Input);

            assertThat(result).isSameAs(assessment1);
            verify(assessmentRepository).save(assessment1Input);
            verifyNoMoreInteractions(assessmentRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessmentRepository.save(assessment1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessmentService.create(assessment1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessmentRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessments from repository")
        void findAll_returns_list_from_repo() {
            when(assessmentRepository.findAll()).thenReturn(List.of(assessment1, assessment2));

            List<Assessment> all = assessmentService.findAll();

            assertThat(all).containsExactly(assessment1, assessment2);
            verify(assessmentRepository).findAll();
            verifyNoMoreInteractions(assessmentRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment when found")
        void findById_ok_returns_optional() {
            when(assessmentRepository.findById(1)).thenReturn(Optional.of(assessment1));

            Optional<Assessment> result = assessmentService.findById(1);

            assertThat(result).contains(assessment1);
            verify(assessmentRepository).findById(1);
            verifyNoMoreInteractions(assessmentRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessmentRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessmentService.findById(1));
            verifyNoMoreInteractions(assessmentRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment when it exists")
        void deleteById_when_exists_deletes() {
            when(assessmentRepository.existsById(1)).thenReturn(true);

            assessmentService.deleteById(1);

            verify(assessmentRepository).existsById(1);
            verify(assessmentRepository).deleteById(1);
            verifyNoMoreInteractions(assessmentRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessmentRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentService.deleteById(1));
            verify(assessmentRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessmentRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessmentRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessmentRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessmentService.deleteById(1));
            verifyNoMoreInteractions(assessmentRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessmentRepository.existsById(1)).thenReturn(true);
            when(assessmentRepository.save(assessment1)).thenReturn(assessment1);

            Assessment result = assessmentService.update(assessment1);

            assertEquals(1, result.getId().intValue());
            verify(assessmentRepository).existsById(1);
            verify(assessmentRepository).save(assessment1);
            verifyNoMoreInteractions(assessmentRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessmentRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentService.update(assessment1));
            verify(assessmentRepository).existsById(1);
            verifyNoMoreInteractions(assessmentRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessmentRepository.existsById(1)).thenReturn(true);
            when(assessmentRepository.save(assessment1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessmentService.update(assessment1));
            verify(assessmentRepository).existsById(1);
            verify(assessmentRepository).save(assessment1);
            verifyNoMoreInteractions(assessmentRepository);
        }
    }
}