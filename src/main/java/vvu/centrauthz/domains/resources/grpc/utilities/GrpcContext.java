package vvu.centrauthz.domains.resources.grpc.utilities;

import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    private void processError(Throwable error) {
        Optional.ofNullable(logger)
                .ifPresent(l -> l.error(error.getMessage(), error));

        var appError = GrpcUtils.toAppError(error);
        var status = GrpcUtils.toStatus(appError);
        StatusRuntimeException ex = StatusProto.toStatusRuntimeException(status);
        responseObserver.onError(ex);
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
                    var end = System.currentTimeMillis() - start;
                    Optional.ofNullable(logger)
                            .ifPresent(l -> l.info("Execution time: {} ms", end));
            });
        } catch (Exception e) {
            processError(e);
        }
    }

    public static <T> GrpcContext<T> create(StreamObserver<T> responseObserver) {
        return new GrpcContext<>(responseObserver);
    }
}
