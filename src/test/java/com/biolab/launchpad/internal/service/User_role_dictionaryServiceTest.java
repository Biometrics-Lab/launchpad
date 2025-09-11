package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.User_role_dictionaryRepository;
import com.biolab.launchpad.internal.repository.model.User_role_dictionary;
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
@DisplayName("User_role_dictionaryService Unit Tests")
class User_role_dictionaryServiceTest {

    @Mock
    private User_role_dictionaryRepository user_role_dictionaryRepository;

    @InjectMocks
    private User_role_dictionaryService user_role_dictionaryService;

    private User_role_dictionary user_role_dictionary1Input;
    private User_role_dictionary user_role_dictionary1;
    private User_role_dictionary user_role_dictionary2;

    @BeforeEach
    void setUp() {
        user_role_dictionary1Input = User_role_dictionary.builder()
                .name("group1")
                .build();

        user_role_dictionary1 = User_role_dictionary.builder()
                .name("group1")
                .build();

        user_role_dictionary2 = User_role_dictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(user_role_dictionaryRepository.save(user_role_dictionary1Input)).thenReturn(user_role_dictionary1);

            User_role_dictionary result = user_role_dictionaryService.create(user_role_dictionary1Input);

            assertThat(result).isSameAs(user_role_dictionary1);
            verify(user_role_dictionaryRepository).existsById(String.valueOf(user_role_dictionary1Input.getName()));
            verify(user_role_dictionaryRepository).save(user_role_dictionary1Input);
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(user_role_dictionaryRepository.save(user_role_dictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> user_role_dictionaryService.create(user_role_dictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(user_role_dictionaryRepository).existsById(String.valueOf(user_role_dictionary1Input.getName()));
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all user_role_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(user_role_dictionaryRepository.findAll()).thenReturn(List.of(user_role_dictionary1, user_role_dictionary2));

            List<User_role_dictionary> all = user_role_dictionaryService.findAll();

            assertThat(all).containsExactly(user_role_dictionary1, user_role_dictionary2);
            verify(user_role_dictionaryRepository).findAll();
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return user_role_dictionary when found")
        void findById_ok_returns_optional() {
            when(user_role_dictionaryRepository.findById("group1")).thenReturn(Optional.of(user_role_dictionary1));

            Optional<User_role_dictionary> result = user_role_dictionaryService.findById("group1");

            assertThat(result).contains(user_role_dictionary1);
            verify(user_role_dictionaryRepository).findById("group1");
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(user_role_dictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> user_role_dictionaryService.findById("group1"));
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete user_role_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(user_role_dictionaryRepository.existsById("group1")).thenReturn(true);

            user_role_dictionaryService.deleteById("group1");

            verify(user_role_dictionaryRepository).existsById("group1");
            verify(user_role_dictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_role_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(user_role_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> user_role_dictionaryService.deleteById("group1"));
            verify(user_role_dictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(user_role_dictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(user_role_dictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> user_role_dictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return user_role_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(user_role_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(user_role_dictionaryRepository.save(user_role_dictionary1)).thenReturn(user_role_dictionary1);

            User_role_dictionary result = user_role_dictionaryService.update(user_role_dictionary1);

            assertEquals("group1", result.getId());
            verify(user_role_dictionaryRepository).existsById("group1");
            verify(user_role_dictionaryRepository).save(user_role_dictionary1);
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_role_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(user_role_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> user_role_dictionaryService.update(user_role_dictionary1));
            verify(user_role_dictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(user_role_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(user_role_dictionaryRepository.save(user_role_dictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> user_role_dictionaryService.update(user_role_dictionary1));
            verify(user_role_dictionaryRepository).existsById("group1");
            verify(user_role_dictionaryRepository).save(user_role_dictionary1);
            verifyNoMoreInteractions(user_role_dictionaryRepository);
        }
    }
}