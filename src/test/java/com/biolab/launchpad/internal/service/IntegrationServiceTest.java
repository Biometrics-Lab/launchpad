package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.IntegrationRepository;
import com.biolab.launchpad.internal.repository.model.Integration;
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
@DisplayName("IntegrationService Unit Tests")
class IntegrationServiceTest {

    @Mock
    private IntegrationRepository integrationRepository;

    @InjectMocks
    private IntegrationService integrationService;

    private Integration integrationInput;
    private Integration integration1;
    private Integration integration2;

    @BeforeEach
    void setUp() {
        integrationInput = Integration.builder().name("Blast Motion").build();
        integration1     = Integration.builder().id(1).name("Blast Motion").build();
        integration2     = Integration.builder().id(2).name("Rapsodo").build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(integrationRepository.save(integrationInput)).thenReturn(integration1);

            Integration result = integrationService.create(integrationInput);

            assertThat(result).isSameAs(integration1);
            verify(integrationRepository).save(integrationInput);
            verifyNoMoreInteractions(integrationRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(integrationRepository.save(integrationInput)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> integrationService.create(integrationInput));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(integrationRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all integrations from repository")
        void findAll_returns_list_from_repo() {
            when(integrationRepository.findAll()).thenReturn(List.of(integration1, integration2));

            List<Integration> all = integrationService.findAll();

            assertThat(all).containsExactly(integration1, integration2);
            verify(integrationRepository).findAll();
            verifyNoMoreInteractions(integrationRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return integration when found")
        void findById_ok_returns_optional() {
            when(integrationRepository.findById(1)).thenReturn(Optional.of(integration1));

            Optional<Integration> result = integrationService.findById(1);

            assertThat(result).contains(integration1);
            verify(integrationRepository).findById(1);
            verifyNoMoreInteractions(integrationRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(integrationRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> integrationService.findById(1));
            verifyNoMoreInteractions(integrationRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete integration when it exists")
        void deleteById_when_exists_deletes() {
            when(integrationRepository.existsById(1)).thenReturn(true);

            integrationService.deleteById(1);

            verify(integrationRepository).existsById(1);
            verify(integrationRepository).deleteById(1);
            verifyNoMoreInteractions(integrationRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when integration does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(integrationRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> integrationService.deleteById(1));
            verify(integrationRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(integrationRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(integrationRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(integrationRepository).deleteById(1);

            assertThrows(PersistException.class, () -> integrationService.deleteById(1));
            verifyNoMoreInteractions(integrationRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return integration when it exists")
        void update_when_exists_saves_and_returns() {
            when(integrationRepository.existsById(1)).thenReturn(true);
            when(integrationRepository.save(integration1)).thenReturn(integration1);

            Integration result = integrationService.update(integration1);

            assertEquals(1, result.getId());
            verify(integrationRepository).existsById(1);
            verify(integrationRepository).save(integration1);
            verifyNoMoreInteractions(integrationRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when integration does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(integrationRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> integrationService.update(integration1));
            verify(integrationRepository).existsById(1);
            verifyNoMoreInteractions(integrationRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(integrationRepository.existsById(1)).thenReturn(true);
            when(integrationRepository.save(integration1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> integrationService.update(integration1));
            verify(integrationRepository).existsById(1);
            verify(integrationRepository).save(integration1);
            verifyNoMoreInteractions(integrationRepository);
        }
    }
}
