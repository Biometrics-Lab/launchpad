//package com.biolab.launchpad.internal.service;
//
//import com.biolab.launchpad.internal.repository.zzzRepository;
//import com.biolab.launchpad.internal.repository.model.zzz;
//import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
//import com.biolab.launchpad.internal.security.exceptions.PersistException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Age_group_dictionaryService Unit Tests")
//class Age_group_dictionaryServiceTest1 {
//
//    @Mock
//    private XXXRepository zzzRepository;
//
//    @InjectMocks
//    private XXXService zzzService;
//
//    private XXX zzz1Input;
//    private XXX zzz1;
//    private XXX zzz2;
//
//    @BeforeEach
//    void setUp() {
//        zzz1Input = XXX.builder()
//                .name("launch_angle")
//                .build();
//
//        zzz1 = XXX.builder()
//                .id(1)
//                .name("launch_angle")
//                .build();
//
//        zzz2 = XXX.builder()
//                .id(2)
//                .name("max_launch_angle")
//                .build();
//    }
//
//    @Nested
//    @DisplayName("create()")
//    class CreateTests {
//        @Test
//        @DisplayName("should save and return entity when successful")
//        void create_ok_saves_and_returns_entity() {
//            when(zzzRepository.save(zzz1Input)).thenReturn(zzz1);
//
//            XXX result = zzzService.create(zzz1Input);
//
//            assertThat(result).isSameAs(zzz1);
//            verify(zzzRepository).save(zzz1Input);
//            verifyNoMoreInteractions(zzzRepository);
//        }
//
//        @Test
//        @DisplayName("should wrap repo exception in PersistException")
//        void create_wraps_any_exception_in_PersistException() {
//            when(zzzRepository.save(zzz1Input)).thenThrow(new RuntimeException("db down"));
//
//            PersistException ex = assertThrows(PersistException.class, () -> zzzService.create(zzz1Input));
//            assertTrue(ex.getMessage().contains("db down"));
//            verifyNoMoreInteractions(zzzRepository);
//        }
//    }
//
//    @Nested
//    @DisplayName("findAll()")
//    class FindAllTests {
//        @Test
//        @DisplayName("should return all zzzs from repository")
//        void findAll_returns_list_from_repo() {
//            when(zzzRepository.findAll()).thenReturn(List.of(zzz1, zzz2));
//
//            List<XXX> all = zzzService.findAll();
//
//            assertThat(all).containsExactly(zzz1, zzz2);
//            verify(zzzRepository).findAll();
//            verifyNoMoreInteractions(zzzRepository);
//        }
//    }
//
//    @Nested
//    @DisplayName("findById()")
//    class FindByIdTests {
//        @Test
//        @DisplayName("should return zzz when found")
//        void findById_ok_returns_optional() {
//            when(zzzRepository.findById(1)).thenReturn(Optional.of(zzz1));
//
//            Optional<XXX> result = zzzService.findById(1);
//
//            assertThat(result).contains(zzz1);
//            verify(zzzRepository).findById(1);
//            verifyNoMoreInteractions(zzzRepository);
//        }
//
//        @Test
//        @DisplayName("should wrap repo exception in PersistException")
//        void findById_wraps_any_repo_exception_in_PersistException() {
//            when(zzzRepository.findById(1)).thenThrow(new RuntimeException("boom"));
//
//            assertThrows(PersistException.class, () -> zzzService.findById(1));
//            verifyNoMoreInteractions(zzzRepository);
//        }
//    }
//
//    @Nested
//    @DisplayName("deleteById()")
//    class DeleteByIdTests {
//        @Test
//        @DisplayName("should delete zzz when it exists")
//        void deleteById_when_exists_deletes() {
//            when(zzzRepository.existsById(1)).thenReturn(true);
//
//            zzzService.deleteById(1);
//
//            verify(zzzRepository).existsById(1);
//            verify(zzzRepository).deleteById(1);
//            verifyNoMoreInteractions(zzzRepository);
//        }
//
//        @Test
//        @DisplayName("should throw NotFoundByException when zzz does not exist")
//        void deleteById_when_not_exists_throws_NotFoundByException() {
//            when(zzzRepository.existsById(1)).thenReturn(false);
//
//            assertThrows(NotFoundByException.class, () -> zzzService.deleteById(1));
//            verify(zzzRepository, never()).deleteById(anyInt());
//            verifyNoMoreInteractions(zzzRepository);
//        }
//
//        @Test
//        @DisplayName("should wrap unexpected repo exception in PersistException")
//        void deleteById_wraps_other_exceptions_in_PersistException() {
//            when(zzzRepository.existsById(1)).thenReturn(true);
//            doThrow(new RuntimeException("constraint fail")).when(zzzRepository).deleteById(1);
//
//            assertThrows(PersistException.class, () -> zzzService.deleteById(1));
//            verifyNoMoreInteractions(zzzRepository);
//        }
//    }
//
//    @Nested
//    @DisplayName("update()")
//    class UpdateTests {
//        @Test
//        @DisplayName("should update and return zzz when it exists")
//        void update_when_exists_saves_and_returns() {
//            when(zzzRepository.existsById(1)).thenReturn(true);
//            when(zzzRepository.save(zzz1)).thenReturn(zzz1);
//
//            XXX result = zzzService.update(zzz1);
//
//            assertEquals(1, result.getId());
//            verify(zzzRepository).existsById(1);
//            verify(zzzRepository).save(zzz1);
//            verifyNoMoreInteractions(zzzRepository);
//        }
//
//        @Test
//        @DisplayName("should throw NotFoundByException when zzz does not exist")
//        void update_when_not_exists_throws_NotFoundByException() {
//            when(zzzRepository.existsById(1)).thenReturn(false);
//
//            assertThrows(NotFoundByException.class, () -> zzzService.update(zzz1));
//            verify(zzzRepository).existsById(1);
//            verifyNoMoreInteractions(zzzRepository);
//        }
//
//        @Test
//        @DisplayName("should wrap repo exception in PersistException")
//        void update_wraps_other_exceptions_in_PersistException() {
//            when(zzzRepository.existsById(1)).thenReturn(true);
//            when(zzzRepository.save(zzz1)).thenThrow(new RuntimeException("db error"));
//
//            assertThrows(PersistException.class, () -> zzzService.update(zzz1));
//            verify(zzzRepository).existsById(1);
//            verify(zzzRepository).save(zzz1);
//            verifyNoMoreInteractions(zzzRepository);
//        }
//    }
//}