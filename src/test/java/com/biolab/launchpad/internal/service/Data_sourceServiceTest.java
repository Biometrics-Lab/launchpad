package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Data_sourceRepository;
import com.biolab.launchpad.internal.repository.model.Data_source;
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
@DisplayName("Data_sourceService Unit Tests")
class Data_sourceServiceTest {

    @Mock
    private Data_sourceRepository data_sourceRepository;

    @InjectMocks
    private Data_sourceService data_sourceService;

    private Data_source data_source1Input;
    private Data_source data_source1;
    private Data_source data_source2;

    @BeforeEach
    void setUp() {
        data_source1Input = Data_source.builder()
                .name("data_source1")
                .build();

        data_source1 = Data_source.builder()
                .id(1)
                .name("data_source1")
                .build();

        data_source2 = Data_source.builder()
                .id(2)
                .name("data_source2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(data_sourceRepository.save(data_source1Input)).thenReturn(data_source1);

            Data_source result = data_sourceService.create(data_source1Input);

            assertThat(result).isSameAs(data_source1);
            verify(data_sourceRepository).save(data_source1Input);
            verifyNoMoreInteractions(data_sourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(data_sourceRepository.save(data_source1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> data_sourceService.create(data_source1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(data_sourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all data_sources from repository")
        void findAll_returns_list_from_repo() {
            when(data_sourceRepository.findAll()).thenReturn(List.of(data_source1, data_source2));

            List<Data_source> all = data_sourceService.findAll();

            assertThat(all).containsExactly(data_source1, data_source2);
            verify(data_sourceRepository).findAll();
            verifyNoMoreInteractions(data_sourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return data_source when found")
        void findById_ok_returns_optional() {
            when(data_sourceRepository.findById(1)).thenReturn(Optional.of(data_source1));

            Optional<Data_source> result = data_sourceService.findById(1);

            assertThat(result).contains(data_source1);
            verify(data_sourceRepository).findById(1);
            verifyNoMoreInteractions(data_sourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(data_sourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> data_sourceService.findById(1));
            verifyNoMoreInteractions(data_sourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete data_source when it exists")
        void deleteById_when_exists_deletes() {
            when(data_sourceRepository.existsById(1)).thenReturn(true);

            data_sourceService.deleteById(1);

            verify(data_sourceRepository).existsById(1);
            verify(data_sourceRepository).deleteById(1);
            verifyNoMoreInteractions(data_sourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(data_sourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> data_sourceService.deleteById(1));
            verify(data_sourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(data_sourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(data_sourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(data_sourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> data_sourceService.deleteById(1));
            verifyNoMoreInteractions(data_sourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return data_source when it exists")
        void update_when_exists_saves_and_returns() {
            when(data_sourceRepository.existsById(1)).thenReturn(true);
            when(data_sourceRepository.save(data_source1)).thenReturn(data_source1);

            Data_source result = data_sourceService.update(data_source1);

            assertEquals(1, result.getId());
            verify(data_sourceRepository).existsById(1);
            verify(data_sourceRepository).save(data_source1);
            verifyNoMoreInteractions(data_sourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(data_sourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> data_sourceService.update(data_source1));
            verify(data_sourceRepository).existsById(1);
            verifyNoMoreInteractions(data_sourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(data_sourceRepository.existsById(1)).thenReturn(true);
            when(data_sourceRepository.save(data_source1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> data_sourceService.update(data_source1));
            verify(data_sourceRepository).existsById(1);
            verify(data_sourceRepository).save(data_source1);
            verifyNoMoreInteractions(data_sourceRepository);
        }
    }
}