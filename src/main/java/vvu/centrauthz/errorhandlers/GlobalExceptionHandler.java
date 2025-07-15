package vvu.centrauthz.errorhandlers;

import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vvu.centrauthz.exceptions.AppError;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.exceptions.ConflictError;
import vvu.centrauthz.exceptions.NotFoundError;

/**
 * Global exception handler for all controller errors.
 */
@Controller
@Slf4j
public class GlobalExceptionHandler {

    @Error(global = true)
    public Mono<HttpResponse<vvu.centrauthz.models.Error>> handleException(HttpRequest<?> request, Exception exception) {
        log.error("Unhandled exception occurred", exception);
        return Mono.just(
                HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(vvu.centrauthz.models.Error.builder()
                                .code("INTERNAL_SERVER_ERROR")
                                .message(exception.getMessage())
                                .build())
        );
    }

    @Error(global = true)
    public Mono<HttpResponse<vvu.centrauthz.models.Error>> handleException(HttpRequest<?> request, ConversionErrorException exception) {
        log.error("ConversionErrorException: {}", exception.getMessage(), exception);
        return Mono.just(
                HttpResponse.status(HttpStatus.BAD_REQUEST)
                        .body(vvu.centrauthz.models.Error.builder()
                                .code("BAD_REQUEST")
                                .message(exception.getMessage())
                                .build())
        );
    }

    @Error(global = true)
    public Mono<HttpResponse<vvu.centrauthz.models.Error>> handleException(HttpRequest<?> request, AppError exception) {
        log.error("AppError: {}", exception.toString(), exception);

        if (exception instanceof NotFoundError) {
            return Mono.just(
                    HttpResponse.status(HttpStatus.NOT_FOUND)
                            .body(exception.getError())
            );
        }

        if (exception instanceof ConflictError) {
            return Mono.just(
                    HttpResponse.status(HttpStatus.CONFLICT)
                            .body(exception.getError())
            );
        }

        if (exception instanceof BadRequestError) {
            return Mono.just(
                    HttpResponse.status(HttpStatus.BAD_REQUEST)
                            .body(exception.getError())
            );
        }

        // Handle NOT_IMPLEMENTED errors with 503 status
        if ("NOT_IMPLEMENTED".equals(exception.getError().code())) {
            return Mono.just(
                    HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(exception.getError())
            );
        }

        return Mono.just(
                HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(exception.getError())
        );
    }
}
