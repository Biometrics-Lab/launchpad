package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.PlayerRepository;
import com.biolab.launchpad.internal.repository.model.Player;
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
@DisplayName("PlayerService Unit Tests")
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player player1Input;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1Input = Player.builder()
                .name("pl")
                .build();

        player1 = Player.builder()
                .id(1)
                .name("pl")
                .build();

        player2 = Player.builder()
                .id(2)
                .name("cool_pl")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(playerRepository.save(player1Input)).thenReturn(player1);

            Player result = playerService.create(player1Input);

            assertThat(result).isSameAs(player1);
            verify(playerRepository).save(player1Input);
            verifyNoMoreInteractions(playerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(playerRepository.save(player1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> playerService.create(player1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(playerRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all players from repository")
        void findAll_returns_list_from_repo() {
            when(playerRepository.findAll()).thenReturn(List.of(player1, player2));

            List<Player> all = playerService.findAll();

            assertThat(all).containsExactly(player1, player2);
            verify(playerRepository).findAll();
            verifyNoMoreInteractions(playerRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return player when found")
        void findById_ok_returns_optional() {
            when(playerRepository.findById(1)).thenReturn(Optional.of(player1));

            Optional<Player> result = playerService.findById(1);

            assertThat(result).contains(player1);
            verify(playerRepository).findById(1);
            verifyNoMoreInteractions(playerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(playerRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> playerService.findById(1));
            verifyNoMoreInteractions(playerRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete player when it exists")
        void deleteById_when_exists_deletes() {
            when(playerRepository.existsById(1)).thenReturn(true);

            playerService.deleteById(1);

            verify(playerRepository).existsById(1);
            verify(playerRepository).deleteById(1);
            verifyNoMoreInteractions(playerRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when player does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(playerRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> playerService.deleteById(1));
            verify(playerRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(playerRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(playerRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(playerRepository).deleteById(1);

            assertThrows(PersistException.class, () -> playerService.deleteById(1));
            verifyNoMoreInteractions(playerRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return player when it exists")
        void update_when_exists_saves_and_returns() {
            when(playerRepository.existsById(1)).thenReturn(true);
            when(playerRepository.save(player1)).thenReturn(player1);

            Player result = playerService.update(player1);

            assertEquals(1, result.getId());
            verify(playerRepository).existsById(1);
            verify(playerRepository).save(player1);
            verifyNoMoreInteractions(playerRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when player does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(playerRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> playerService.update(player1));
            verify(playerRepository).existsById(1);
            verifyNoMoreInteractions(playerRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(playerRepository.existsById(1)).thenReturn(true);
            when(playerRepository.save(player1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> playerService.update(player1));
            verify(playerRepository).existsById(1);
            verify(playerRepository).save(player1);
            verifyNoMoreInteractions(playerRepository);
        }
    }
}