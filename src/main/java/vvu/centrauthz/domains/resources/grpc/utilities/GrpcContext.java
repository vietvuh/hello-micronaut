package vvu.centrauthz.domains.resources.grpc.utilities;

import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import vvu.centrauthz.exceptions.EUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

public class GrpcContext<T> {
    private Logger logger;
    private final StreamObserver<T> responseObserver;

    public GrpcContext(StreamObserver<T> responseObserver) {
        this.responseObserver = Objects.requireNonNull(responseObserver);
    }

    public GrpcContext<T> withLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    private Throwable getError(Throwable error) {
        if (error instanceof CompletionException ex) {
            return Objects.nonNull(ex.getCause()) ? ex.getCause() : ex;
        }
        return error;
    }

    private void processError(Throwable error) {
        Optional.ofNullable(logger)
                .ifPresent(l -> l.error(ExceptionUtils.getStackTrace(error)));

        var ex = getError(error);
        var appError = GrpcUtils.toAppError(ex);
        var status = GrpcUtils.toStatus(appError);
        responseObserver.onError(StatusProto.toStatusRuntimeException(status));
    }

    public void execute(Supplier<CompletableFuture<T>> supplier) {
        var start = System.currentTimeMillis();
        try {
            supplier
                .get()
                .whenComplete(( value, error) ->  {
                    if (Objects.nonNull(error)) {
                        processError(error);
                    } else {
                        responseObserver.onNext(value);
                    }
                    responseObserver.onCompleted();
                    var end = System.currentTimeMillis() - start;
                    Optional.ofNullable(logger)
                            .ifPresent(l -> l.info("Execution time: {} ms", end));
            });
        } catch (Exception e) {
            processError(e);
            responseObserver.onCompleted();
        }
    }

    public static <T> GrpcContext<T> create(StreamObserver<T> responseObserver) {
        return new GrpcContext<>(responseObserver);
    }
}
