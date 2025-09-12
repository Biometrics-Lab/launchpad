package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Session_resourceRepository;
import com.biolab.launchpad.internal.repository.model.Session_resource;
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
@DisplayName("Session_resourceService Unit Tests")
class Session_resourceServiceTest {

    @Mock
    private Session_resourceRepository session_resourceRepository;

    @InjectMocks
    private Session_resourceService session_resourceService;

    private Session_resource session_resource1Input;
    private Session_resource session_resource1;
    private Session_resource session_resource2;

    @BeforeEach
    void setUp() {
        session_resource1Input = Session_resource.builder()
                .build();

        session_resource1 = Session_resource.builder()
                .id(1)
                .build();

        session_resource2 = Session_resource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(session_resourceRepository.save(session_resource1Input)).thenReturn(session_resource1);

            Session_resource result = session_resourceService.create(session_resource1Input);

            assertThat(result).isSameAs(session_resource1);
            verify(session_resourceRepository).save(session_resource1Input);
            verifyNoMoreInteractions(session_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(session_resourceRepository.save(session_resource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> session_resourceService.create(session_resource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(session_resourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all session_resources from repository")
        void findAll_returns_list_from_repo() {
            when(session_resourceRepository.findAll()).thenReturn(List.of(session_resource1, session_resource2));

            List<Session_resource> all = session_resourceService.findAll();

            assertThat(all).containsExactly(session_resource1, session_resource2);
            verify(session_resourceRepository).findAll();
            verifyNoMoreInteractions(session_resourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return session_resource when found")
        void findById_ok_returns_optional() {
            when(session_resourceRepository.findById(1)).thenReturn(Optional.of(session_resource1));

            Optional<Session_resource> result = session_resourceService.findById(1);

            assertThat(result).contains(session_resource1);
            verify(session_resourceRepository).findById(1);
            verifyNoMoreInteractions(session_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(session_resourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> session_resourceService.findById(1));
            verifyNoMoreInteractions(session_resourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete session_resource when it exists")
        void deleteById_when_exists_deletes() {
            when(session_resourceRepository.existsById(1)).thenReturn(true);

            session_resourceService.deleteById(1);

            verify(session_resourceRepository).existsById(1);
            verify(session_resourceRepository).deleteById(1);
            verifyNoMoreInteractions(session_resourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_resource does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(session_resourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> session_resourceService.deleteById(1));
            verify(session_resourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(session_resourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(session_resourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(session_resourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> session_resourceService.deleteById(1));
            verifyNoMoreInteractions(session_resourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return session_resource when it exists")
        void update_when_exists_saves_and_returns() {
            when(session_resourceRepository.existsById(1)).thenReturn(true);
            when(session_resourceRepository.save(session_resource1)).thenReturn(session_resource1);

            Session_resource result = session_resourceService.update(session_resource1);

            assertEquals(1, result.getId().intValue());
            verify(session_resourceRepository).existsById(1);
            verify(session_resourceRepository).save(session_resource1);
            verifyNoMoreInteractions(session_resourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session_resource does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(session_resourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> session_resourceService.update(session_resource1));
            verify(session_resourceRepository).existsById(1);
            verifyNoMoreInteractions(session_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(session_resourceRepository.existsById(1)).thenReturn(true);
            when(session_resourceRepository.save(session_resource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> session_resourceService.update(session_resource1));
            verify(session_resourceRepository).existsById(1);
            verify(session_resourceRepository).save(session_resource1);
            verifyNoMoreInteractions(session_resourceRepository);
        }
    }
}