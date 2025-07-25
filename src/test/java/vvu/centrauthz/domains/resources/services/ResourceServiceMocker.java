package vvu.centrauthz.domains.resources.services;

import org.mockito.Mockito;
import vvu.centrauthz.domains.resources.repositories.ResourceReadable;
import vvu.centrauthz.domains.resources.repositories.ResourceRemovable;
import vvu.centrauthz.domains.resources.repositories.ResourceWritable;

import java.util.Optional;
import java.util.function.Consumer;

public class ResourceServiceMocker {

    private final ResourceReadable resourceReadableMock = Mockito.mock(ResourceReadable.class);
    private final ResourceWritable resourceWritableMock = Mockito.mock(ResourceWritable.class);
    private final ResourceRemovable resourceRemovableMock = Mockito.mock(ResourceRemovable.class);
    private Consumer<ResourceReadable> readableVerifier = null;
    private Consumer<ResourceWritable> writableVerifier = null;
    private Consumer<ResourceRemovable> removableVerifier = null;


    ResourceService build() {
        return new ResourceService(resourceReadableMock, resourceWritableMock, resourceRemovableMock);
    }

    ResourceServiceMocker forReadable(Consumer<ResourceReadable> consumer) {
        consumer.accept(resourceReadableMock);
        return this;
    }

    ResourceServiceMocker forWritable(Consumer<ResourceWritable> consumer) {
        consumer.accept(resourceWritableMock);
        return this;
    }

    ResourceServiceMocker forRemovable(Consumer<ResourceRemovable> consumer) {
        consumer.accept(resourceRemovableMock);
        return this;
    }

    ResourceServiceMocker withReadableVerifier(Consumer<ResourceReadable> verifier) {
        this.readableVerifier = verifier;
        return this;
    }

    ResourceServiceMocker withWritableVerifier(Consumer<ResourceWritable> verifier) {
        this.writableVerifier = verifier;
        return this;
    }

    ResourceServiceMocker withRemovableVerifier(Consumer<ResourceRemovable> verifier) {
        this.removableVerifier = verifier;
        return this;
    }

    void verify() {
        Optional
                .ofNullable(readableVerifier)
                .ifPresent(v -> v.accept(resourceReadableMock));
        Optional
                .ofNullable(writableVerifier)
                .ifPresent(v -> v.accept(resourceWritableMock));
        Optional
                .ofNullable(removableVerifier)
                .ifPresent(v -> v.accept(resourceRemovableMock));
    }

    static ResourceServiceMocker create() {
        return new ResourceServiceMocker();
    }
}
