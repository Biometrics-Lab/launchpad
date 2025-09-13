package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.RepResource;
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
class RepResourceServiceTest {

    @Mock
    private RepResourceRepository repResourceRepository;

    @InjectMocks
    private RepResourceService repResourceService;

    private RepResource repResource1Input;
    private RepResource repResource1;
    private RepResource repResource2;

    @BeforeEach
    void setUp() {
        repResource1Input = RepResource.builder()
                .build();

        repResource1 = RepResource.builder()
                .id(1)
                .build();

        repResource2 = RepResource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(repResourceRepository.save(repResource1Input)).thenReturn(repResource1);

            RepResource result = repResourceService.create(repResource1Input);

            assertThat(result).isSameAs(repResource1);
            verify(repResourceRepository).save(repResource1Input);
            verifyNoMoreInteractions(repResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(repResourceRepository.save(repResource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> repResourceService.create(repResource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(repResourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all rep_resources from repository")
        void findAll_returns_list_from_repo() {
            when(repResourceRepository.findAll()).thenReturn(List.of(repResource1, repResource2));

            List<RepResource> all = repResourceService.findAll();

            assertThat(all).containsExactly(repResource1, repResource2);
            verify(repResourceRepository).findAll();
            verifyNoMoreInteractions(repResourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep_resource when found")
        void findById_ok_returns_optional() {
            when(repResourceRepository.findById(1)).thenReturn(Optional.of(repResource1));

            Optional<RepResource> result = repResourceService.findById(1);

            assertThat(result).contains(repResource1);
            verify(repResourceRepository).findById(1);
            verifyNoMoreInteractions(repResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(repResourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> repResourceService.findById(1));
            verifyNoMoreInteractions(repResourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep_resource when it exists")
        void deleteById_when_exists_deletes() {
            when(repResourceRepository.existsById(1)).thenReturn(true);

            repResourceService.deleteById(1);

            verify(repResourceRepository).existsById(1);
            verify(repResourceRepository).deleteById(1);
            verifyNoMoreInteractions(repResourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_resource does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(repResourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repResourceService.deleteById(1));
            verify(repResourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(repResourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(repResourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(repResourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> repResourceService.deleteById(1));
            verifyNoMoreInteractions(repResourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep_resource when it exists")
        void update_when_exists_saves_and_returns() {
            when(repResourceRepository.existsById(1)).thenReturn(true);
            when(repResourceRepository.save(repResource1)).thenReturn(repResource1);

            RepResource result = repResourceService.update(repResource1);

            assertEquals(1, result.getId().intValue());
            verify(repResourceRepository).existsById(1);
            verify(repResourceRepository).save(repResource1);
            verifyNoMoreInteractions(repResourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_resource does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(repResourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repResourceService.update(repResource1));
            verify(repResourceRepository).existsById(1);
            verifyNoMoreInteractions(repResourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(repResourceRepository.existsById(1)).thenReturn(true);
            when(repResourceRepository.save(repResource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> repResourceService.update(repResource1));
            verify(repResourceRepository).existsById(1);
            verify(repResourceRepository).save(repResource1);
            verifyNoMoreInteractions(repResourceRepository);
        }
    }
}