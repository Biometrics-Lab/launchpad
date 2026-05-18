package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.DataSourceRepository;
import com.biolab.launchpad.internal.repository.model.DataSource;
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
@DisplayName("DataSourceService Unit Tests")
class DataSourceServiceTest {

    @Mock
    private DataSourceRepository dataSourceRepository;

    @InjectMocks
    private DataSourceService dataSourceService;

    private DataSource dataSource1Input;
    private DataSource dataSource1;
    private DataSource dataSource2;

    @BeforeEach
    void setUp() {
        dataSource1Input = DataSource.builder()
                .integrationId(10)
                .metricId(20)
                .type("JSON_CONFIG")
                .build();

        dataSource1 = DataSource.builder()
                .id(1)
                .integrationId(10)
                .metricId(20)
                .type("JSON_CONFIG")
                .build();

        dataSource2 = DataSource.builder()
                .id(2)
                .integrationId(10)
                .metricId(30)
                .type("SCRIPT")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(dataSourceRepository.save(dataSource1Input)).thenReturn(dataSource1);

            DataSource result = dataSourceService.create(dataSource1Input);

            assertThat(result).isSameAs(dataSource1);
            verify(dataSourceRepository).save(dataSource1Input);
            verifyNoMoreInteractions(dataSourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(dataSourceRepository.save(dataSource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> dataSourceService.create(dataSource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(dataSourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all data_sources from repository")
        void findAll_returns_list_from_repo() {
            when(dataSourceRepository.findAll()).thenReturn(List.of(dataSource1, dataSource2));

            List<DataSource> all = dataSourceService.findAll();

            assertThat(all).containsExactly(dataSource1, dataSource2);
            verify(dataSourceRepository).findAll();
            verifyNoMoreInteractions(dataSourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return data_source when found")
        void findById_ok_returns_optional() {
            when(dataSourceRepository.findById(1)).thenReturn(Optional.of(dataSource1));

            Optional<DataSource> result = dataSourceService.findById(1);

            assertThat(result).contains(dataSource1);
            verify(dataSourceRepository).findById(1);
            verifyNoMoreInteractions(dataSourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(dataSourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> dataSourceService.findById(1));
            verifyNoMoreInteractions(dataSourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete data_source when it exists")
        void deleteById_when_exists_deletes() {
            when(dataSourceRepository.existsById(1)).thenReturn(true);

            dataSourceService.deleteById(1);

            verify(dataSourceRepository).existsById(1);
            verify(dataSourceRepository).deleteById(1);
            verifyNoMoreInteractions(dataSourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(dataSourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> dataSourceService.deleteById(1));
            verify(dataSourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(dataSourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(dataSourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(dataSourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> dataSourceService.deleteById(1));
            verifyNoMoreInteractions(dataSourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return data_source when it exists")
        void update_when_exists_saves_and_returns() {
            when(dataSourceRepository.existsById(1)).thenReturn(true);
            when(dataSourceRepository.save(dataSource1)).thenReturn(dataSource1);

            DataSource result = dataSourceService.update(dataSource1);

            assertEquals(1, result.getId());
            verify(dataSourceRepository).existsById(1);
            verify(dataSourceRepository).save(dataSource1);
            verifyNoMoreInteractions(dataSourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when data_source does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(dataSourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> dataSourceService.update(dataSource1));
            verify(dataSourceRepository).existsById(1);
            verifyNoMoreInteractions(dataSourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(dataSourceRepository.existsById(1)).thenReturn(true);
            when(dataSourceRepository.save(dataSource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> dataSourceService.update(dataSource1));
            verify(dataSourceRepository).existsById(1);
            verify(dataSourceRepository).save(dataSource1);
            verifyNoMoreInteractions(dataSourceRepository);
        }
    }
}
