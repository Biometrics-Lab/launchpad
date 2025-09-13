package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.UserRoleDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.UserRoleDictionary;
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
class UserRoleDictionaryServiceTest {

    @Mock
    private UserRoleDictionaryRepository userRoleDictionaryRepository;

    @InjectMocks
    private UserRoleDictionaryService userRoleDictionaryService;

    private UserRoleDictionary userRoleDictionary1Input;
    private UserRoleDictionary userRoleDictionary1;
    private UserRoleDictionary userRoleDictionary2;

    @BeforeEach
    void setUp() {
        userRoleDictionary1Input = UserRoleDictionary.builder()
                .name("group1")
                .build();

        userRoleDictionary1 = UserRoleDictionary.builder()
                .name("group1")
                .build();

        userRoleDictionary2 = UserRoleDictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(userRoleDictionaryRepository.save(userRoleDictionary1Input)).thenReturn(userRoleDictionary1);

            UserRoleDictionary result = userRoleDictionaryService.create(userRoleDictionary1Input);

            assertThat(result).isSameAs(userRoleDictionary1);
            verify(userRoleDictionaryRepository).existsById(String.valueOf(userRoleDictionary1Input.getName()));
            verify(userRoleDictionaryRepository).save(userRoleDictionary1Input);
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(userRoleDictionaryRepository.save(userRoleDictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> userRoleDictionaryService.create(userRoleDictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(userRoleDictionaryRepository).existsById(String.valueOf(userRoleDictionary1Input.getName()));
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all user_role_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(userRoleDictionaryRepository.findAll()).thenReturn(List.of(userRoleDictionary1, userRoleDictionary2));

            List<UserRoleDictionary> all = userRoleDictionaryService.findAll();

            assertThat(all).containsExactly(userRoleDictionary1, userRoleDictionary2);
            verify(userRoleDictionaryRepository).findAll();
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return user_role_dictionary when found")
        void findById_ok_returns_optional() {
            when(userRoleDictionaryRepository.findById("group1")).thenReturn(Optional.of(userRoleDictionary1));

            Optional<UserRoleDictionary> result = userRoleDictionaryService.findById("group1");

            assertThat(result).contains(userRoleDictionary1);
            verify(userRoleDictionaryRepository).findById("group1");
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(userRoleDictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> userRoleDictionaryService.findById("group1"));
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete user_role_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(userRoleDictionaryRepository.existsById("group1")).thenReturn(true);

            userRoleDictionaryService.deleteById("group1");

            verify(userRoleDictionaryRepository).existsById("group1");
            verify(userRoleDictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_role_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(userRoleDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> userRoleDictionaryService.deleteById("group1"));
            verify(userRoleDictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(userRoleDictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(userRoleDictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> userRoleDictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return user_role_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(userRoleDictionaryRepository.existsById("group1")).thenReturn(true);
            when(userRoleDictionaryRepository.save(userRoleDictionary1)).thenReturn(userRoleDictionary1);

            UserRoleDictionary result = userRoleDictionaryService.update(userRoleDictionary1);

            assertEquals("group1", result.getId());
            verify(userRoleDictionaryRepository).existsById("group1");
            verify(userRoleDictionaryRepository).save(userRoleDictionary1);
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_role_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(userRoleDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> userRoleDictionaryService.update(userRoleDictionary1));
            verify(userRoleDictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(userRoleDictionaryRepository.existsById("group1")).thenReturn(true);
            when(userRoleDictionaryRepository.save(userRoleDictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> userRoleDictionaryService.update(userRoleDictionary1));
            verify(userRoleDictionaryRepository).existsById("group1");
            verify(userRoleDictionaryRepository).save(userRoleDictionary1);
            verifyNoMoreInteractions(userRoleDictionaryRepository);
        }
    }
}