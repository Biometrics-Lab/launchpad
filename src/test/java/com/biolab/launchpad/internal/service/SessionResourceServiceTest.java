package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.SessionResourceRepository;
import com.biolab.launchpad.internal.repository.model.SessionResource;
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
@DisplayName("SessionResourceService Unit Tests")
class SessionResourceServiceTest {

    @Mock
    private SessionResourceRepository sessionResourceRepository;

    @InjectMocks
    private SessionResourceService sessionResourceService;

    private SessionResource sessionResource1Input;
    private SessionResource sessionResource1;
    private SessionResource sessionResource2;

    @BeforeEach
    void setUp() {
        sessionResource1Input = SessionResource.builder()
                .build();

        sessionResource1 = SessionResource.builder()
                .id(1)
                .build();

        sessionResource2 = SessionResource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(sessionResourceRepository.save(sessionResource1Input)).thenReturn(sessionResource1);

            SessionResource result = sessionResourceService.create(sessionResource1Input);

            assertThat(result).isSameAs(sessionResource1);
            verify(sessionResourceRepository).save(sessionResource1Input);
            verifyNoMoreInteractions(sessionResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(sessionResourceRepository.save(sessionResource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> sessionResourceService.create(sessionResource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(sessionResourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all session_resources from repository")
        void findAll_returns_list_from_repo() {
            when(sessionResourceRepository.findAll()).thenReturn(List.of(sessionResource1, sessionResource2));

            List<SessionResource> all = sessionResourceService.findAll();

            assertThat(all).containsExactly(sessionResource1, sessionResource2);
            verify(sessionResourceRepository).findAll();
            verifyNoMoreInteractions(sessionResourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return session_resource when found")
        void findById_ok_returns_optional() {
            when(sessionResourceRepository.findById(1)).thenReturn(Optional.of(sessionResource1));

            Optional<SessionResource> result = sessionResourceService.findById(1);

            assertThat(result).contains(sessionResource1);
            verify(sessionResourceRepository).findById(1);
            verifyNoMoreInteractions(sessionResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(sessionResourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> sessionResourceService.findById(1));
            verifyNoMoreInteractions(sessionResourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete session_resource when it exists")
        void deleteById_when_exists_deletes() {
            when(sessionResourceRepository.existsById(1)).thenReturn(true);

            sessionResourceService.deleteById(1);

            verify(sessionResourceRepository).existsById(1);
            verify(sessionResourceRepository).deleteById(1);
            verifyNoMoreInteractions(sessionResourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_resource does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(sessionResourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sessionResourceService.deleteById(1));
            verify(sessionResourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(sessionResourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(sessionResourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(sessionResourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> sessionResourceService.deleteById(1));
            verifyNoMoreInteractions(sessionResourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return session_resource when it exists")
        void update_when_exists_saves_and_returns() {
            when(sessionResourceRepository.existsById(1)).thenReturn(true);
            when(sessionResourceRepository.save(sessionResource1)).thenReturn(sessionResource1);

            SessionResource result = sessionResourceService.update(sessionResource1);

            assertEquals(1, result.getId().intValue());
            verify(sessionResourceRepository).existsById(1);
            verify(sessionResourceRepository).save(sessionResource1);
            verifyNoMoreInteractions(sessionResourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_resource does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(sessionResourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sessionResourceService.update(sessionResource1));
            verify(sessionResourceRepository).existsById(1);
            verifyNoMoreInteractions(sessionResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(sessionResourceRepository.existsById(1)).thenReturn(true);
            when(sessionResourceRepository.save(sessionResource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> sessionResourceService.update(sessionResource1));
            verify(sessionResourceRepository).existsById(1);
            verify(sessionResourceRepository).save(sessionResource1);
            verifyNoMoreInteractions(sessionResourceRepository);
        }
    }
}