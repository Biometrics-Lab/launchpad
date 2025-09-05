package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.MeasurementRepository;
import com.bmlab.launchpad.repository.model.Measurement;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.security.exceptions.PersistException;
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
@DisplayName("MeasurementService Unit Tests")
class MeasurementServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private MeasurementService measurementService;

    private Measurement measurement1Input;
    private Measurement measurement1;
    private Measurement measurement2;

    @BeforeEach
    void setUp() {
        measurement1Input = Measurement.builder()
                .name("launch_angle")
                .build();

        measurement1 = Measurement.builder()
                .id(1)
                .name("launch_angle")
                .build();

        measurement2 = Measurement.builder()
                .id(2)
                .name("max_launch_angle")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(measurementRepository.save(measurement1Input)).thenReturn(measurement1);

            Measurement result = measurementService.create(measurement1Input);

            assertThat(result).isSameAs(measurement1);
            verify(measurementRepository).save(measurement1Input);
            verifyNoMoreInteractions(measurementRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(measurementRepository.save(measurement1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> measurementService.create(measurement1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(measurementRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all measurements from repository")
        void findAll_returns_list_from_repo() {
            when(measurementRepository.findAll()).thenReturn(List.of(measurement1, measurement2));

            List<Measurement> all = measurementService.findAll();

            assertThat(all).containsExactly(measurement1, measurement2);
            verify(measurementRepository).findAll();
            verifyNoMoreInteractions(measurementRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return measurement when found")
        void findById_ok_returns_optional() {
            when(measurementRepository.findById(1)).thenReturn(Optional.of(measurement1));

            Optional<Measurement> result = measurementService.findById(1);

            assertThat(result).contains(measurement1);
            verify(measurementRepository).findById(1);
            verifyNoMoreInteractions(measurementRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(measurementRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> measurementService.findById(1));
            verifyNoMoreInteractions(measurementRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete measurement when it exists")
        void deleteById_when_exists_deletes() {
            when(measurementRepository.existsById(1)).thenReturn(true);

            measurementService.deleteById(1);

            verify(measurementRepository).existsById(1);
            verify(measurementRepository).deleteById(1);
            verifyNoMoreInteractions(measurementRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when measurement does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(measurementRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> measurementService.deleteById(1));
            verify(measurementRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(measurementRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(measurementRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(measurementRepository).deleteById(1);

            assertThrows(PersistException.class, () -> measurementService.deleteById(1));
            verifyNoMoreInteractions(measurementRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return measurement when it exists")
        void update_when_exists_saves_and_returns() {
            when(measurementRepository.existsById(1)).thenReturn(true);
            when(measurementRepository.save(measurement1)).thenReturn(measurement1);

            Measurement result = measurementService.update(measurement1);

            assertEquals(1, result.getId());
            verify(measurementRepository).existsById(1);
            verify(measurementRepository).save(measurement1);
            verifyNoMoreInteractions(measurementRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when measurement does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(measurementRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> measurementService.update(measurement1));
            verify(measurementRepository).existsById(1);
            verifyNoMoreInteractions(measurementRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(measurementRepository.existsById(1)).thenReturn(true);
            when(measurementRepository.save(measurement1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> measurementService.update(measurement1));
            verify(measurementRepository).existsById(1);
            verify(measurementRepository).save(measurement1);
            verifyNoMoreInteractions(measurementRepository);
        }
    }
}