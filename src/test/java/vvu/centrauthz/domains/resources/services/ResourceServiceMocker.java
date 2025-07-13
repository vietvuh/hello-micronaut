package vvu.centrauthz.domains.resources.services;

import org.mockito.Mockito;
import vvu.centrauthz.domains.resources.repositories.Readable;
import vvu.centrauthz.domains.resources.repositories.Removable;
import vvu.centrauthz.domains.resources.repositories.Writable;

import java.util.Optional;
import java.util.function.Consumer;

public class ResourceServiceMocker {

    private final vvu.centrauthz.domains.resources.repositories.Readable readableMock = Mockito.mock(vvu.centrauthz.domains.resources.repositories.Readable.class);
    private final Writable writableMock = Mockito.mock(Writable.class);
    private final Removable removableMock = Mockito.mock(Removable.class);
    private Consumer<vvu.centrauthz.domains.resources.repositories.Readable> readableVerifier = null;
    private Consumer<Writable> writableVerifier = null;
    private Consumer<Removable> removableVerifier = null;


    ResourceService build() {
        return new ResourceService(readableMock, writableMock, removableMock);
    }

    ResourceServiceMocker forReadable(Consumer<vvu.centrauthz.domains.resources.repositories.Readable> consumer) {
        consumer.accept(readableMock);
        return this;
    }

    ResourceServiceMocker forWritable(Consumer<Writable> consumer) {
        consumer.accept(writableMock);
        return this;
    }

    ResourceServiceMocker forRemovable(Consumer<Removable> consumer) {
        consumer.accept(removableMock);
        return this;
    }

    ResourceServiceMocker withReadableVerifier(Consumer<Readable> verifier) {
        this.readableVerifier = verifier;
        return this;
    }

    ResourceServiceMocker withWritableVerifier(Consumer<Writable> verifier) {
        this.writableVerifier = verifier;
        return this;
    }

    ResourceServiceMocker withRemovableVerifier(Consumer<Removable> verifier) {
        this.removableVerifier = verifier;
        return this;
    }

    void verify() {
        Optional
                .ofNullable(readableVerifier)
                .ifPresent(v -> v.accept(readableMock));
        Optional
                .ofNullable(writableVerifier)
                .ifPresent(v -> v.accept(writableMock));
        Optional
                .ofNullable(removableVerifier)
                .ifPresent(v -> v.accept(removableMock));
    }

    static ResourceServiceMocker create() {
        return new ResourceServiceMocker();
    }
}
