package vvu.centrauthz.errorhandlers;

import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.web.router.exceptions.UnsatisfiedHeaderRouteException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vvu.centrauthz.exceptions.*;
import vvu.centrauthz.utilities.StringTools;

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
    public Mono<HttpResponse<vvu.centrauthz.models.Error>> handleException(HttpRequest<?> request, ConstraintViolationException exception) {

        var details = StringTools.exceptionToMap(exception);

        var error = vvu.centrauthz.models.Error.builder()
            .code("VALIDATION_ERROR")
            .details(details)
            .build();

        return Mono.just(
            HttpResponse.status(HttpStatus.BAD_REQUEST)
                .body(error));
    }

    // UnsatisfiedHeaderRouteException
    @Error(global = true)
    public Mono<HttpResponse<vvu.centrauthz.models.Error>> handleException(HttpRequest<?> request, UnsatisfiedHeaderRouteException exception) {
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

        switch (exception) {
            case NotFoundError notFoundError -> {
                return Mono.just(
                    HttpResponse.status(HttpStatus.NOT_FOUND)
                        .body(notFoundError.getError())
                );
            }
            case ConflictError conflictError -> {
                return Mono.just(
                    HttpResponse.status(HttpStatus.CONFLICT)
                        .body(conflictError.getError())
                );
            }
            case BadRequestError badRequestError -> {
                return Mono.just(
                    HttpResponse.status(HttpStatus.BAD_REQUEST)
                        .body(badRequestError.getError())
                );
            }
            case NotImplementedError notImplementedError -> {
                return Mono.just(
                    HttpResponse.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(notImplementedError.getError())
                );
            }
            default -> {
            }
        }

        return Mono.just(
                HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(exception.getError())
        );
    }
}
