package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.UserPlayerRepository;
import com.biolab.launchpad.internal.repository.model.UserPlayer;
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
@DisplayName("UserPlayerService Unit Tests")
class UserPlayerServiceTest {

    @Mock
    private UserPlayerRepository userPlayerRepository;

    @InjectMocks
    private UserPlayerService userPlayerService;

    private UserPlayer userPlayer1Input;
    private UserPlayer userPlayer1;
    private UserPlayer userPlayer2;

    @BeforeEach
    void setUp() {
        userPlayer1Input = UserPlayer.builder()
                .build();

        userPlayer1 = UserPlayer.builder()
                .id(1)
                .build();

        userPlayer2 = UserPlayer.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(userPlayerRepository.save(userPlayer1Input)).thenReturn(userPlayer1);

            UserPlayer result = userPlayerService.create(userPlayer1Input);

            assertThat(result).isSameAs(userPlayer1);
            verify(userPlayerRepository).save(userPlayer1Input);
            verifyNoMoreInteractions(userPlayerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(userPlayerRepository.save(userPlayer1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> userPlayerService.create(userPlayer1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(userPlayerRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all user_players from repository")
        void findAll_returns_list_from_repo() {
            when(userPlayerRepository.findAll()).thenReturn(List.of(userPlayer1, userPlayer2));

            List<UserPlayer> all = userPlayerService.findAll();

            assertThat(all).containsExactly(userPlayer1, userPlayer2);
            verify(userPlayerRepository).findAll();
            verifyNoMoreInteractions(userPlayerRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return user_player when found")
        void findById_ok_returns_optional() {
            when(userPlayerRepository.findById(1)).thenReturn(Optional.of(userPlayer1));

            Optional<UserPlayer> result = userPlayerService.findById(1);

            assertThat(result).contains(userPlayer1);
            verify(userPlayerRepository).findById(1);
            verifyNoMoreInteractions(userPlayerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(userPlayerRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> userPlayerService.findById(1));
            verifyNoMoreInteractions(userPlayerRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete user_player when it exists")
        void deleteById_when_exists_deletes() {
            when(userPlayerRepository.existsById(1)).thenReturn(true);

            userPlayerService.deleteById(1);

            verify(userPlayerRepository).existsById(1);
            verify(userPlayerRepository).deleteById(1);
            verifyNoMoreInteractions(userPlayerRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_player does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(userPlayerRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> userPlayerService.deleteById(1));
            verify(userPlayerRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(userPlayerRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(userPlayerRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(userPlayerRepository).deleteById(1);

            assertThrows(PersistException.class, () -> userPlayerService.deleteById(1));
            verifyNoMoreInteractions(userPlayerRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return user_player when it exists")
        void update_when_exists_saves_and_returns() {
            when(userPlayerRepository.existsById(1)).thenReturn(true);
            when(userPlayerRepository.save(userPlayer1)).thenReturn(userPlayer1);

            UserPlayer result = userPlayerService.update(userPlayer1);

            assertEquals(1, result.getId().intValue());
            verify(userPlayerRepository).existsById(1);
            verify(userPlayerRepository).save(userPlayer1);
            verifyNoMoreInteractions(userPlayerRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when user_player does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(userPlayerRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> userPlayerService.update(userPlayer1));
            verify(userPlayerRepository).existsById(1);
            verifyNoMoreInteractions(userPlayerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(userPlayerRepository.existsById(1)).thenReturn(true);
            when(userPlayerRepository.save(userPlayer1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> userPlayerService.update(userPlayer1));
            verify(userPlayerRepository).existsById(1);
            verify(userPlayerRepository).save(userPlayer1);
            verifyNoMoreInteractions(userPlayerRepository);
        }
    }
}