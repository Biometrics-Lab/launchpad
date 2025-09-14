package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.model.Rep;
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
@DisplayName("RepService Unit Tests")
class RepServiceTest {

    @Mock
    private RepRepository repRepository;

    @InjectMocks
    private RepService repService;

    private Rep rep1Input;
    private Rep rep1;
    private Rep rep2;

    @BeforeEach
    void setUp() {
        rep1Input = Rep.builder()
                .build();

        rep1 = Rep.builder()
                .id(1)
                .build();

        rep2 = Rep.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(repRepository.save(rep1Input)).thenReturn(rep1);

            Rep result = repService.create(rep1Input);

            assertThat(result).isSameAs(rep1);
            verify(repRepository).save(rep1Input);
            verifyNoMoreInteractions(repRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(repRepository.save(rep1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> repService.create(rep1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(repRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all reps from repository")
        void findAll_returns_list_from_repo() {
            when(repRepository.findAll()).thenReturn(List.of(rep1, rep2));

            List<Rep> all = repService.findAll();

            assertThat(all).containsExactly(rep1, rep2);
            verify(repRepository).findAll();
            verifyNoMoreInteractions(repRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep when found")
        void findById_ok_returns_optional() {
            when(repRepository.findById(1)).thenReturn(Optional.of(rep1));

            Optional<Rep> result = repService.findById(1);

            assertThat(result).contains(rep1);
            verify(repRepository).findById(1);
            verifyNoMoreInteractions(repRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(repRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> repService.findById(1));
            verifyNoMoreInteractions(repRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep when it exists")
        void deleteById_when_exists_deletes() {
            when(repRepository.existsById(1)).thenReturn(true);

            repService.deleteById(1);

            verify(repRepository).existsById(1);
            verify(repRepository).deleteById(1);
            verifyNoMoreInteractions(repRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(repRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repService.deleteById(1));
            verify(repRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(repRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(repRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(repRepository).deleteById(1);

            assertThrows(PersistException.class, () -> repService.deleteById(1));
            verifyNoMoreInteractions(repRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep when it exists")
        void update_when_exists_saves_and_returns() {
            when(repRepository.existsById(1)).thenReturn(true);
            when(repRepository.save(rep1)).thenReturn(rep1);

            Rep result = repService.update(rep1);

            assertEquals(1, result.getId().intValue());
            verify(repRepository).existsById(1);
            verify(repRepository).save(rep1);
            verifyNoMoreInteractions(repRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(repRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repService.update(rep1));
            verify(repRepository).existsById(1);
            verifyNoMoreInteractions(repRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(repRepository.existsById(1)).thenReturn(true);
            when(repRepository.save(rep1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> repService.update(rep1));
            verify(repRepository).existsById(1);
            verify(repRepository).save(rep1);
            verifyNoMoreInteractions(repRepository);
        }
    }
}