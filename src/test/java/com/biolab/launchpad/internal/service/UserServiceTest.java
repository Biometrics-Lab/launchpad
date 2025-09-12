package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.UserRepository;
import com.biolab.launchpad.internal.repository.model.User;
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
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1Input;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1Input = User.builder()
                .name("user")
                .build();

        user1 = User.builder()
                .id(1)
                .name("user")
                .build();

        user2 = User.builder()
                .id(2)
                .name("bed_user")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(userRepository.save(user1Input)).thenReturn(user1);

            User result = userService.create(user1Input);

            assertThat(result).isSameAs(user1);
            verify(userRepository).save(user1Input);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(userRepository.save(user1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> userService.create(user1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all users from repository")
        void findAll_returns_list_from_repo() {
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));

            List<User> all = userService.findAll();

            assertThat(all).containsExactly(user1, user2);
            verify(userRepository).findAll();
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return user when found")
        void findById_ok_returns_optional() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user1));

            Optional<User> result = userService.findById(1);

            assertThat(result).contains(user1);
            verify(userRepository).findById(1);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(userRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> userService.findById(1));
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete user when it exists")
        void deleteById_when_exists_deletes() {
            when(userRepository.existsById(1)).thenReturn(true);

            userService.deleteById(1);

            verify(userRepository).existsById(1);
            verify(userRepository).deleteById(1);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(userRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> userService.deleteById(1));
            verify(userRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(userRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(userRepository).deleteById(1);

            assertThrows(PersistException.class, () -> userService.deleteById(1));
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return user when it exists")
        void update_when_exists_saves_and_returns() {
            when(userRepository.existsById(1)).thenReturn(true);
            when(userRepository.save(user1)).thenReturn(user1);

            User result = userService.update(user1);

            assertEquals(1, result.getId());
            verify(userRepository).existsById(1);
            verify(userRepository).save(user1);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(userRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> userService.update(user1));
            verify(userRepository).existsById(1);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(userRepository.existsById(1)).thenReturn(true);
            when(userRepository.save(user1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> userService.update(user1));
            verify(userRepository).existsById(1);
            verify(userRepository).save(user1);
            verifyNoMoreInteractions(userRepository);
        }
    }
}