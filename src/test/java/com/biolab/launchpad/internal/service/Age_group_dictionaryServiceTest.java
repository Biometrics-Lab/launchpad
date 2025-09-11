package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Age_group_dictionaryRepository;
import com.biolab.launchpad.internal.repository.model.Age_group_dictionary;
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
@DisplayName("Age_group_dictionaryService Unit Tests")
class Age_group_dictionaryServiceTest {

    @Mock
    private Age_group_dictionaryRepository age_group_dictionaryRepository;

    @InjectMocks
    private Age_group_dictionaryService age_group_dictionaryService;

    private Age_group_dictionary age_group_dictionary1Input;
    private Age_group_dictionary age_group_dictionary1;
    private Age_group_dictionary age_group_dictionary2;

    @BeforeEach
    void setUp() {
        age_group_dictionary1Input = Age_group_dictionary.builder()
                .name("group1")
                .build();

        age_group_dictionary1 = Age_group_dictionary.builder()
                .name("group1")
                .build();

        age_group_dictionary2 = Age_group_dictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(age_group_dictionaryRepository.save(age_group_dictionary1Input)).thenReturn(age_group_dictionary1);

            Age_group_dictionary result = age_group_dictionaryService.create(age_group_dictionary1Input);

            assertThat(result).isSameAs(age_group_dictionary1);
            verify(age_group_dictionaryRepository).existsById(String.valueOf(age_group_dictionary1Input.getName()));
            verify(age_group_dictionaryRepository).save(age_group_dictionary1Input);
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(age_group_dictionaryRepository.save(age_group_dictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> age_group_dictionaryService.create(age_group_dictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(age_group_dictionaryRepository).existsById(String.valueOf(age_group_dictionary1Input.getName()));
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all age_group_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(age_group_dictionaryRepository.findAll()).thenReturn(List.of(age_group_dictionary1, age_group_dictionary2));

            List<Age_group_dictionary> all = age_group_dictionaryService.findAll();

            assertThat(all).containsExactly(age_group_dictionary1, age_group_dictionary2);
            verify(age_group_dictionaryRepository).findAll();
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return age_group_dictionary when found")
        void findById_ok_returns_optional() {
            when(age_group_dictionaryRepository.findById("group1")).thenReturn(Optional.of(age_group_dictionary1));

            Optional<Age_group_dictionary> result = age_group_dictionaryService.findById("group1");

            assertThat(result).contains(age_group_dictionary1);
            verify(age_group_dictionaryRepository).findById("group1");
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(age_group_dictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> age_group_dictionaryService.findById("group1"));
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete age_group_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(age_group_dictionaryRepository.existsById("group1")).thenReturn(true);

            age_group_dictionaryService.deleteById("group1");

            verify(age_group_dictionaryRepository).existsById("group1");
            verify(age_group_dictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when age_group_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(age_group_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> age_group_dictionaryService.deleteById("group1"));
            verify(age_group_dictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(age_group_dictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(age_group_dictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> age_group_dictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return age_group_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(age_group_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(age_group_dictionaryRepository.save(age_group_dictionary1)).thenReturn(age_group_dictionary1);

            Age_group_dictionary result = age_group_dictionaryService.update(age_group_dictionary1);

            assertEquals("group1", result.getId());
            verify(age_group_dictionaryRepository).existsById("group1");
            verify(age_group_dictionaryRepository).save(age_group_dictionary1);
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when age_group_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(age_group_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> age_group_dictionaryService.update(age_group_dictionary1));
            verify(age_group_dictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(age_group_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(age_group_dictionaryRepository.save(age_group_dictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> age_group_dictionaryService.update(age_group_dictionary1));
            verify(age_group_dictionaryRepository).existsById("group1");
            verify(age_group_dictionaryRepository).save(age_group_dictionary1);
            verifyNoMoreInteractions(age_group_dictionaryRepository);
        }
    }
}