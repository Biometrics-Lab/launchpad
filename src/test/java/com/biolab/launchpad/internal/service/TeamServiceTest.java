package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.TeamRepository;
import com.biolab.launchpad.internal.repository.model.Team;
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
@DisplayName("TeamService Unit Tests")
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team1Input;
    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        team1Input = Team.builder()
                .name("team")
                .build();

        team1 = Team.builder()
                .id(1)
                .name("team")
                .build();

        team2 = Team.builder()
                .id(2)
                .name("cool_team")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(teamRepository.save(team1Input)).thenReturn(team1);

            Team result = teamService.create(team1Input);

            assertThat(result).isSameAs(team1);
            verify(teamRepository).save(team1Input);
            verifyNoMoreInteractions(teamRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(teamRepository.save(team1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> teamService.create(team1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(teamRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all teams from repository")
        void findAll_returns_list_from_repo() {
            when(teamRepository.findAll()).thenReturn(List.of(team1, team2));

            List<Team> all = teamService.findAll();

            assertThat(all).containsExactly(team1, team2);
            verify(teamRepository).findAll();
            verifyNoMoreInteractions(teamRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return team when found")
        void findById_ok_returns_optional() {
            when(teamRepository.findById(1)).thenReturn(Optional.of(team1));

            Optional<Team> result = teamService.findById(1);

            assertThat(result).contains(team1);
            verify(teamRepository).findById(1);
            verifyNoMoreInteractions(teamRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(teamRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> teamService.findById(1));
            verifyNoMoreInteractions(teamRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete team when it exists")
        void deleteById_when_exists_deletes() {
            when(teamRepository.existsById(1)).thenReturn(true);

            teamService.deleteById(1);

            verify(teamRepository).existsById(1);
            verify(teamRepository).deleteById(1);
            verifyNoMoreInteractions(teamRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when team does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(teamRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> teamService.deleteById(1));
            verify(teamRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(teamRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(teamRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(teamRepository).deleteById(1);

            assertThrows(PersistException.class, () -> teamService.deleteById(1));
            verifyNoMoreInteractions(teamRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return team when it exists")
        void update_when_exists_saves_and_returns() {
            when(teamRepository.existsById(1)).thenReturn(true);
            when(teamRepository.save(team1)).thenReturn(team1);

            Team result = teamService.update(team1);

            assertEquals(1, result.getId());
            verify(teamRepository).existsById(1);
            verify(teamRepository).save(team1);
            verifyNoMoreInteractions(teamRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when team does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(teamRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> teamService.update(team1));
            verify(teamRepository).existsById(1);
            verifyNoMoreInteractions(teamRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(teamRepository.existsById(1)).thenReturn(true);
            when(teamRepository.save(team1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> teamService.update(team1));
            verify(teamRepository).existsById(1);
            verify(teamRepository).save(team1);
            verifyNoMoreInteractions(teamRepository);
        }
    }
}