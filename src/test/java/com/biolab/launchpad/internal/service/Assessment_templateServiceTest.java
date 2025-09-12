package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Assessment_templateRepository;
import com.biolab.launchpad.internal.repository.model.Assessment_template;
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
class Assessment_templateServiceTest {

    @Mock
    private Assessment_templateRepository assessment_templateRepository;

    @InjectMocks
    private Assessment_templateService assessment_templateService;

    private Assessment_template assessment_template1Input;
    private Assessment_template assessment_template1;
    private Assessment_template assessment_template2;

    @BeforeEach
    void setUp() {
        assessment_template1Input = Assessment_template.builder()
                .name("ass_temp")
                .build();

        assessment_template1 = Assessment_template.builder()
                .id(1)
                .name("ass_temp")
                .build();

        assessment_template2 = Assessment_template.builder()
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
            when(assessment_templateRepository.save(assessment_template1Input)).thenReturn(assessment_template1);

            Assessment_template result = assessment_templateService.create(assessment_template1Input);

            assertThat(result).isSameAs(assessment_template1);
            verify(assessment_templateRepository).save(assessment_template1Input);
            verifyNoMoreInteractions(assessment_templateRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessment_templateRepository.save(assessment_template1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessment_templateService.create(assessment_template1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessment_templateRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessment_templates from repository")
        void findAll_returns_list_from_repo() {
            when(assessment_templateRepository.findAll()).thenReturn(List.of(assessment_template1, assessment_template2));

            List<Assessment_template> all = assessment_templateService.findAll();

            assertThat(all).containsExactly(assessment_template1, assessment_template2);
            verify(assessment_templateRepository).findAll();
            verifyNoMoreInteractions(assessment_templateRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment_template when found")
        void findById_ok_returns_optional() {
            when(assessment_templateRepository.findById(1)).thenReturn(Optional.of(assessment_template1));

            Optional<Assessment_template> result = assessment_templateService.findById(1);

            assertThat(result).contains(assessment_template1);
            verify(assessment_templateRepository).findById(1);
            verifyNoMoreInteractions(assessment_templateRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessment_templateRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessment_templateService.findById(1));
            verifyNoMoreInteractions(assessment_templateRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment_template when it exists")
        void deleteById_when_exists_deletes() {
            when(assessment_templateRepository.existsById(1)).thenReturn(true);

            assessment_templateService.deleteById(1);

            verify(assessment_templateRepository).existsById(1);
            verify(assessment_templateRepository).deleteById(1);
            verifyNoMoreInteractions(assessment_templateRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_template does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessment_templateRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessment_templateService.deleteById(1));
            verify(assessment_templateRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessment_templateRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessment_templateRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessment_templateRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessment_templateService.deleteById(1));
            verifyNoMoreInteractions(assessment_templateRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment_template when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessment_templateRepository.existsById(1)).thenReturn(true);
            when(assessment_templateRepository.save(assessment_template1)).thenReturn(assessment_template1);

            Assessment_template result = assessment_templateService.update(assessment_template1);

            assertEquals(1, result.getId());
            verify(assessment_templateRepository).existsById(1);
            verify(assessment_templateRepository).save(assessment_template1);
            verifyNoMoreInteractions(assessment_templateRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_template does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessment_templateRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessment_templateService.update(assessment_template1));
            verify(assessment_templateRepository).existsById(1);
            verifyNoMoreInteractions(assessment_templateRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessment_templateRepository.existsById(1)).thenReturn(true);
            when(assessment_templateRepository.save(assessment_template1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessment_templateService.update(assessment_template1));
            verify(assessment_templateRepository).existsById(1);
            verify(assessment_templateRepository).save(assessment_template1);
            verifyNoMoreInteractions(assessment_templateRepository);
        }
    }
}