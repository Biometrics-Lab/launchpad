package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.SportDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.SportDictionary;
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
class SportDictionaryServiceTest {

    @Mock
    private SportDictionaryRepository sportDictionaryRepository;

    @InjectMocks
    private SportDictionaryService sportDictionaryService;

    private SportDictionary sportDictionary1Input;
    private SportDictionary sportDictionary1;
    private SportDictionary sportDictionary2;

    @BeforeEach
    void setUp() {
        sportDictionary1Input = SportDictionary.builder()
                .name("group1")
                .build();

        sportDictionary1 = SportDictionary.builder()
                .name("group1")
                .build();

        sportDictionary2 = SportDictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(sportDictionaryRepository.save(sportDictionary1Input)).thenReturn(sportDictionary1);

            SportDictionary result = sportDictionaryService.create(sportDictionary1Input);

            assertThat(result).isSameAs(sportDictionary1);
            verify(sportDictionaryRepository).existsById(String.valueOf(sportDictionary1Input.getName()));
            verify(sportDictionaryRepository).save(sportDictionary1Input);
            verifyNoMoreInteractions(sportDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(sportDictionaryRepository.save(sportDictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> sportDictionaryService.create(sportDictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(sportDictionaryRepository).existsById(String.valueOf(sportDictionary1Input.getName()));
            verifyNoMoreInteractions(sportDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all sport_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(sportDictionaryRepository.findAll()).thenReturn(List.of(sportDictionary1, sportDictionary2));

            List<SportDictionary> all = sportDictionaryService.findAll();

            assertThat(all).containsExactly(sportDictionary1, sportDictionary2);
            verify(sportDictionaryRepository).findAll();
            verifyNoMoreInteractions(sportDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return sport_dictionary when found")
        void findById_ok_returns_optional() {
            when(sportDictionaryRepository.findById("group1")).thenReturn(Optional.of(sportDictionary1));

            Optional<SportDictionary> result = sportDictionaryService.findById("group1");

            assertThat(result).contains(sportDictionary1);
            verify(sportDictionaryRepository).findById("group1");
            verifyNoMoreInteractions(sportDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(sportDictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> sportDictionaryService.findById("group1"));
            verifyNoMoreInteractions(sportDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete sport_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(sportDictionaryRepository.existsById("group1")).thenReturn(true);

            sportDictionaryService.deleteById("group1");

            verify(sportDictionaryRepository).existsById("group1");
            verify(sportDictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(sportDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when sport_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(sportDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sportDictionaryService.deleteById("group1"));
            verify(sportDictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(sportDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(sportDictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(sportDictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> sportDictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(sportDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return sport_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(sportDictionaryRepository.existsById("group1")).thenReturn(true);
            when(sportDictionaryRepository.save(sportDictionary1)).thenReturn(sportDictionary1);

            SportDictionary result = sportDictionaryService.update(sportDictionary1);

            assertEquals("group1", result.getId());
            verify(sportDictionaryRepository).existsById("group1");
            verify(sportDictionaryRepository).save(sportDictionary1);
            verifyNoMoreInteractions(sportDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when sport_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(sportDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> sportDictionaryService.update(sportDictionary1));
            verify(sportDictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(sportDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(sportDictionaryRepository.existsById("group1")).thenReturn(true);
            when(sportDictionaryRepository.save(sportDictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> sportDictionaryService.update(sportDictionary1));
            verify(sportDictionaryRepository).existsById("group1");
            verify(sportDictionaryRepository).save(sportDictionary1);
            verifyNoMoreInteractions(sportDictionaryRepository);
        }
    }
}