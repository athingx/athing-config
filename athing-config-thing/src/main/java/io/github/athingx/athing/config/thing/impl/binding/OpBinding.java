package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.thing.api.Thing;

import java.util.concurrent.CompletableFuture;

public interface OpBinding<T> {

    CompletableFuture<T> bind(Thing thing);

}
