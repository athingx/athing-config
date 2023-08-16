package io.github.athingx.athing.config.thing.impl;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigListener;
import io.github.athingx.athing.config.thing.ThingConfigurator;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.op.ThingOpCaller;

import java.util.concurrent.CompletableFuture;

/**
 * 设备配置实现
 */
public class ThingConfiguratorImpl implements ThingConfigurator {

    private final ThingConfigListener listener;

    private final ThingOpCaller<Pull, ThingConfig> puller;

    public ThingConfiguratorImpl(final ThingConfigListener listener,
                                 final ThingOpCaller<Pull, ThingConfig> puller) {
        this.listener = listener;
        this.puller = puller;
    }

    @Override
    public CompletableFuture<Void> update(ThingConfig.Scope scope) {
        return fetch(scope).thenAccept(listener::apply);
    }

    @Override
    public CompletableFuture<ThingConfig> fetch(ThingConfig.Scope scope) {
        return puller.call(new Pull(scope));
    }

}
