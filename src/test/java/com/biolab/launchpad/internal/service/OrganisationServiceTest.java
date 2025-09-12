package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.OrganisationRepository;
import com.biolab.launchpad.internal.repository.model.Organisation;
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
@DisplayName("OrganisationService Unit Tests")
class OrganisationServiceTest {

    @Mock
    private OrganisationRepository organisationRepository;

    @InjectMocks
    private OrganisationService organisationService;

    private Organisation organisation1Input;
    private Organisation organisation1;
    private Organisation organisation2;

    @BeforeEach
    void setUp() {
        organisation1Input = Organisation.builder()
                .name("org1")
                .build();

        organisation1 = Organisation.builder()
                .id(1)
                .name("org1")
                .build();

        organisation2 = Organisation.builder()
                .id(2)
                .name("org2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(organisationRepository.save(organisation1Input)).thenReturn(organisation1);

            Organisation result = organisationService.create(organisation1Input);

            assertThat(result).isSameAs(organisation1);
            verify(organisationRepository).save(organisation1Input);
            verifyNoMoreInteractions(organisationRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(organisationRepository.save(organisation1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> organisationService.create(organisation1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(organisationRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all organisations from repository")
        void findAll_returns_list_from_repo() {
            when(organisationRepository.findAll()).thenReturn(List.of(organisation1, organisation2));

            List<Organisation> all = organisationService.findAll();

            assertThat(all).containsExactly(organisation1, organisation2);
            verify(organisationRepository).findAll();
            verifyNoMoreInteractions(organisationRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return organisation when found")
        void findById_ok_returns_optional() {
            when(organisationRepository.findById(1)).thenReturn(Optional.of(organisation1));

            Optional<Organisation> result = organisationService.findById(1);

            assertThat(result).contains(organisation1);
            verify(organisationRepository).findById(1);
            verifyNoMoreInteractions(organisationRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(organisationRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> organisationService.findById(1));
            verifyNoMoreInteractions(organisationRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete organisation when it exists")
        void deleteById_when_exists_deletes() {
            when(organisationRepository.existsById(1)).thenReturn(true);

            organisationService.deleteById(1);

            verify(organisationRepository).existsById(1);
            verify(organisationRepository).deleteById(1);
            verifyNoMoreInteractions(organisationRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when organisation does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(organisationRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> organisationService.deleteById(1));
            verify(organisationRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(organisationRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(organisationRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(organisationRepository).deleteById(1);

            assertThrows(PersistException.class, () -> organisationService.deleteById(1));
            verifyNoMoreInteractions(organisationRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return organisation when it exists")
        void update_when_exists_saves_and_returns() {
            when(organisationRepository.existsById(1)).thenReturn(true);
            when(organisationRepository.save(organisation1)).thenReturn(organisation1);

            Organisation result = organisationService.update(organisation1);

            assertEquals(1, result.getId());
            verify(organisationRepository).existsById(1);
            verify(organisationRepository).save(organisation1);
            verifyNoMoreInteractions(organisationRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when organisation does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(organisationRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> organisationService.update(organisation1));
            verify(organisationRepository).existsById(1);
            verifyNoMoreInteractions(organisationRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(organisationRepository.existsById(1)).thenReturn(true);
            when(organisationRepository.save(organisation1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> organisationService.update(organisation1));
            verify(organisationRepository).existsById(1);
            verify(organisationRepository).save(organisation1);
            verifyNoMoreInteractions(organisationRepository);
        }
    }
}