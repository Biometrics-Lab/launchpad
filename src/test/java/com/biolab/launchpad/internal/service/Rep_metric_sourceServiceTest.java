package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.Rep_metric_sourceRepository;
import com.biolab.launchpad.internal.repository.model.Rep_metric_source;
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
@DisplayName("Rep_metric_sourceService Unit Tests")
class Rep_metric_sourceServiceTest {

    @Mock
    private Rep_metric_sourceRepository rep_metric_sourceRepository;

    @InjectMocks
    private Rep_metric_sourceService rep_metric_sourceService;

    private Rep_metric_source rep_metric_source1Input;
    private Rep_metric_source rep_metric_source1;
    private Rep_metric_source rep_metric_source2;

    @BeforeEach
    void setUp() {
        rep_metric_source1Input = Rep_metric_source.builder()
                .build();

        rep_metric_source1 = Rep_metric_source.builder()
                .id(1)
                .build();

        rep_metric_source2 = Rep_metric_source.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(rep_metric_sourceRepository.save(rep_metric_source1Input)).thenReturn(rep_metric_source1);

            Rep_metric_source result = rep_metric_sourceService.create(rep_metric_source1Input);

            assertThat(result).isSameAs(rep_metric_source1);
            verify(rep_metric_sourceRepository).save(rep_metric_source1Input);
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(rep_metric_sourceRepository.save(rep_metric_source1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> rep_metric_sourceService.create(rep_metric_source1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all rep_metric_sources from repository")
        void findAll_returns_list_from_repo() {
            when(rep_metric_sourceRepository.findAll()).thenReturn(List.of(rep_metric_source1, rep_metric_source2));

            List<Rep_metric_source> all = rep_metric_sourceService.findAll();

            assertThat(all).containsExactly(rep_metric_source1, rep_metric_source2);
            verify(rep_metric_sourceRepository).findAll();
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return rep_metric_source when found")
        void findById_ok_returns_optional() {
            when(rep_metric_sourceRepository.findById(1)).thenReturn(Optional.of(rep_metric_source1));

            Optional<Rep_metric_source> result = rep_metric_sourceService.findById(1);

            assertThat(result).contains(rep_metric_source1);
            verify(rep_metric_sourceRepository).findById(1);
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(rep_metric_sourceRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> rep_metric_sourceService.findById(1));
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete rep_metric_source when it exists")
        void deleteById_when_exists_deletes() {
            when(rep_metric_sourceRepository.existsById(1)).thenReturn(true);

            rep_metric_sourceService.deleteById(1);

            verify(rep_metric_sourceRepository).existsById(1);
            verify(rep_metric_sourceRepository).deleteById(1);
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric_source does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(rep_metric_sourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> rep_metric_sourceService.deleteById(1));
            verify(rep_metric_sourceRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(rep_metric_sourceRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(rep_metric_sourceRepository).deleteById(1);

            assertThrows(PersistException.class, () -> rep_metric_sourceService.deleteById(1));
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return rep_metric_source when it exists")
        void update_when_exists_saves_and_returns() {
            when(rep_metric_sourceRepository.existsById(1)).thenReturn(true);
            when(rep_metric_sourceRepository.save(rep_metric_source1)).thenReturn(rep_metric_source1);

            Rep_metric_source result = rep_metric_sourceService.update(rep_metric_source1);

            assertEquals(1, result.getId().intValue());
            verify(rep_metric_sourceRepository).existsById(1);
            verify(rep_metric_sourceRepository).save(rep_metric_source1);
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when rep_metric_source does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(rep_metric_sourceRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> rep_metric_sourceService.update(rep_metric_source1));
            verify(rep_metric_sourceRepository).existsById(1);
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(rep_metric_sourceRepository.existsById(1)).thenReturn(true);
            when(rep_metric_sourceRepository.save(rep_metric_source1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> rep_metric_sourceService.update(rep_metric_source1));
            verify(rep_metric_sourceRepository).existsById(1);
            verify(rep_metric_sourceRepository).save(rep_metric_source1);
            verifyNoMoreInteractions(rep_metric_sourceRepository);
        }
    }
}