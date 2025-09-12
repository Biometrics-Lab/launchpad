package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.User_playerRepository;
import com.biolab.launchpad.internal.repository.model.User_player;
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
@DisplayName("User_playerService Unit Tests")
class User_playerServiceTest {

    @Mock
    private User_playerRepository user_playerRepository;

    @InjectMocks
    private User_playerService user_playerService;

    private User_player user_player1Input;
    private User_player user_player1;
    private User_player user_player2;

    @BeforeEach
    void setUp() {
        user_player1Input = User_player.builder()
                .build();

        user_player1 = User_player.builder()
                .id(1)
                .build();

        user_player2 = User_player.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(user_playerRepository.save(user_player1Input)).thenReturn(user_player1);

            User_player result = user_playerService.create(user_player1Input);

            assertThat(result).isSameAs(user_player1);
            verify(user_playerRepository).save(user_player1Input);
            verifyNoMoreInteractions(user_playerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(user_playerRepository.save(user_player1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> user_playerService.create(user_player1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(user_playerRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all user_players from repository")
        void findAll_returns_list_from_repo() {
            when(user_playerRepository.findAll()).thenReturn(List.of(user_player1, user_player2));

            List<User_player> all = user_playerService.findAll();

            assertThat(all).containsExactly(user_player1, user_player2);
            verify(user_playerRepository).findAll();
            verifyNoMoreInteractions(user_playerRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return user_player when found")
        void findById_ok_returns_optional() {
            when(user_playerRepository.findById(1)).thenReturn(Optional.of(user_player1));

            Optional<User_player> result = user_playerService.findById(1);

            assertThat(result).contains(user_player1);
            verify(user_playerRepository).findById(1);
            verifyNoMoreInteractions(user_playerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(user_playerRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> user_playerService.findById(1));
            verifyNoMoreInteractions(user_playerRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete user_player when it exists")
        void deleteById_when_exists_deletes() {
            when(user_playerRepository.existsById(1)).thenReturn(true);

            user_playerService.deleteById(1);

            verify(user_playerRepository).existsById(1);
            verify(user_playerRepository).deleteById(1);
            verifyNoMoreInteractions(user_playerRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_player does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(user_playerRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> user_playerService.deleteById(1));
            verify(user_playerRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(user_playerRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(user_playerRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(user_playerRepository).deleteById(1);

            assertThrows(PersistException.class, () -> user_playerService.deleteById(1));
            verifyNoMoreInteractions(user_playerRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return user_player when it exists")
        void update_when_exists_saves_and_returns() {
            when(user_playerRepository.existsById(1)).thenReturn(true);
            when(user_playerRepository.save(user_player1)).thenReturn(user_player1);

            User_player result = user_playerService.update(user_player1);

            assertEquals(1, result.getId().intValue());
            verify(user_playerRepository).existsById(1);
            verify(user_playerRepository).save(user_player1);
            verifyNoMoreInteractions(user_playerRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_player does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(user_playerRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> user_playerService.update(user_player1));
            verify(user_playerRepository).existsById(1);
            verifyNoMoreInteractions(user_playerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(user_playerRepository.existsById(1)).thenReturn(true);
            when(user_playerRepository.save(user_player1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> user_playerService.update(user_player1));
            verify(user_playerRepository).existsById(1);
            verify(user_playerRepository).save(user_player1);
            verifyNoMoreInteractions(user_playerRepository);
        }
    }
}