package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AgeGroupDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.AgeGroupDictionary;
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
@DisplayName("Age_group_dictionaryService Unit Tests")
class AgeGroupDictionaryServiceTest {

    @Mock
    private AgeGroupDictionaryRepository ageGroupDictionaryRepository;

    @InjectMocks
    private AgeGroupDictionaryService ageGroupDictionaryService;

    private AgeGroupDictionary ageGroupDictionary1Input;
    private AgeGroupDictionary ageGroupDictionary1;
    private AgeGroupDictionary ageGroupDictionary2;

    @BeforeEach
    void setUp() {
        ageGroupDictionary1Input = AgeGroupDictionary.builder()
                .name("group1")
                .build();

        ageGroupDictionary1 = AgeGroupDictionary.builder()
                .name("group1")
                .build();

        ageGroupDictionary2 = AgeGroupDictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(ageGroupDictionaryRepository.save(ageGroupDictionary1Input)).thenReturn(ageGroupDictionary1);

            AgeGroupDictionary result = ageGroupDictionaryService.create(ageGroupDictionary1Input);

            assertThat(result).isSameAs(ageGroupDictionary1);
            verify(ageGroupDictionaryRepository).existsById(String.valueOf(ageGroupDictionary1Input.getName()));
            verify(ageGroupDictionaryRepository).save(ageGroupDictionary1Input);
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(ageGroupDictionaryRepository.save(ageGroupDictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> ageGroupDictionaryService.create(ageGroupDictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(ageGroupDictionaryRepository).existsById(String.valueOf(ageGroupDictionary1Input.getName()));
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all age_group_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(ageGroupDictionaryRepository.findAll()).thenReturn(List.of(ageGroupDictionary1, ageGroupDictionary2));

            List<AgeGroupDictionary> all = ageGroupDictionaryService.findAll();

            assertThat(all).containsExactly(ageGroupDictionary1, ageGroupDictionary2);
            verify(ageGroupDictionaryRepository).findAll();
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return age_group_dictionary when found")
        void findById_ok_returns_optional() {
            when(ageGroupDictionaryRepository.findById("group1")).thenReturn(Optional.of(ageGroupDictionary1));

            Optional<AgeGroupDictionary> result = ageGroupDictionaryService.findById("group1");

            assertThat(result).contains(ageGroupDictionary1);
            verify(ageGroupDictionaryRepository).findById("group1");
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(ageGroupDictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> ageGroupDictionaryService.findById("group1"));
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete age_group_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(ageGroupDictionaryRepository.existsById("group1")).thenReturn(true);

            ageGroupDictionaryService.deleteById("group1");

            verify(ageGroupDictionaryRepository).existsById("group1");
            verify(ageGroupDictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when age_group_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(ageGroupDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> ageGroupDictionaryService.deleteById("group1"));
            verify(ageGroupDictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(ageGroupDictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(ageGroupDictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> ageGroupDictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return age_group_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(ageGroupDictionaryRepository.existsById("group1")).thenReturn(true);
            when(ageGroupDictionaryRepository.save(ageGroupDictionary1)).thenReturn(ageGroupDictionary1);

            AgeGroupDictionary result = ageGroupDictionaryService.update(ageGroupDictionary1);

            assertEquals("group1", result.getId());
            verify(ageGroupDictionaryRepository).existsById("group1");
            verify(ageGroupDictionaryRepository).save(ageGroupDictionary1);
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when age_group_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(ageGroupDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> ageGroupDictionaryService.update(ageGroupDictionary1));
            verify(ageGroupDictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(ageGroupDictionaryRepository.existsById("group1")).thenReturn(true);
            when(ageGroupDictionaryRepository.save(ageGroupDictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> ageGroupDictionaryService.update(ageGroupDictionary1));
            verify(ageGroupDictionaryRepository).existsById("group1");
            verify(ageGroupDictionaryRepository).save(ageGroupDictionary1);
            verifyNoMoreInteractions(ageGroupDictionaryRepository);
        }
    }
}