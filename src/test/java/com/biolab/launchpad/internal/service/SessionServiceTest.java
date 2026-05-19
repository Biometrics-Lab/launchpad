package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Session1Repository;
import com.biolab.launchpad.internal.repository.model.Session;
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
@DisplayName("Session1Service Unit Tests")
class SessionServiceTest {

    @Mock
    private Session1Repository session1Repository;

    @InjectMocks
    private Session1Service session1Service;

    private Session session11Input;
    private Session session11;
    private Session session12;

    @BeforeEach
    void setUp() {
        session11Input = Session.builder()
                .build();

        session11 = Session.builder()
                .id(1)
                .build();

        session12 = Session.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(session1Repository.save(session11Input)).thenReturn(session11);

            Session result = session1Service.create(session11Input);

            assertThat(result).isSameAs(session11);
            verify(session1Repository).save(session11Input);
            verifyNoMoreInteractions(session1Repository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(session1Repository.save(session11Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> session1Service.create(session11Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(session1Repository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all session1s from repository")
        void findAll_returns_list_from_repo() {
            when(session1Repository.findAll()).thenReturn(List.of(session11, session12));

            List<Session> all = session1Service.findAll();

            assertThat(all).containsExactly(session11, session12);
            verify(session1Repository).findAll();
            verifyNoMoreInteractions(session1Repository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return session1 when found")
        void findById_ok_returns_optional() {
            when(session1Repository.findById(1)).thenReturn(Optional.of(session11));

            Optional<Session> result = session1Service.findById(1);

            assertThat(result).contains(session11);
            verify(session1Repository).findById(1);
            verifyNoMoreInteractions(session1Repository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(session1Repository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> session1Service.findById(1));
            verifyNoMoreInteractions(session1Repository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete session1 when it exists")
        void deleteById_when_exists_deletes() {
            when(session1Repository.existsById(1)).thenReturn(true);

            session1Service.deleteById(1);

            verify(session1Repository).existsById(1);
            verify(session1Repository).deleteById(1);
            verifyNoMoreInteractions(session1Repository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session1 does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(session1Repository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> session1Service.deleteById(1));
            verify(session1Repository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(session1Repository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(session1Repository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(session1Repository).deleteById(1);

            assertThrows(PersistException.class, () -> session1Service.deleteById(1));
            verifyNoMoreInteractions(session1Repository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return session1 when it exists")
        void update_when_exists_saves_and_returns() {
            when(session1Repository.existsById(1)).thenReturn(true);
            when(session1Repository.save(session11)).thenReturn(session11);

            Session result = session1Service.update(session11);

            assertEquals(1, result.getId().intValue());
            verify(session1Repository).existsById(1);
            verify(session1Repository).save(session11);
            verifyNoMoreInteractions(session1Repository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when session1 does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(session1Repository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> session1Service.update(session11));
            verify(session1Repository).existsById(1);
            verifyNoMoreInteractions(session1Repository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(session1Repository.existsById(1)).thenReturn(true);
            when(session1Repository.save(session11)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> session1Service.update(session11));
            verify(session1Repository).existsById(1);
            verify(session1Repository).save(session11);
            verifyNoMoreInteractions(session1Repository);
        }
    }
}