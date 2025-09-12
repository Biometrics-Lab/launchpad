package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ReportRepository;
import com.biolab.launchpad.internal.repository.model.Report;
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
@DisplayName("ReportService Unit Tests")
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private Report report1Input;
    private Report report1;
    private Report report2;

    @BeforeEach
    void setUp() {
        report1Input = Report.builder()
                .name("report1")
                .build();

        report1 = Report.builder()
                .id(1)
                .name("report1")
                .build();

        report2 = Report.builder()
                .id(2)
                .name("report2")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(reportRepository.save(report1Input)).thenReturn(report1);

            Report result = reportService.create(report1Input);

            assertThat(result).isSameAs(report1);
            verify(reportRepository).save(report1Input);
            verifyNoMoreInteractions(reportRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(reportRepository.save(report1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> reportService.create(report1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(reportRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all reports from repository")
        void findAll_returns_list_from_repo() {
            when(reportRepository.findAll()).thenReturn(List.of(report1, report2));

            List<Report> all = reportService.findAll();

            assertThat(all).containsExactly(report1, report2);
            verify(reportRepository).findAll();
            verifyNoMoreInteractions(reportRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return report when found")
        void findById_ok_returns_optional() {
            when(reportRepository.findById(1)).thenReturn(Optional.of(report1));

            Optional<Report> result = reportService.findById(1);

            assertThat(result).contains(report1);
            verify(reportRepository).findById(1);
            verifyNoMoreInteractions(reportRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(reportRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> reportService.findById(1));
            verifyNoMoreInteractions(reportRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete report when it exists")
        void deleteById_when_exists_deletes() {
            when(reportRepository.existsById(1)).thenReturn(true);

            reportService.deleteById(1);

            verify(reportRepository).existsById(1);
            verify(reportRepository).deleteById(1);
            verifyNoMoreInteractions(reportRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when report does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(reportRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> reportService.deleteById(1));
            verify(reportRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(reportRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(reportRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(reportRepository).deleteById(1);

            assertThrows(PersistException.class, () -> reportService.deleteById(1));
            verifyNoMoreInteractions(reportRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return report when it exists")
        void update_when_exists_saves_and_returns() {
            when(reportRepository.existsById(1)).thenReturn(true);
            when(reportRepository.save(report1)).thenReturn(report1);

            Report result = reportService.update(report1);

            assertEquals(1, result.getId());
            verify(reportRepository).existsById(1);
            verify(reportRepository).save(report1);
            verifyNoMoreInteractions(reportRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when report does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(reportRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> reportService.update(report1));
            verify(reportRepository).existsById(1);
            verifyNoMoreInteractions(reportRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(reportRepository.existsById(1)).thenReturn(true);
            when(reportRepository.save(report1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> reportService.update(report1));
            verify(reportRepository).existsById(1);
            verify(reportRepository).save(report1);
            verifyNoMoreInteractions(reportRepository);
        }
    }
}