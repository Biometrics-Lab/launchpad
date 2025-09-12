package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Rep_metricRepository;
import com.biolab.launchpad.internal.repository.model.Rep_metric;
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
@DisplayName("Rep_metricService Unit Tests")
class Rep_metricServiceTest {

    @Mock
    private Rep_metricRepository rep_metricRepository;

    @InjectMocks
    private Rep_metricService rep_metricService;

    private Rep_metric rep_metric1Input;
    private Rep_metric rep_metric1;
    private Rep_metric rep_metric2;

    @BeforeEach
    void setUp() {
        rep_metric1Input = Rep_metric.builder()
                .build();

        rep_metric1 = Rep_metric.builder()
                .id(1)
                .build();

        rep_metric2 = Rep_metric.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(rep_metricRepository.save(rep_metric1Input)).thenReturn(rep_metric1);

            Rep_metric result = rep_metricService.create(rep_metric1Input);

            assertThat(result).isSameAs(rep_metric1);
            verify(rep_metricRepository).save(rep_metric1Input);
            verifyNoMoreInteractions(rep_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(rep_metricRepository.save(rep_metric1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> rep_metricService.create(rep_metric1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(rep_metricRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all rep_metrics from repository")
        void findAll_returns_list_from_repo() {
            when(rep_metricRepository.findAll()).thenReturn(List.of(rep_metric1, rep_metric2));

            List<Rep_metric> all = rep_metricService.findAll();

            assertThat(all).containsExactly(rep_metric1, rep_metric2);
            verify(rep_metricRepository).findAll();
            verifyNoMoreInteractions(rep_metricRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep_metric when found")
        void findById_ok_returns_optional() {
            when(rep_metricRepository.findById(1)).thenReturn(Optional.of(rep_metric1));

            Optional<Rep_metric> result = rep_metricService.findById(1);

            assertThat(result).contains(rep_metric1);
            verify(rep_metricRepository).findById(1);
            verifyNoMoreInteractions(rep_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(rep_metricRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> rep_metricService.findById(1));
            verifyNoMoreInteractions(rep_metricRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep_metric when it exists")
        void deleteById_when_exists_deletes() {
            when(rep_metricRepository.existsById(1)).thenReturn(true);

            rep_metricService.deleteById(1);

            verify(rep_metricRepository).existsById(1);
            verify(rep_metricRepository).deleteById(1);
            verifyNoMoreInteractions(rep_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(rep_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> rep_metricService.deleteById(1));
            verify(rep_metricRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(rep_metricRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(rep_metricRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(rep_metricRepository).deleteById(1);

            assertThrows(PersistException.class, () -> rep_metricService.deleteById(1));
            verifyNoMoreInteractions(rep_metricRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep_metric when it exists")
        void update_when_exists_saves_and_returns() {
            when(rep_metricRepository.existsById(1)).thenReturn(true);
            when(rep_metricRepository.save(rep_metric1)).thenReturn(rep_metric1);

            Rep_metric result = rep_metricService.update(rep_metric1);

            assertEquals(1, result.getId().intValue());
            verify(rep_metricRepository).existsById(1);
            verify(rep_metricRepository).save(rep_metric1);
            verifyNoMoreInteractions(rep_metricRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(rep_metricRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> rep_metricService.update(rep_metric1));
            verify(rep_metricRepository).existsById(1);
            verifyNoMoreInteractions(rep_metricRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(rep_metricRepository.existsById(1)).thenReturn(true);
            when(rep_metricRepository.save(rep_metric1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> rep_metricService.update(rep_metric1));
            verify(rep_metricRepository).existsById(1);
            verify(rep_metricRepository).save(rep_metric1);
            verifyNoMoreInteractions(rep_metricRepository);
        }
    }
}