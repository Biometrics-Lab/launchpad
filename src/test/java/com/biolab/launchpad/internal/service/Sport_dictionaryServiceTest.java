package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Sport_dictionaryRepository;
import com.biolab.launchpad.internal.repository.model.Sport_dictionary;
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
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sport_dictionaryService Unit Tests")
class Sport_dictionaryServiceTest {

    @Mock
    private Sport_dictionaryRepository sport_dictionaryRepository;

    @InjectMocks
    private Sport_dictionaryService sport_dictionaryService;

    private Sport_dictionary sport_dictionary1Input;
    private Sport_dictionary sport_dictionary1;
    private Sport_dictionary sport_dictionary2;

    @BeforeEach
    void setUp() {
        sport_dictionary1Input = Sport_dictionary.builder()
                .name("group1")
                .build();

        sport_dictionary1 = Sport_dictionary.builder()
                .name("group1")
                .build();

        sport_dictionary2 = Sport_dictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(sport_dictionaryRepository.save(sport_dictionary1Input)).thenReturn(sport_dictionary1);

            Sport_dictionary result = sport_dictionaryService.create(sport_dictionary1Input);

            assertThat(result).isSameAs(sport_dictionary1);
            verify(sport_dictionaryRepository).existsById(String.valueOf(sport_dictionary1Input.getName()));
            verify(sport_dictionaryRepository).save(sport_dictionary1Input);
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(sport_dictionaryRepository.save(sport_dictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> sport_dictionaryService.create(sport_dictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(sport_dictionaryRepository).existsById(String.valueOf(sport_dictionary1Input.getName()));
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all sport_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(sport_dictionaryRepository.findAll()).thenReturn(List.of(sport_dictionary1, sport_dictionary2));

            List<Sport_dictionary> all = sport_dictionaryService.findAll();

            assertThat(all).containsExactly(sport_dictionary1, sport_dictionary2);
            verify(sport_dictionaryRepository).findAll();
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return sport_dictionary when found")
        void findById_ok_returns_optional() {
            when(sport_dictionaryRepository.findById("group1")).thenReturn(Optional.of(sport_dictionary1));

            Optional<Sport_dictionary> result = sport_dictionaryService.findById("group1");

            assertThat(result).contains(sport_dictionary1);
            verify(sport_dictionaryRepository).findById("group1");
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(sport_dictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> sport_dictionaryService.findById("group1"));
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete sport_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(sport_dictionaryRepository.existsById("group1")).thenReturn(true);

            sport_dictionaryService.deleteById("group1");

            verify(sport_dictionaryRepository).existsById("group1");
            verify(sport_dictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when sport_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(sport_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sport_dictionaryService.deleteById("group1"));
            verify(sport_dictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(sport_dictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(sport_dictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> sport_dictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return sport_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(sport_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(sport_dictionaryRepository.save(sport_dictionary1)).thenReturn(sport_dictionary1);

            Sport_dictionary result = sport_dictionaryService.update(sport_dictionary1);

            assertEquals("group1", result.getId());
            verify(sport_dictionaryRepository).existsById("group1");
            verify(sport_dictionaryRepository).save(sport_dictionary1);
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when sport_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(sport_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sport_dictionaryService.update(sport_dictionary1));
            verify(sport_dictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(sport_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(sport_dictionaryRepository.save(sport_dictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> sport_dictionaryService.update(sport_dictionary1));
            verify(sport_dictionaryRepository).existsById("group1");
            verify(sport_dictionaryRepository).save(sport_dictionary1);
            verifyNoMoreInteractions(sport_dictionaryRepository);
        }
    }
}