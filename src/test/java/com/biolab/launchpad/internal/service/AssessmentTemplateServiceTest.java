package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentTemplateRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentTemplate;
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
@DisplayName("Assessment_templateService Unit Tests")
class AssessmentTemplateServiceTest {

    @Mock
    private AssessmentTemplateRepository assessmentTemplateRepository;

    @InjectMocks
    private AssessmentTemplateService assessmentTemplateService;

    private AssessmentTemplate assessmentTemplate1Input;
    private AssessmentTemplate assessmentTemplate1;
    private AssessmentTemplate assessmentTemplate2;

    @BeforeEach
    void setUp() {
        assessmentTemplate1Input = AssessmentTemplate.builder()
                .name("ass_temp")
                .build();

        assessmentTemplate1 = AssessmentTemplate.builder()
                .id(1)
                .name("ass_temp")
                .build();

        assessmentTemplate2 = AssessmentTemplate.builder()
                .id(2)
                .name("ass_temp_max")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessmentTemplateRepository.save(assessmentTemplate1Input)).thenReturn(assessmentTemplate1);

            AssessmentTemplate result = assessmentTemplateService.create(assessmentTemplate1Input);

            assertThat(result).isSameAs(assessmentTemplate1);
            verify(assessmentTemplateRepository).save(assessmentTemplate1Input);
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessmentTemplateRepository.save(assessmentTemplate1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessmentTemplateService.create(assessmentTemplate1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessment_templates from repository")
        void findAll_returns_list_from_repo() {
            when(assessmentTemplateRepository.findAll()).thenReturn(List.of(assessmentTemplate1, assessmentTemplate2));

            List<AssessmentTemplate> all = assessmentTemplateService.findAll();

            assertThat(all).containsExactly(assessmentTemplate1, assessmentTemplate2);
            verify(assessmentTemplateRepository).findAll();
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment_template when found")
        void findById_ok_returns_optional() {
            when(assessmentTemplateRepository.findById(1)).thenReturn(Optional.of(assessmentTemplate1));

            Optional<AssessmentTemplate> result = assessmentTemplateService.findById(1);

            assertThat(result).contains(assessmentTemplate1);
            verify(assessmentTemplateRepository).findById(1);
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessmentTemplateRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessmentTemplateService.findById(1));
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment_template when it exists")
        void deleteById_when_exists_deletes() {
            when(assessmentTemplateRepository.existsById(1)).thenReturn(true);

            assessmentTemplateService.deleteById(1);

            verify(assessmentTemplateRepository).existsById(1);
            verify(assessmentTemplateRepository).deleteById(1);
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_template does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessmentTemplateRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentTemplateService.deleteById(1));
            verify(assessmentTemplateRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessmentTemplateRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessmentTemplateRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessmentTemplateService.deleteById(1));
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment_template when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessmentTemplateRepository.existsById(1)).thenReturn(true);
            when(assessmentTemplateRepository.save(assessmentTemplate1)).thenReturn(assessmentTemplate1);

            AssessmentTemplate result = assessmentTemplateService.update(assessmentTemplate1);

            assertEquals(1, result.getId());
            verify(assessmentTemplateRepository).existsById(1);
            verify(assessmentTemplateRepository).save(assessmentTemplate1);
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_template does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessmentTemplateRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentTemplateService.update(assessmentTemplate1));
            verify(assessmentTemplateRepository).existsById(1);
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessmentTemplateRepository.existsById(1)).thenReturn(true);
            when(assessmentTemplateRepository.save(assessmentTemplate1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessmentTemplateService.update(assessmentTemplate1));
            verify(assessmentTemplateRepository).existsById(1);
            verify(assessmentTemplateRepository).save(assessmentTemplate1);
            verifyNoMoreInteractions(assessmentTemplateRepository);
        }
    }
}