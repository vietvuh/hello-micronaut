package vvu.centrauthz.domains.resources.grpc.utilities;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.rpc.Status;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import lombok.extern.slf4j.Slf4j;
import resources.Resources;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.*;
import vvu.centrauthz.models.Error;
import vvu.centrauthz.utilities.JsonTools;
import com.google.protobuf.util.JsonFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

        return Resource.builder()
                .id(UUID.fromString(input.getId()))
                .applicationKey(input.getApplicationKey())
                .type(input.getType())
                .ownerId(UUID.fromString(input.getOwnerId()))
                .parentId(UUID.fromString(input.getParentId()))
                .sharedWith(input.getSharedWithList().stream().map(UUID::fromString).toList())
                .tags(input.getTagsList())
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

    static Struct toStruct(JsonMapper mapper, JsonNode input) {

        try {
            Struct.Builder struct = Struct.newBuilder();
            JsonFormat.parser().merge(JsonTools.toString(mapper, input), struct);
            return struct.build();
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalJsonValue(e);
        }
    }

    public static Resources.Resource convert(JsonMapper mapper, Resource input) {

        var builder = Resources.Resource.newBuilder()
                .setId(input.id().toString())
                .setApplicationKey(input.applicationKey())
                .setType(input.type());
        Optional.ofNullable(input.ownerId())
                .ifPresent( v -> builder.setOwnerId(v.toString()));
        Optional.ofNullable(input.parentId())
                .ifPresent( v -> builder.setParentId(v.toString()));
        Optional.ofNullable(input.sharedWith())
                .ifPresent( v -> builder.addAllSharedWith(v.stream().map(UUID::toString).toList()));
        Optional.ofNullable(input.tags())
                .ifPresent(builder::addAllTags);
        Optional.ofNullable(input.tags())
                .ifPresent(builder::addAllTags);
        Optional.ofNullable(input.createdAt())
                .ifPresent(builder::setCreatedAt);
        Optional.ofNullable(input.createdBy())
                .ifPresent( v -> builder.setCreatedBy(v.toString()));
        Optional.ofNullable(input.updatedAt())
                .ifPresent(builder::setUpdatedAt);
        Optional.ofNullable(input.updatedBy())
                .ifPresent( v -> builder.setUpdatedBy(v.toString()));

        Optional.ofNullable(input.details()).ifPresent(d -> builder.setDetails(toStruct(mapper, d)));

        return builder.build();
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
