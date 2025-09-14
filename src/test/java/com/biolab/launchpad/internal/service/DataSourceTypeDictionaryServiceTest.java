package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.DataSourceTypeDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.DataSourceTypeDictionary;
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
@DisplayName("DataSourceTypeDictionaryService Unit Tests")
class DataSourceTypeDictionaryServiceTest {

    @Mock
    private DataSourceTypeDictionaryRepository dataSourceTypeDictionaryRepository;

    @InjectMocks
    private DataSourceTypeDictionaryService dataSourceTypeDictionaryService;

    private DataSourceTypeDictionary dataSourceTypeDictionary1Input;
    private DataSourceTypeDictionary dataSourceTypeDictionary1;
    private DataSourceTypeDictionary dataSourceTypeDictionary2;

    @BeforeEach
    void setUp() {
        dataSourceTypeDictionary1Input = DataSourceTypeDictionary.builder()
                .name("group1")
                .build();

        dataSourceTypeDictionary1 = DataSourceTypeDictionary.builder()
                .name("group1")
                .build();

        dataSourceTypeDictionary2 = DataSourceTypeDictionary.builder()
                .name("group2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary1Input)).thenReturn(dataSourceTypeDictionary1);

            DataSourceTypeDictionary result = dataSourceTypeDictionaryService.create(dataSourceTypeDictionary1Input);

            assertThat(result).isSameAs(dataSourceTypeDictionary1);
            verify(dataSourceTypeDictionaryRepository).existsById(String.valueOf(dataSourceTypeDictionary1Input.getName()));
            verify(dataSourceTypeDictionaryRepository).save(dataSourceTypeDictionary1Input);
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> dataSourceTypeDictionaryService.create(dataSourceTypeDictionary1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verify(dataSourceTypeDictionaryRepository).existsById(String.valueOf(dataSourceTypeDictionary1Input.getName()));
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all data_source_type_dictionarys from repository")
        void findAll_returns_list_from_repo() {
            when(dataSourceTypeDictionaryRepository.findAll()).thenReturn(List.of(dataSourceTypeDictionary1, dataSourceTypeDictionary2));

            List<DataSourceTypeDictionary> all = dataSourceTypeDictionaryService.findAll();

            assertThat(all).containsExactly(dataSourceTypeDictionary1, dataSourceTypeDictionary2);
            verify(dataSourceTypeDictionaryRepository).findAll();
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return data_source_type_dictionary when found")
        void findById_ok_returns_optional() {
            when(dataSourceTypeDictionaryRepository.findById("group1")).thenReturn(Optional.of(dataSourceTypeDictionary1));

            Optional<DataSourceTypeDictionary> result = dataSourceTypeDictionaryService.findById("group1");

            assertThat(result).contains(dataSourceTypeDictionary1);
            verify(dataSourceTypeDictionaryRepository).findById("group1");
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(dataSourceTypeDictionaryRepository.findById("group1")).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> dataSourceTypeDictionaryService.findById("group1"));
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete data_source_type_dictionary when it exists")
        void deleteById_when_exists_deletes() {
            when(dataSourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);

            dataSourceTypeDictionaryService.deleteById("group1");

            verify(dataSourceTypeDictionaryRepository).existsById("group1");
            verify(dataSourceTypeDictionaryRepository).deleteById("group1");
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source_type_dictionary does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(dataSourceTypeDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> dataSourceTypeDictionaryService.deleteById("group1"));
            verify(dataSourceTypeDictionaryRepository, never()).deleteById(anyString());
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(dataSourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(dataSourceTypeDictionaryRepository).deleteById("group1");

            assertThrows(PersistException.class, () -> dataSourceTypeDictionaryService.deleteById("group1"));
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return data_source_type_dictionary when it exists")
        void update_when_exists_saves_and_returns() {
            when(dataSourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);
            when(dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary1)).thenReturn(dataSourceTypeDictionary1);

            DataSourceTypeDictionary result = dataSourceTypeDictionaryService.update(dataSourceTypeDictionary1);

            assertEquals("group1", result.getId());
            verify(dataSourceTypeDictionaryRepository).existsById("group1");
            verify(dataSourceTypeDictionaryRepository).save(dataSourceTypeDictionary1);
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source_type_dictionary does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(dataSourceTypeDictionaryRepository.existsById("group1")).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> dataSourceTypeDictionaryService.update(dataSourceTypeDictionary1));
            verify(dataSourceTypeDictionaryRepository).existsById("group1");
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(dataSourceTypeDictionaryRepository.existsById("group1")).thenReturn(true);
            when(dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> dataSourceTypeDictionaryService.update(dataSourceTypeDictionary1));
            verify(dataSourceTypeDictionaryRepository).existsById("group1");
            verify(dataSourceTypeDictionaryRepository).save(dataSourceTypeDictionary1);
            verifyNoMoreInteractions(dataSourceTypeDictionaryRepository);
        }
    }
}