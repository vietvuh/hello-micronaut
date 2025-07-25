package vvu.centrauthz.domains.resources.grpc.utilities;

import com.google.protobuf.Struct;
import com.google.rpc.Status;
import io.micronaut.json.JsonMapper;
import resources.Resources;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.*;
import vvu.centrauthz.models.Error;
import vvu.centrauthz.utilities.JsonTools;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GrpcUtils {
    GrpcUtils() {
        throw new IllegalStateException();
    }

    public static UUID idToUUID(resources.Resources.GetResourceRequest request) {
        try {
            return UUID.fromString(request.getId());
        } catch (RuntimeException e) {

            var error = Error.builder()
                    .code("INVALID_ID")
                    .message("Invalid ID")
                    .build();
            throw new BadRequestError(error);
        }
    }

    public static Resource convert(JsonMapper mapper,Resources.Resource input) {

        var details = JsonTools.toJson(mapper, input.getDetails());

        return Resource.builder()
                .id(UUID.fromString(input.getId()))
                .applicationKey(input.getApplicationKey())
                .type(input.getType())
                .ownerId(UUID.fromString(input.getOwnerId()))
                .parentId(UUID.fromString(input.getParentId()))
                .sharedWith(input.getSharedWithList().stream().map(UUID::fromString).toList())
                .tags(input.getTagsList())
                .details(details)
                .createdAt(input.getCreatedAt())
                .createdBy(UUID.fromString(input.getCreatedBy()))
                .updatedAt(input.getUpdatedAt())
                .updatedBy(UUID.fromString(input.getUpdatedBy()))
                .build();
    }

    public static Resources.GetResourceResponse toGetResourceResponse(JsonMapper mapper, Resource input) {
        return Resources.GetResourceResponse
                .newBuilder()
                .setResource(convert(mapper, input))
                .build();
    }

    public static Resources.Resource convert(JsonMapper mapper, Resource input) {

        String ownerId = Optional
                .ofNullable(input.ownerId())
                .map(UUID::toString)
                .orElse(null);
        String parentId = Optional
                .ofNullable(input.parentId())
                .map(UUID::toString)
                .orElse(null);
        var sharedWith = Optional.ofNullable(input.sharedWith())
                .map(l -> l.stream().map(UUID::toString).toList())
                .orElse(null);
        var details = Optional.ofNullable(input.details())
                .map(m -> JsonTools.toValue(mapper, m, Struct.class))
                .orElse(null);

        return Resources.Resource.newBuilder()
                .setId(input.id().toString())
                .setApplicationKey(input.applicationKey())
                .setType(input.type())
                .setDetails(details)
                .setOwnerId(ownerId)
                .setParentId(parentId)
                .addAllSharedWith(sharedWith)
                .addAllTags(input.tags())
                .build();
    }

    public static Resources.Error convert(Error input) {
        var builder = Resources.Error.newBuilder()
                .setCode(input.code())
                .setMessage(input.message());
        builder.putAllDetails(input.details());
        return builder.build();
    }

    public static  AppError toAppError(Throwable e) {
        if (e instanceof AppError apperror) {
            return apperror;
        } else {
            return new AppError("INTERNAL_ERROR", e);
        }
    }

    static io.grpc.Status appErrorToStatus(AppError error) {
        return switch (error) {
            case BadRequestError badRequestError -> io.grpc.Status.INVALID_ARGUMENT;
            case NotFoundError notFoundError -> io.grpc.Status.NOT_FOUND;
            case ConflictError conflictError -> io.grpc.Status.FAILED_PRECONDITION;
            case IllegalJsonValue illegalJsonValue -> io.grpc.Status.INVALID_ARGUMENT;
            case NotImplementedError notImplementedError -> io.grpc.Status.UNIMPLEMENTED;
            case null, default -> io.grpc.Status.INTERNAL;
        };
    }

    public static Status toStatus(AppError error) {
        var status = Status.newBuilder();
        if (Objects.isNull(error)) {
            status.setCode(io.grpc.Status.INTERNAL.getCode().value());
        } else if (Objects.isNull(error.getError())) {
            status.setCode(io.grpc.Status.INTERNAL.getCode().value());
            status.setMessage(error.getMessage());
        } else {
            status.setCode(appErrorToStatus(error).getCode().value());
            status.setMessage(error.getMessage());
            if (Objects.nonNull(error.getError().details())) {
                var errorProto = convert(error.getError());
                status.addDetails(com.google.protobuf.Any.pack(errorProto));
            }
        }

        return status.build();
    }
}
