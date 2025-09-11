package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Data_source_type_dictionaryRepository;
import com.biolab.launchpad.internal.repository.model.Data_source_type_dictionary;
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
@DisplayName("Data_source_type_dictionaryService Unit Tests")
class Data_source_type_dictionaryServiceTest {

    @Mock
    private Data_source_type_dictionaryRepository data_source_type_dictionaryRepository;

    @InjectMocks
    private Data_source_type_dictionaryService data_source_type_dictionaryService;

    private Data_source_type_dictionary data_source_type_dictionary1Input;
    private Data_source_type_dictionary data_source_type_dictionary1;
    private Data_source_type_dictionary data_source_type_dictionary2;

    @BeforeEach
    void setUp() {
        data_source_type_dictionary1Input = Data_source_type_dictionary.builder()
                .name("group1")
                .build();

        data_source_type_dictionary1 = Data_source_type_dictionary.builder()
                .name("group1")
                .build();

        data_source_type_dictionary2 = Data_source_type_dictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(data_source_type_dictionaryRepository.save(data_source_type_dictionary1Input)).thenReturn(data_source_type_dictionary1);

            Data_source_type_dictionary result = data_source_type_dictionaryService.create(data_source_type_dictionary1Input);

            assertThat(result).isSameAs(data_source_type_dictionary1);
            verify(data_source_type_dictionaryRepository).existsById(String.valueOf(data_source_type_dictionary1Input.getName()));
            verify(data_source_type_dictionaryRepository).save(data_source_type_dictionary1Input);
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(data_source_type_dictionaryRepository.save(data_source_type_dictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> data_source_type_dictionaryService.create(data_source_type_dictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(data_source_type_dictionaryRepository).existsById(String.valueOf(data_source_type_dictionary1Input.getName()));
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all data_source_type_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(data_source_type_dictionaryRepository.findAll()).thenReturn(List.of(data_source_type_dictionary1, data_source_type_dictionary2));

            List<Data_source_type_dictionary> all = data_source_type_dictionaryService.findAll();

            assertThat(all).containsExactly(data_source_type_dictionary1, data_source_type_dictionary2);
            verify(data_source_type_dictionaryRepository).findAll();
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return data_source_type_dictionary when found")
        void findById_ok_returns_optional() {
            when(data_source_type_dictionaryRepository.findById("group1")).thenReturn(Optional.of(data_source_type_dictionary1));

            Optional<Data_source_type_dictionary> result = data_source_type_dictionaryService.findById("group1");

            assertThat(result).contains(data_source_type_dictionary1);
            verify(data_source_type_dictionaryRepository).findById("group1");
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(data_source_type_dictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> data_source_type_dictionaryService.findById("group1"));
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete data_source_type_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(data_source_type_dictionaryRepository.existsById("group1")).thenReturn(true);

            data_source_type_dictionaryService.deleteById("group1");

            verify(data_source_type_dictionaryRepository).existsById("group1");
            verify(data_source_type_dictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source_type_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(data_source_type_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> data_source_type_dictionaryService.deleteById("group1"));
            verify(data_source_type_dictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(data_source_type_dictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(data_source_type_dictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> data_source_type_dictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return data_source_type_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(data_source_type_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(data_source_type_dictionaryRepository.save(data_source_type_dictionary1)).thenReturn(data_source_type_dictionary1);

            Data_source_type_dictionary result = data_source_type_dictionaryService.update(data_source_type_dictionary1);

            assertEquals("group1", result.getId());
            verify(data_source_type_dictionaryRepository).existsById("group1");
            verify(data_source_type_dictionaryRepository).save(data_source_type_dictionary1);
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source_type_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(data_source_type_dictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> data_source_type_dictionaryService.update(data_source_type_dictionary1));
            verify(data_source_type_dictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(data_source_type_dictionaryRepository.existsById("group1")).thenReturn(true);
            when(data_source_type_dictionaryRepository.save(data_source_type_dictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> data_source_type_dictionaryService.update(data_source_type_dictionary1));
            verify(data_source_type_dictionaryRepository).existsById("group1");
            verify(data_source_type_dictionaryRepository).save(data_source_type_dictionary1);
            verifyNoMoreInteractions(data_source_type_dictionaryRepository);
        }
    }
}