package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.RepMetricSourceRepository;
import com.biolab.launchpad.internal.repository.model.RepMetricSource;
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
@DisplayName("RepMetricSourceService Unit Tests")
class RepMetricSourceServiceTest {

    @Mock
    private RepMetricSourceRepository repMetricSourceRepository;

    @InjectMocks
    private RepMetricSourceService repMetricSourceService;

    private RepMetricSource repMetricSource1Input;
    private RepMetricSource repMetricSource1;
    private RepMetricSource repMetricSource2;

    @BeforeEach
    void setUp() {
        repMetricSource1Input = RepMetricSource.builder()
                .build();

        repMetricSource1 = RepMetricSource.builder()
                .id(1)
                .build();

        repMetricSource2 = RepMetricSource.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(repMetricSourceRepository.save(repMetricSource1Input)).thenReturn(repMetricSource1);

            RepMetricSource result = repMetricSourceService.create(repMetricSource1Input);

            assertThat(result).isSameAs(repMetricSource1);
            verify(repMetricSourceRepository).save(repMetricSource1Input);
            verifyNoMoreInteractions(repMetricSourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(repMetricSourceRepository.save(repMetricSource1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> repMetricSourceService.create(repMetricSource1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(repMetricSourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all rep_metric_sources from repository")
        void findAll_returns_list_from_repo() {
            when(repMetricSourceRepository.findAll()).thenReturn(List.of(repMetricSource1, repMetricSource2));

            List<RepMetricSource> all = repMetricSourceService.findAll();

            assertThat(all).containsExactly(repMetricSource1, repMetricSource2);
            verify(repMetricSourceRepository).findAll();
            verifyNoMoreInteractions(repMetricSourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep_metric_source when found")
        void findById_ok_returns_optional() {
            when(repMetricSourceRepository.findById(1)).thenReturn(Optional.of(repMetricSource1));

            Optional<RepMetricSource> result = repMetricSourceService.findById(1);

            assertThat(result).contains(repMetricSource1);
            verify(repMetricSourceRepository).findById(1);
            verifyNoMoreInteractions(repMetricSourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(repMetricSourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> repMetricSourceService.findById(1));
            verifyNoMoreInteractions(repMetricSourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep_metric_source when it exists")
        void deleteById_when_exists_deletes() {
            when(repMetricSourceRepository.existsById(1)).thenReturn(true);

            repMetricSourceService.deleteById(1);

            verify(repMetricSourceRepository).existsById(1);
            verify(repMetricSourceRepository).deleteById(1);
            verifyNoMoreInteractions(repMetricSourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric_source does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(repMetricSourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repMetricSourceService.deleteById(1));
            verify(repMetricSourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(repMetricSourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(repMetricSourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(repMetricSourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> repMetricSourceService.deleteById(1));
            verifyNoMoreInteractions(repMetricSourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep_metric_source when it exists")
        void update_when_exists_saves_and_returns() {
            when(repMetricSourceRepository.existsById(1)).thenReturn(true);
            when(repMetricSourceRepository.save(repMetricSource1)).thenReturn(repMetricSource1);

            RepMetricSource result = repMetricSourceService.update(repMetricSource1);

            assertEquals(1, result.getId().intValue());
            verify(repMetricSourceRepository).existsById(1);
            verify(repMetricSourceRepository).save(repMetricSource1);
            verifyNoMoreInteractions(repMetricSourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric_source does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(repMetricSourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> repMetricSourceService.update(repMetricSource1));
            verify(repMetricSourceRepository).existsById(1);
            verifyNoMoreInteractions(repMetricSourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(repMetricSourceRepository.existsById(1)).thenReturn(true);
            when(repMetricSourceRepository.save(repMetricSource1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> repMetricSourceService.update(repMetricSource1));
            verify(repMetricSourceRepository).existsById(1);
            verify(repMetricSourceRepository).save(repMetricSource1);
            verifyNoMoreInteractions(repMetricSourceRepository);
        }
    }
}