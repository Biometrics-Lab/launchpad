package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ConditionRepository;
import com.biolab.launchpad.internal.repository.model.Condition;
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
@DisplayName("ConditionService Unit Tests")
class ConditionServiceTest {

    @Mock
    private ConditionRepository conditionRepository;

    @InjectMocks
    private ConditionService conditionService;

    private Condition condition1Input;
    private Condition condition1;
    private Condition condition2;

    @BeforeEach
    void setUp() {
        condition1Input = Condition.builder()
                .name("Hitting from T")
                .build();

        condition1 = Condition.builder()
                .id(1)
                .name("Hitting from T")
                .build();

        condition2 = Condition.builder()
                .id(2)
                .name("Pitching machine 60ft")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(conditionRepository.save(condition1Input)).thenReturn(condition1);

            Condition result = conditionService.create(condition1Input);

            assertThat(result).isSameAs(condition1);
            verify(conditionRepository).save(condition1Input);
            verifyNoMoreInteractions(conditionRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(conditionRepository.save(condition1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> conditionService.create(condition1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(conditionRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all conditions from repository")
        void findAll_returns_list_from_repo() {
            when(conditionRepository.findAll()).thenReturn(List.of(condition1, condition2));

            List<Condition> all = conditionService.findAll();

            assertThat(all).containsExactly(condition1, condition2);
            verify(conditionRepository).findAll();
            verifyNoMoreInteractions(conditionRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return condition when found")
        void findById_ok_returns_optional() {
            when(conditionRepository.findById(1)).thenReturn(Optional.of(condition1));

            Optional<Condition> result = conditionService.findById(1);

            assertThat(result).contains(condition1);
            verify(conditionRepository).findById(1);
            verifyNoMoreInteractions(conditionRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(conditionRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> conditionService.findById(1));
            verifyNoMoreInteractions(conditionRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete condition when it exists")
        void deleteById_when_exists_deletes() {
            when(conditionRepository.existsById(1)).thenReturn(true);

            conditionService.deleteById(1);

            verify(conditionRepository).existsById(1);
            verify(conditionRepository).deleteById(1);
            verifyNoMoreInteractions(conditionRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when condition does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(conditionRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> conditionService.deleteById(1));
            verify(conditionRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(conditionRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(conditionRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(conditionRepository).deleteById(1);

            assertThrows(PersistException.class, () -> conditionService.deleteById(1));
            verifyNoMoreInteractions(conditionRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return condition when it exists")
        void update_when_exists_saves_and_returns() {
            when(conditionRepository.existsById(1)).thenReturn(true);
            when(conditionRepository.save(condition1)).thenReturn(condition1);

            Condition result = conditionService.update(condition1);

            assertEquals(1, result.getId());
            verify(conditionRepository).existsById(1);
            verify(conditionRepository).save(condition1);
            verifyNoMoreInteractions(conditionRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when condition does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(conditionRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> conditionService.update(condition1));
            verify(conditionRepository).existsById(1);
            verifyNoMoreInteractions(conditionRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(conditionRepository.existsById(1)).thenReturn(true);
            when(conditionRepository.save(condition1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> conditionService.update(condition1));
            verify(conditionRepository).existsById(1);
            verify(conditionRepository).save(condition1);
            verifyNoMoreInteractions(conditionRepository);
        }
    }
}
