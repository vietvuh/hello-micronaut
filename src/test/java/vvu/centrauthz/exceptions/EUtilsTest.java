package vvu.centrauthz.exceptions;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EUtilsTest {

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("should throw IllegalStateException when trying to instantiate EUtils")
        void shouldThrowIllegalStateExceptionWhenTryingToInstantiateEUtils() {
            // When & Then
            assertThrows(IllegalStateException.class, EUtils::new);
        }
    }

    @Nested
    @DisplayName("createNotFoundError method tests")
    class CreateNotFoundErrorTests {

        @Test
        @DisplayName("should create NotFoundError with correct code and message")
        void shouldCreateNotFoundErrorWithCorrectCodeAndMessage() {
            // Given
            String message = "Resource not found";

            // When
            NotFoundError error = EUtils.createNotFoundError(message);

            // Then
            assertNotNull(error);
            assertEquals("NOT_FOUND", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }

        @Test
        @DisplayName("should create NotFoundError with null message")
        void shouldCreateNotFoundErrorWithNullMessage() {
            // Given
            String message = null;

            // When
            NotFoundError error = EUtils.createNotFoundError(message);

            // Then
            assertNotNull(error);
            assertEquals("NOT_FOUND", error.getError().code());
            assertNull(error.getError().message());
            assertNull(error.getMessage());
        }

        @Test
        @DisplayName("should create NotFoundError with empty message")
        void shouldCreateNotFoundErrorWithEmptyMessage() {
            // Given
            String message = "";

            // When
            NotFoundError error = EUtils.createNotFoundError(message);

            // Then
            assertNotNull(error);
            assertEquals("NOT_FOUND", error.getError().code());
            assertEquals("", error.getError().message());
            assertEquals("", error.getMessage());
        }

        @Test
        @DisplayName("should create NotFoundError with whitespace message")
        void shouldCreateNotFoundErrorWithWhitespaceMessage() {
            // Given
            String message = "   ";

            // When
            NotFoundError error = EUtils.createNotFoundError(message);

            // Then
            assertNotNull(error);
            assertEquals("NOT_FOUND", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }

        @Test
        @DisplayName("should create NotFoundError with long message")
        void shouldCreateNotFoundErrorWithLongMessage() {
            // Given
            String message = "This is a very long error message that contains many words and characters to test that the method can handle long messages properly without any issues";

            // When
            NotFoundError error = EUtils.createNotFoundError(message);

            // Then
            assertNotNull(error);
            assertEquals("NOT_FOUND", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }

        @Test
        @DisplayName("should create NotFoundError with special characters in message")
        void shouldCreateNotFoundErrorWithSpecialCharactersInMessage() {
            // Given
            String message = "Resource with ID '12345' not found! @#$%^&*()";

            // When
            NotFoundError error = EUtils.createNotFoundError(message);

            // Then
            assertNotNull(error);
            assertEquals("NOT_FOUND", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }
    }

    @Nested
    @DisplayName("createBadRequestError method tests")
    class CreateBadRequestErrorTests {

        @Test
        @DisplayName("should create BadRequestError with correct code and message")
        void shouldCreateBadRequestErrorWithCorrectCodeAndMessage() {
            // Given
            String message = "Invalid request parameters";

            // When
            BadRequestError error = EUtils.createBadRequestError(message);

            // Then
            assertNotNull(error);
            assertEquals("BAD_REQUEST", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }

        @Test
        @DisplayName("should create BadRequestError with null message")
        void shouldCreateBadRequestErrorWithNullMessage() {
            // Given
            String message = null;

            // When
            BadRequestError error = EUtils.createBadRequestError(message);

            // Then
            assertNotNull(error);
            assertEquals("BAD_REQUEST", error.getError().code());
            assertNull(error.getError().message());
            assertNull(error.getMessage());
        }

        @Test
        @DisplayName("should create BadRequestError with empty message")
        void shouldCreateBadRequestErrorWithEmptyMessage() {
            // Given
            String message = "";

            // When
            BadRequestError error = EUtils.createBadRequestError(message);

            // Then
            assertNotNull(error);
            assertEquals("BAD_REQUEST", error.getError().code());
            assertEquals("", error.getError().message());
            assertEquals("", error.getMessage());
        }

        @Test
        @DisplayName("should create BadRequestError with whitespace message")
        void shouldCreateBadRequestErrorWithWhitespaceMessage() {
            // Given
            String message = "   ";

            // When
            BadRequestError error = EUtils.createBadRequestError(message);

            // Then
            assertNotNull(error);
            assertEquals("BAD_REQUEST", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }

        @Test
        @DisplayName("should create BadRequestError with long message")
        void shouldCreateBadRequestErrorWithLongMessage() {
            // Given
            String message = "This is a very long error message for bad request that contains many validation errors and detailed explanations about what went wrong with the request";

            // When
            BadRequestError error = EUtils.createBadRequestError(message);

            // Then
            assertNotNull(error);
            assertEquals("BAD_REQUEST", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }

        @Test
        @DisplayName("should create BadRequestError with special characters in message")
        void shouldCreateBadRequestErrorWithSpecialCharactersInMessage() {
            // Given
            String message = "Invalid JSON format: { \"field\": \"value\" } @#$%^&*()";

            // When
            BadRequestError error = EUtils.createBadRequestError(message);

            // Then
            assertNotNull(error);
            assertEquals("BAD_REQUEST", error.getError().code());
            assertEquals(message, error.getError().message());
            assertEquals(message, error.getMessage());
        }
    }

    @Nested
    @DisplayName("Error type verification tests")
    class ErrorTypeVerificationTests {

        @Test
        @DisplayName("createNotFoundError should return instance of NotFoundError")
        void createNotFoundErrorShouldReturnInstanceOfNotFoundError() {
            // When
            NotFoundError error = EUtils.createNotFoundError("test message");

            // Then
            assertInstanceOf(NotFoundError.class, error);
            assertInstanceOf(AppError.class, error);
            assertInstanceOf(RuntimeException.class, error);
        }

        @Test
        @DisplayName("createBadRequestError should return instance of BadRequestError")
        void createBadRequestErrorShouldReturnInstanceOfBadRequestError() {
            // When
            BadRequestError error = EUtils.createBadRequestError("test message");

            // Then
            assertInstanceOf(BadRequestError.class, error);
            assertInstanceOf(AppError.class, error);
            assertInstanceOf(RuntimeException.class, error);
        }
    }

    @Nested
    @DisplayName("Method behavior consistency tests")
    class MethodBehaviorConsistencyTests {

        @Test
        @DisplayName("multiple calls with same message should create equivalent errors")
        void multipleCallsWithSameMessageShouldCreateEquivalentErrors() {
            // Given
            String message = "Test error message";

            // When
            NotFoundError error1 = EUtils.createNotFoundError(message);
            NotFoundError error2 = EUtils.createNotFoundError(message);

            BadRequestError badError1 = EUtils.createBadRequestError(message);
            BadRequestError badError2 = EUtils.createBadRequestError(message);

            // Then
            assertEquals(error1.getError().code(), error2.getError().code());
            assertEquals(error1.getError().message(), error2.getError().message());
            assertEquals(error1.getMessage(), error2.getMessage());

            assertEquals(badError1.getError().code(), badError2.getError().code());
            assertEquals(badError1.getError().message(), badError2.getError().message());
            assertEquals(badError1.getMessage(), badError2.getMessage());
        }

        @Test
        @DisplayName("errors should have different codes but same message structure")
        void errorsShouldHaveDifferentCodesButSameMessageStructure() {
            // Given
            String message = "Test error message";

            // When
            NotFoundError notFoundError = EUtils.createNotFoundError(message);
            BadRequestError badRequestError = EUtils.createBadRequestError(message);

            // Then
            assertNotEquals(notFoundError.getError().code(), badRequestError.getError().code());
            assertEquals(notFoundError.getError().message(), badRequestError.getError().message());
            assertEquals(notFoundError.getMessage(), badRequestError.getMessage());
        }
    }
}
