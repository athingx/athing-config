package io.github.athingx.athing.config.thing.impl;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.op.ThingOpBinder;
import io.github.athingx.athing.thing.api.op.ThingOpCaller;

import java.util.concurrent.CompletableFuture;

/**
 * 设备配置实现
 */
public class ThingConfigImpl implements ThingConfig {

    private final ConfigListener listener;

    private final ThingOpCaller<Pull, Config> puller;
    private final ThingOpBinder pusher;

    public ThingConfigImpl(final ConfigListener listener,
                           final ThingOpCaller<Pull, Config> puller,
                           final ThingOpBinder pusher) {
        this.listener = listener;
        this.puller = puller;
        this.pusher = pusher;
    }

    @Override
    public CompletableFuture<Void> update(Config.Scope scope) {
        return fetch(scope).thenAccept(this::apply);
    }

    @Override
    public CompletableFuture<Config> fetch(Config.Scope scope) {
        return puller.call(new Pull(scope));
    }

    @Override
    public void apply(Config config) {
        listener.apply(config);
    }

    @Override
    public CompletableFuture<Void> uninstall() {
        return CompletableFuture.allOf(
                puller.unbind(),
                pusher.unbind()
        );
    }
}
