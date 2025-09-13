package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentResourceRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentResource;
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
@DisplayName("Assessment_resourceService Unit Tests")
class AssessmentResourceServiceTest {

    @Mock
    private AssessmentResourceRepository assessmentResourceRepository;

    @InjectMocks
    private AssessmentResourceService assessmentResourceService;

    private AssessmentResource assessmentResource1Input;
    private AssessmentResource assessmentResource1;
    private AssessmentResource assessmentResource2;

    @BeforeEach
    void setUp() {
        assessmentResource1Input = AssessmentResource.builder()
                .build();

        assessmentResource1 = AssessmentResource.builder()
                .id(1)
                .build();

        assessmentResource2 = AssessmentResource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessmentResourceRepository.save(assessmentResource1Input)).thenReturn(assessmentResource1);

            AssessmentResource result = assessmentResourceService.create(assessmentResource1Input);

            assertThat(result).isSameAs(assessmentResource1);
            verify(assessmentResourceRepository).save(assessmentResource1Input);
            verifyNoMoreInteractions(assessmentResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessmentResourceRepository.save(assessmentResource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessmentResourceService.create(assessmentResource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessmentResourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessment_resources from repository")
        void findAll_returns_list_from_repo() {
            when(assessmentResourceRepository.findAll()).thenReturn(List.of(assessmentResource1, assessmentResource2));

            List<AssessmentResource> all = assessmentResourceService.findAll();

            assertThat(all).containsExactly(assessmentResource1, assessmentResource2);
            verify(assessmentResourceRepository).findAll();
            verifyNoMoreInteractions(assessmentResourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment_resource when found")
        void findById_ok_returns_optional() {
            when(assessmentResourceRepository.findById(1)).thenReturn(Optional.of(assessmentResource1));

            Optional<AssessmentResource> result = assessmentResourceService.findById(1);

            assertThat(result).contains(assessmentResource1);
            verify(assessmentResourceRepository).findById(1);
            verifyNoMoreInteractions(assessmentResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessmentResourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessmentResourceService.findById(1));
            verifyNoMoreInteractions(assessmentResourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment_resource when it exists")
        void deleteById_when_exists_deletes() {
            when(assessmentResourceRepository.existsById(1)).thenReturn(true);

            assessmentResourceService.deleteById(1);

            verify(assessmentResourceRepository).existsById(1);
            verify(assessmentResourceRepository).deleteById(1);
            verifyNoMoreInteractions(assessmentResourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_resource does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessmentResourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentResourceService.deleteById(1));
            verify(assessmentResourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessmentResourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessmentResourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessmentResourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessmentResourceService.deleteById(1));
            verifyNoMoreInteractions(assessmentResourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment_resource when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessmentResourceRepository.existsById(1)).thenReturn(true);
            when(assessmentResourceRepository.save(assessmentResource1)).thenReturn(assessmentResource1);

            AssessmentResource result = assessmentResourceService.update(assessmentResource1);

            assertEquals(1, result.getId().intValue());
            verify(assessmentResourceRepository).existsById(1);
            verify(assessmentResourceRepository).save(assessmentResource1);
            verifyNoMoreInteractions(assessmentResourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_resource does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessmentResourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessmentResourceService.update(assessmentResource1));
            verify(assessmentResourceRepository).existsById(1);
            verifyNoMoreInteractions(assessmentResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessmentResourceRepository.existsById(1)).thenReturn(true);
            when(assessmentResourceRepository.save(assessmentResource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessmentResourceService.update(assessmentResource1));
            verify(assessmentResourceRepository).existsById(1);
            verify(assessmentResourceRepository).save(assessmentResource1);
            verifyNoMoreInteractions(assessmentResourceRepository);
        }
    }
}