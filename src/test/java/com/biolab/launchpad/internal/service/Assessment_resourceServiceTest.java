package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Assessment_resourceRepository;
import com.biolab.launchpad.internal.repository.model.Assessment_resource;
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
class Assessment_resourceServiceTest {

    @Mock
    private Assessment_resourceRepository assessment_resourceRepository;

    @InjectMocks
    private Assessment_resourceService assessment_resourceService;

    private Assessment_resource assessment_resource1Input;
    private Assessment_resource assessment_resource1;
    private Assessment_resource assessment_resource2;

    @BeforeEach
    void setUp() {
        assessment_resource1Input = Assessment_resource.builder()
                .build();

        assessment_resource1 = Assessment_resource.builder()
                .id(1)
                .build();

        assessment_resource2 = Assessment_resource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(assessment_resourceRepository.save(assessment_resource1Input)).thenReturn(assessment_resource1);

            Assessment_resource result = assessment_resourceService.create(assessment_resource1Input);

            assertThat(result).isSameAs(assessment_resource1);
            verify(assessment_resourceRepository).save(assessment_resource1Input);
            verifyNoMoreInteractions(assessment_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(assessment_resourceRepository.save(assessment_resource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> assessment_resourceService.create(assessment_resource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(assessment_resourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all assessment_resources from repository")
        void findAll_returns_list_from_repo() {
            when(assessment_resourceRepository.findAll()).thenReturn(List.of(assessment_resource1, assessment_resource2));

            List<Assessment_resource> all = assessment_resourceService.findAll();

            assertThat(all).containsExactly(assessment_resource1, assessment_resource2);
            verify(assessment_resourceRepository).findAll();
            verifyNoMoreInteractions(assessment_resourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return assessment_resource when found")
        void findById_ok_returns_optional() {
            when(assessment_resourceRepository.findById(1)).thenReturn(Optional.of(assessment_resource1));

            Optional<Assessment_resource> result = assessment_resourceService.findById(1);

            assertThat(result).contains(assessment_resource1);
            verify(assessment_resourceRepository).findById(1);
            verifyNoMoreInteractions(assessment_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(assessment_resourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> assessment_resourceService.findById(1));
            verifyNoMoreInteractions(assessment_resourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete assessment_resource when it exists")
        void deleteById_when_exists_deletes() {
            when(assessment_resourceRepository.existsById(1)).thenReturn(true);

            assessment_resourceService.deleteById(1);

            verify(assessment_resourceRepository).existsById(1);
            verify(assessment_resourceRepository).deleteById(1);
            verifyNoMoreInteractions(assessment_resourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_resource does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(assessment_resourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessment_resourceService.deleteById(1));
            verify(assessment_resourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(assessment_resourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(assessment_resourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(assessment_resourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> assessment_resourceService.deleteById(1));
            verifyNoMoreInteractions(assessment_resourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return assessment_resource when it exists")
        void update_when_exists_saves_and_returns() {
            when(assessment_resourceRepository.existsById(1)).thenReturn(true);
            when(assessment_resourceRepository.save(assessment_resource1)).thenReturn(assessment_resource1);

            Assessment_resource result = assessment_resourceService.update(assessment_resource1);

            assertEquals(1, result.getId().intValue());
            verify(assessment_resourceRepository).existsById(1);
            verify(assessment_resourceRepository).save(assessment_resource1);
            verifyNoMoreInteractions(assessment_resourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when assessment_resource does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(assessment_resourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> assessment_resourceService.update(assessment_resource1));
            verify(assessment_resourceRepository).existsById(1);
            verifyNoMoreInteractions(assessment_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(assessment_resourceRepository.existsById(1)).thenReturn(true);
            when(assessment_resourceRepository.save(assessment_resource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> assessment_resourceService.update(assessment_resource1));
            verify(assessment_resourceRepository).existsById(1);
            verify(assessment_resourceRepository).save(assessment_resource1);
            verifyNoMoreInteractions(assessment_resourceRepository);
        }
    }
}