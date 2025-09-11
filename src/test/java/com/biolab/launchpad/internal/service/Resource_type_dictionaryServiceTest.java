package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Resource_type_dictionaryRepository;
import com.biolab.launchpad.internal.repository.model.Resource_type_dictionary;
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
class Resource_type_dictionaryServiceTest {

    @Mock
    private Resource_type_dictionaryRepository resource_type_dictionaryRepository;

    @InjectMocks
    private Resource_type_dictionaryService resource_type_dictionaryService;

    private Resource_type_dictionary resource_type_dictionary1Input;
    private Resource_type_dictionary resource_type_dictionary1;
    private Resource_type_dictionary resource_type_dictionary2;

    @BeforeEach
    void setUp() {
        resource_type_dictionary1Input = Resource_type_dictionary.builder()
                .name("group1")
                .build();

        resource_type_dictionary1 = Resource_type_dictionary.builder()
                .name("group1")
                .build();

        resource_type_dictionary2 = Resource_type_dictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(resource_type_dictionaryRepository.save(resource_type_dictionary1Input)).thenReturn(resource_type_dictionary1);

            Resource_type_dictionary result = resource_type_dictionaryService.create(resource_type_dictionary1Input);

            assertThat(result).isSameAs(resource_type_dictionary1);
            verify(resource_type_dictionaryRepository).existsById(String.valueOf(resource_type_dictionary1Input.getName()));
            verify(resource_type_dictionaryRepository).save(resource_type_dictionary1Input);
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(resource_type_dictionaryRepository.save(resource_type_dictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> resource_type_dictionaryService.create(resource_type_dictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(resource_type_dictionaryRepository).existsById(String.valueOf(resource_type_dictionary1Input.getName()));
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all resource_type_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(resource_type_dictionaryRepository.findAll()).thenReturn(List.of(resource_type_dictionary1, resource_type_dictionary2));

            List<Resource_type_dictionary> all = resource_type_dictionaryService.findAll();

            assertThat(all).containsExactly(resource_type_dictionary1, resource_type_dictionary2);
            verify(resource_type_dictionaryRepository).findAll();
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return resource_type_dictionary when found")
        void findById_ok_returns_optional() {
            when(resource_type_dictionaryRepository.findById("group1")).thenReturn(Optional.of(resource_type_dictionary1));

            Optional<Resource_type_dictionary> result = resource_type_dictionaryService.findById("group1");

            assertThat(result).contains(resource_type_dictionary1);
            verify(resource_type_dictionaryRepository).findById("group1");
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(resource_type_dictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> resource_type_dictionaryService.findById("group1"));
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete resource_type_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(resource_type_dictionaryRepository.existsById("group1")).thenReturn(true);

            resource_type_dictionaryService.deleteById("group1");

            verify(resource_type_dictionaryRepository).existsById("group1");
            verify(resource_type_dictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when resource_type_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(resource_type_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> resource_type_dictionaryService.deleteById("group1"));
            verify(resource_type_dictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(resource_type_dictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(resource_type_dictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> resource_type_dictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return resource_type_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(resource_type_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(resource_type_dictionaryRepository.save(resource_type_dictionary1)).thenReturn(resource_type_dictionary1);

            Resource_type_dictionary result = resource_type_dictionaryService.update(resource_type_dictionary1);

            assertEquals("group1", result.getId());
            verify(resource_type_dictionaryRepository).existsById("group1");
            verify(resource_type_dictionaryRepository).save(resource_type_dictionary1);
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when resource_type_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(resource_type_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> resource_type_dictionaryService.update(resource_type_dictionary1));
            verify(resource_type_dictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(resource_type_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(resource_type_dictionaryRepository.save(resource_type_dictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> resource_type_dictionaryService.update(resource_type_dictionary1));
            verify(resource_type_dictionaryRepository).existsById("group1");
            verify(resource_type_dictionaryRepository).save(resource_type_dictionary1);
            verifyNoMoreInteractions(resource_type_dictionaryRepository);
        }
    }
}