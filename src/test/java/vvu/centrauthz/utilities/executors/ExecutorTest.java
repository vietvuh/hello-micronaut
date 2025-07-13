package vvu.centrauthz.utilities.executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExecutorTest {

    private static final String SUCCESS_RESULT = "success";
    private static final String ERROR_MESSAGE = "Test exception";
    private static final String FALLBACK_RESULT = "fallback";
    private static final String CUSTOM_FALLBACK = "custom fallback";
    private static final String NEW_RESULT = "new result";
    private static final RuntimeException TEST_EXCEPTION = new RuntimeException(ERROR_MESSAGE);

    private final Supplier<String> successSupplier = () -> SUCCESS_RESULT;
    private final Supplier<String> failingSupplier = () -> {
        throw new RuntimeException(ERROR_MESSAGE);
    };
    private final Function<Throwable, String> errorHandler = e -> FALLBACK_RESULT;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void execute_shouldReturnSupplierResult_whenNoException() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);

        // Given
        when(mockSupplier.get()).thenReturn(SUCCESS_RESULT);
        executor = new Executor<>(mockSupplier, errorHandler);

        // When
        String result = executor.execute();

        // Then
        assertEquals(SUCCESS_RESULT, result);
        verify(mockSupplier).get();
    }

    @Test
    void execute_shouldReturnFallback_whenExceptionThrown() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        // Given
        when(mockSupplier.get()).thenThrow(TEST_EXCEPTION);
        when(mockErrorHandler.apply(any())).thenReturn(FALLBACK_RESULT);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);
        executor = new Executor<>(mockSupplier, mockErrorHandler);

        // When
        String result = executor.execute();

        // Then
        assertEquals(FALLBACK_RESULT, result);
        verify(mockSupplier).get();
        verify(mockErrorHandler).apply(TEST_EXCEPTION);
    }

    @Test
    void execute_shouldLogError_whenExceptionThrownAndLoggerProvided() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);

        var e = new RuntimeException(new IllegalArgumentException(ERROR_MESSAGE));
        // Given
        when(mockSupplier.get()).thenThrow(e);
        when(mockErrorHandler.apply(any())).thenReturn(FALLBACK_RESULT);
        doNothing().when(mockLogger).error(captor.capture());
        
        executor = new Executor<>(mockSupplier, mockErrorHandler)
                .withLogger(mockLogger);

        // When
        executor.execute();

        // Then
        verify(mockLogger, times(1)).error(anyString());
    }

    @Test
    void withSupplier_shouldReplaceSupplier() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);

        // Given
        when(mockSupplier.get()).thenReturn(NEW_RESULT);
        executor = executor.withSupplier(mockSupplier);

        // When
        String result = executor.execute();

        // Then
        assertEquals(NEW_RESULT, result);
        verify(mockSupplier).get();
    }

    @Test
    void withWhenError_shouldReplaceErrorHandler() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);

        // Given
        when(mockSupplier.get()).thenThrow(TEST_EXCEPTION);
        when(mockErrorHandler.apply(any())).thenReturn(CUSTOM_FALLBACK);
        
        executor = new Executor<>(mockSupplier, errorHandler)
                .withWhenError(mockErrorHandler);

        // When
        String result = executor.execute();

        // Then
        assertEquals(CUSTOM_FALLBACK, result);
        verify(mockErrorHandler).apply(TEST_EXCEPTION);
    }

    @Test
    void withLogger_shouldSetLogger() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);

        ArgumentCaptor<String> eMessage = ArgumentCaptor.forClass(String.class);

        doNothing().when(mockLogger).error(eMessage.capture());

        // Given
        when(mockSupplier.get()).thenThrow(TEST_EXCEPTION);
        when(mockErrorHandler.apply(any())).thenReturn(FALLBACK_RESULT);
        
        executor = new Executor<>(mockSupplier, mockErrorHandler)
                .withLogger(mockLogger);

        // When
        executor.execute();

        // Then
        verify(mockLogger).error(anyString());
        assertEquals(ERROR_MESSAGE, eMessage.getValue());
    }

    @Test
    void execute_shouldNotLog_whenNoLoggerProvided() {

        Logger mockLogger = Mockito.mock(Logger.class);
        Supplier<String> mockSupplier = Mockito.mock(Supplier.class);
        Function<Throwable, String> mockErrorHandler = Mockito.mock(Function.class);
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);
        // Given
        when(mockSupplier.get()).thenThrow(TEST_EXCEPTION);
        when(mockErrorHandler.apply(any())).thenReturn(FALLBACK_RESULT);
        
        executor = new Executor<>(mockSupplier, mockErrorHandler);

        // When
        String result = executor.execute();

        // Then
        verifyNoInteractions(mockLogger);
        assertEquals(FALLBACK_RESULT, result);
    }

    @Test
    void execute_shouldCallSupplierOnlyOnce() {
        // Given
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);
        AtomicBoolean called = new AtomicBoolean(false);
        Supplier<String> supplier = () -> {
            assertFalse(called.getAndSet(true));
            return SUCCESS_RESULT;
        };
        executor = new Executor<>(supplier, errorHandler);

        // When
        executor.execute();
    }

    @Test
    void constructor_shouldThrowNullPointerException_whenSupplierIsNull() {
        assertThrows(NullPointerException.class, () -> new Executor<>(null, errorHandler));
    }

    @Test
    void constructor_shouldThrowNullPointerException_whenErrorHandlerIsNull() {
        assertThrows(NullPointerException.class, () -> new Executor<>(successSupplier, null));
    }

    @Test
    void withSupplier_shouldThrowNullPointerException_whenSupplierIsNull() {
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);
        assertThrows(NullPointerException.class, () -> executor.withSupplier(null));
    }

    @Test
    void withWhenError_shouldThrowNullPointerException_whenErrorHandlerIsNull() {
        Executor<String> executor = new Executor<>(successSupplier, errorHandler);
        assertThrows(NullPointerException.class, () -> executor.withWhenError(null));
    }
}
