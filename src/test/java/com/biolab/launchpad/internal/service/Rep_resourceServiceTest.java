package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Rep_resourceRepository;
import com.biolab.launchpad.internal.repository.model.Rep_resource;
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
@DisplayName("Rep_resourceService Unit Tests")
class Rep_resourceServiceTest {

    @Mock
    private Rep_resourceRepository rep_resourceRepository;

    @InjectMocks
    private Rep_resourceService rep_resourceService;

    private Rep_resource rep_resource1Input;
    private Rep_resource rep_resource1;
    private Rep_resource rep_resource2;

    @BeforeEach
    void setUp() {
        rep_resource1Input = Rep_resource.builder()
                .build();

        rep_resource1 = Rep_resource.builder()
                .id(1)
                .build();

        rep_resource2 = Rep_resource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(rep_resourceRepository.save(rep_resource1Input)).thenReturn(rep_resource1);

            Rep_resource result = rep_resourceService.create(rep_resource1Input);

            assertThat(result).isSameAs(rep_resource1);
            verify(rep_resourceRepository).save(rep_resource1Input);
            verifyNoMoreInteractions(rep_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(rep_resourceRepository.save(rep_resource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> rep_resourceService.create(rep_resource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(rep_resourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all rep_resources from repository")
        void findAll_returns_list_from_repo() {
            when(rep_resourceRepository.findAll()).thenReturn(List.of(rep_resource1, rep_resource2));

            List<Rep_resource> all = rep_resourceService.findAll();

            assertThat(all).containsExactly(rep_resource1, rep_resource2);
            verify(rep_resourceRepository).findAll();
            verifyNoMoreInteractions(rep_resourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep_resource when found")
        void findById_ok_returns_optional() {
            when(rep_resourceRepository.findById(1)).thenReturn(Optional.of(rep_resource1));

            Optional<Rep_resource> result = rep_resourceService.findById(1);

            assertThat(result).contains(rep_resource1);
            verify(rep_resourceRepository).findById(1);
            verifyNoMoreInteractions(rep_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(rep_resourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> rep_resourceService.findById(1));
            verifyNoMoreInteractions(rep_resourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep_resource when it exists")
        void deleteById_when_exists_deletes() {
            when(rep_resourceRepository.existsById(1)).thenReturn(true);

            rep_resourceService.deleteById(1);

            verify(rep_resourceRepository).existsById(1);
            verify(rep_resourceRepository).deleteById(1);
            verifyNoMoreInteractions(rep_resourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_resource does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(rep_resourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> rep_resourceService.deleteById(1));
            verify(rep_resourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(rep_resourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(rep_resourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(rep_resourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> rep_resourceService.deleteById(1));
            verifyNoMoreInteractions(rep_resourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep_resource when it exists")
        void update_when_exists_saves_and_returns() {
            when(rep_resourceRepository.existsById(1)).thenReturn(true);
            when(rep_resourceRepository.save(rep_resource1)).thenReturn(rep_resource1);

            Rep_resource result = rep_resourceService.update(rep_resource1);

            assertEquals(1, result.getId().intValue());
            verify(rep_resourceRepository).existsById(1);
            verify(rep_resourceRepository).save(rep_resource1);
            verifyNoMoreInteractions(rep_resourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_resource does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(rep_resourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> rep_resourceService.update(rep_resource1));
            verify(rep_resourceRepository).existsById(1);
            verifyNoMoreInteractions(rep_resourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(rep_resourceRepository.existsById(1)).thenReturn(true);
            when(rep_resourceRepository.save(rep_resource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> rep_resourceService.update(rep_resource1));
            verify(rep_resourceRepository).existsById(1);
            verify(rep_resourceRepository).save(rep_resource1);
            verifyNoMoreInteractions(rep_resourceRepository);
        }
    }
}