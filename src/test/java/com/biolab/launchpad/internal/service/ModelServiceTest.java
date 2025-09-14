package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ModelRepository;
import com.biolab.launchpad.internal.repository.model.Model;
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
@DisplayName("ModelService Unit Tests")
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelService modelService;

    private Model model1Input;
    private Model model1;
    private Model model2;

    @BeforeEach
    void setUp() {
        model1Input = Model.builder()
                .build();

        model1 = Model.builder()
                .id(1)
                .build();

        model2 = Model.builder()
                .id(2)
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        @DisplayName("should save and return entity when successful")
        void create_ok_saves_and_returns_entity() {
            when(modelRepository.save(model1Input)).thenReturn(model1);

            Model result = modelService.create(model1Input);

            assertThat(result).isSameAs(model1);
            verify(modelRepository).save(model1Input);
            verifyNoMoreInteractions(modelRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void create_wraps_any_exception_in_PersistException() {
            when(modelRepository.save(model1Input)).thenThrow(new RuntimeException("db down"));

            PersistException ex = assertThrows(PersistException.class, () -> modelService.create(model1Input));
            assertTrue(ex.getMessage().contains("db down"));
            verifyNoMoreInteractions(modelRepository);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {
        @Test
        @DisplayName("should return all models from repository")
        void findAll_returns_list_from_repo() {
            when(modelRepository.findAll()).thenReturn(List.of(model1, model2));

            List<Model> all = modelService.findAll();

            assertThat(all).containsExactly(model1, model2);
            verify(modelRepository).findAll();
            verifyNoMoreInteractions(modelRepository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {
        @Test
        @DisplayName("should return model when found")
        void findById_ok_returns_optional() {
            when(modelRepository.findById(1)).thenReturn(Optional.of(model1));

            Optional<Model> result = modelService.findById(1);

            assertThat(result).contains(model1);
            verify(modelRepository).findById(1);
            verifyNoMoreInteractions(modelRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void findById_wraps_any_repo_exception_in_PersistException() {
            when(modelRepository.findById(1)).thenThrow(new RuntimeException("boom"));

            assertThrows(PersistException.class, () -> modelService.findById(1));
            verifyNoMoreInteractions(modelRepository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {
        @Test
        @DisplayName("should delete model when it exists")
        void deleteById_when_exists_deletes() {
            when(modelRepository.existsById(1)).thenReturn(true);

            modelService.deleteById(1);

            verify(modelRepository).existsById(1);
            verify(modelRepository).deleteById(1);
            verifyNoMoreInteractions(modelRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when model does not exist")
        void deleteById_when_not_exists_throws_NotFoundByException() {
            when(modelRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> modelService.deleteById(1));
            verify(modelRepository, never()).deleteById(anyInt());
            verifyNoMoreInteractions(modelRepository);
        }

        @Test
        @DisplayName("should wrap unexpected repo exception in PersistException")
        void deleteById_wraps_other_exceptions_in_PersistException() {
            when(modelRepository.existsById(1)).thenReturn(true);
            doThrow(new RuntimeException("constraint fail")).when(modelRepository).deleteById(1);

            assertThrows(PersistException.class, () -> modelService.deleteById(1));
            verifyNoMoreInteractions(modelRepository);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {
        @Test
        @DisplayName("should update and return model when it exists")
        void update_when_exists_saves_and_returns() {
            when(modelRepository.existsById(1)).thenReturn(true);
            when(modelRepository.save(model1)).thenReturn(model1);

            Model result = modelService.update(model1);

            assertEquals(1, result.getId().intValue());
            verify(modelRepository).existsById(1);
            verify(modelRepository).save(model1);
            verifyNoMoreInteractions(modelRepository);
        }

        @Test
        @DisplayName("should throw NotFoundByException when model does not exist")
        void update_when_not_exists_throws_NotFoundByException() {
            when(modelRepository.existsById(1)).thenReturn(false);

            assertThrows(NotFoundByException.class, () -> modelService.update(model1));
            verify(modelRepository).existsById(1);
            verifyNoMoreInteractions(modelRepository);
        }

        @Test
        @DisplayName("should wrap repo exception in PersistException")
        void update_wraps_other_exceptions_in_PersistException() {
            when(modelRepository.existsById(1)).thenReturn(true);
            when(modelRepository.save(model1)).thenThrow(new RuntimeException("db error"));

            assertThrows(PersistException.class, () -> modelService.update(model1));
            verify(modelRepository).existsById(1);
            verify(modelRepository).save(model1);
            verifyNoMoreInteractions(modelRepository);
        }
    }
}