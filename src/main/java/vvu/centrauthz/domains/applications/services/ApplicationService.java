package vvu.centrauthz.domains.applications.services;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.applications.repositories.ApplicationReadable;
import vvu.centrauthz.domains.applications.repositories.ApplicationRemovable;
import vvu.centrauthz.domains.applications.repositories.ApplicationWritable;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.utilities.Context;

@Singleton
@Slf4j
public class ApplicationService {
    private ApplicationReadable readable;
    private ApplicationWritable writable;
    private ApplicationRemovable removable;

    public ApplicationService(ApplicationReadable readable, ApplicationWritable writable, ApplicationRemovable removable) {
        this.readable = readable;
        this.writable = writable;
        this.removable = removable;
    }

    public Mono<Resource> get(String appKey, Context context) {
        return Mono.error(EUtils.createNotImplementedError());
    }

    public Mono<Void> remove(String appKey, Context context) {
        log.warn("Removing application: {} with context: {}. Not Supported", appKey, context);
        return Mono.error(EUtils.createNotImplementedError());
    }

    public Mono<Void> save(String appKey, Resource resource, Context context) {
        return Mono.error(EUtils.createNotImplementedError());
    }

}
