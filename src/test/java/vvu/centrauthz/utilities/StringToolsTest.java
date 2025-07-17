package vvu.centrauthz.utilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StringToolsTest {

    @Test
    void getNameFromPath_nullOrEmpty_string() {
        String empty = " ";
        assertSame(empty, StringTools.getNameFromPath(empty, '.'));
    }

    @Test
    void getNameFromPath_noSeparator_string() {
        String testS = UUID.randomUUID().toString();
        assertSame(testS, StringTools.getNameFromPath(testS, '.'));
    }

    @Test
    void getNameFromPath_havingSeparator_string() {
        String testS = "updResource.resource.ownerId";
        assertEquals("ownerId", StringTools.getNameFromPath(testS, '.'));
        testS = "ownerId";
        assertEquals("ownerId", StringTools.getNameFromPath(testS, '.'));
        testS = ".";
        assertEquals("", StringTools.getNameFromPath(testS, '.'));
    }

    @Test
    void testIsBlank() {
        assertTrue(StringTools.isBlank(null));
        assertTrue(StringTools.isBlank("  "));
        assertFalse(StringTools.isBlank("  XX"));
    }

    @Test
    void testExceptionToMap() {

        ConstraintViolation<String> violation1 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation1.getMessage()).thenReturn("violation1 message");
        var path1 = Mockito.mock(Path.class);
        Mockito.when(path1.toString()).thenReturn("violation1.prop1");
        Mockito.when(violation1.getPropertyPath()).thenReturn(path1);

        ConstraintViolation<String> violation2 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation2.getMessage()).thenReturn("violation2 message");
        var path2 = Mockito.mock(Path.class);
        Mockito.when(path2.toString()).thenReturn("violation2.prop2");
        Mockito.when(violation2.getPropertyPath()).thenReturn(path2);

        ConstraintViolation<String> violation3 = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation3.getMessage()).thenReturn("violation3 message");
        var path3 = Mockito.mock(Path.class);
        Mockito.when(path3.toString()).thenReturn("violation2.prop2");
        Mockito.when(violation3.getPropertyPath()).thenReturn(path3);

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation1, violation2, violation3));

        var map = StringTools.exceptionToMap(exception);
        assertEquals(2, map.size());
        assertEquals("violation1 message", map.get("prop1"));
        assertTrue(List.of("violation2 message", "violation3 message").contains(map.get("prop2")));

    }
}
