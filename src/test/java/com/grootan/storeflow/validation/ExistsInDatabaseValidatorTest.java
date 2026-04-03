package com.grootan.storeflow.validation;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExistsInDatabaseValidatorTest {

    private ExistsInDatabaseValidator validator;
    private EntityManager entityManager;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        validator = new ExistsInDatabaseValidator(entityManager);
        
        ExistsInDatabase annotation = mock(ExistsInDatabase.class);
        doReturn(Object.class).when(annotation).entityClass();
        validator.initialize(annotation);
        
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_whenEntityExists_returnsTrue() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(Object.class, id)).thenReturn(new Object());
        
        assertTrue(validator.isValid(id, context));
    }

    @Test
    void isValid_whenEntityDoesNotExist_returnsFalse() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(Object.class, id)).thenReturn(null);
        
        assertFalse(validator.isValid(id, context));
    }

    @Test
    void isValid_whenValueIsNull_returnsTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void isValid_withStringId_callsEntityManagerCorrectly() {
        String id = "some-string-id";
        when(entityManager.find(Object.class, id)).thenReturn(new Object());
        
        assertTrue(validator.isValid(id, context));
        verify(entityManager).find(Object.class, id);
    }
    
    @Test
    void isValid_withLongId_callsEntityManagerCorrectly() {
        Long id = 123L;
        when(entityManager.find(Object.class, id)).thenReturn(null);
        
        assertFalse(validator.isValid(id, context));
        verify(entityManager).find(Object.class, id);
    }
    
    @Test
    void initialize_setsEntityClass() {
        // Just executing the flow to test initialization sets the correct class context
        ExistsInDatabase annotation = mock(ExistsInDatabase.class);
        doReturn(String.class).when(annotation).entityClass();
        
        validator.initialize(annotation);
        
        when(entityManager.find(String.class, 1L)).thenReturn(new Object());
        assertTrue(validator.isValid(1L, context));
    }
}
