package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ResourceTypeDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
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
@DisplayName("Resource_type_dictionaryService Unit Tests")
class ResourceTypeDictionaryServiceTest {

    @Mock
    private ResourceTypeDictionaryRepository resourceTypeDictionaryRepository;

    @InjectMocks
    private ResourceTypeDictionaryService resourceTypeDictionaryService;

    private ResourceTypeDictionary resourceTypeDictionary1Input;
    private ResourceTypeDictionary resourceTypeDictionary1;
    private ResourceTypeDictionary resourceTypeDictionary2;

    @BeforeEach
    void setUp() {
        resourceTypeDictionary1Input = ResourceTypeDictionary.builder()
                .name("group1")
                .build();

        resourceTypeDictionary1 = ResourceTypeDictionary.builder()
                .name("group1")
                .build();

        resourceTypeDictionary2 = ResourceTypeDictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(resourceTypeDictionaryRepository.save(resourceTypeDictionary1Input)).thenReturn(resourceTypeDictionary1);

            ResourceTypeDictionary result = resourceTypeDictionaryService.create(resourceTypeDictionary1Input);

            assertThat(result).isSameAs(resourceTypeDictionary1);
            verify(resourceTypeDictionaryRepository).existsById(String.valueOf(resourceTypeDictionary1Input.getName()));
            verify(resourceTypeDictionaryRepository).save(resourceTypeDictionary1Input);
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(resourceTypeDictionaryRepository.save(resourceTypeDictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> resourceTypeDictionaryService.create(resourceTypeDictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(resourceTypeDictionaryRepository).existsById(String.valueOf(resourceTypeDictionary1Input.getName()));
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all resource_type_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(resourceTypeDictionaryRepository.findAll()).thenReturn(List.of(resourceTypeDictionary1, resourceTypeDictionary2));

            List<ResourceTypeDictionary> all = resourceTypeDictionaryService.findAll();

            assertThat(all).containsExactly(resourceTypeDictionary1, resourceTypeDictionary2);
            verify(resourceTypeDictionaryRepository).findAll();
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return resource_type_dictionary when found")
        void findById_ok_returns_optional() {
            when(resourceTypeDictionaryRepository.findById("group1")).thenReturn(Optional.of(resourceTypeDictionary1));

            Optional<ResourceTypeDictionary> result = resourceTypeDictionaryService.findById("group1");

            assertThat(result).contains(resourceTypeDictionary1);
            verify(resourceTypeDictionaryRepository).findById("group1");
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(resourceTypeDictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> resourceTypeDictionaryService.findById("group1"));
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete resource_type_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(resourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);

            resourceTypeDictionaryService.deleteById("group1");

            verify(resourceTypeDictionaryRepository).existsById("group1");
            verify(resourceTypeDictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when resource_type_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(resourceTypeDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> resourceTypeDictionaryService.deleteById("group1"));
            verify(resourceTypeDictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(resourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(resourceTypeDictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> resourceTypeDictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return resource_type_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(resourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);
            when(resourceTypeDictionaryRepository.save(resourceTypeDictionary1)).thenReturn(resourceTypeDictionary1);

            ResourceTypeDictionary result = resourceTypeDictionaryService.update(resourceTypeDictionary1);

            assertEquals("group1", result.getId());
            verify(resourceTypeDictionaryRepository).existsById("group1");
            verify(resourceTypeDictionaryRepository).save(resourceTypeDictionary1);
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when resource_type_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(resourceTypeDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> resourceTypeDictionaryService.update(resourceTypeDictionary1));
            verify(resourceTypeDictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(resourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);
            when(resourceTypeDictionaryRepository.save(resourceTypeDictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> resourceTypeDictionaryService.update(resourceTypeDictionary1));
            verify(resourceTypeDictionaryRepository).existsById("group1");
            verify(resourceTypeDictionaryRepository).save(resourceTypeDictionary1);
            verifyNoMoreInteractions(resourceTypeDictionaryRepository);
        }
    }
}