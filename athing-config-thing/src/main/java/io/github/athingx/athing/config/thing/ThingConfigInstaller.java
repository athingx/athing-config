package io.github.athingx.athing.config.thing;

import io.github.athingx.athing.config.thing.impl.ThingConfigImpl;
import io.github.athingx.athing.config.thing.impl.binding.ThingOpBindingForPuller;
import io.github.athingx.athing.config.thing.impl.binding.ThingOpBindingForPusher;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.plugin.ThingPluginInstaller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ThingConfigInstaller implements ThingPluginInstaller<ThingConfig> {

    private ThingConfigOption option = new ThingConfigOption();
    private ConfigListener listener;

    public ThingConfigInstaller option(ThingConfigOption option) {
        this.option = option;
        return this;
    }

    public ThingConfigInstaller listener(ConfigListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Meta<ThingConfig> meta() {
        return new Meta<>(ThingConfig.THING_CONFIG_ID, ThingConfig.class);
    }

    @Override
    public CompletableFuture<ThingConfig> install(Thing thing) {
        Objects.requireNonNull(option, "option is required!");
        Objects.requireNonNull(listener, "listener is required!");
        final var pullerF = new ThingOpBindingForPuller(option).bind(thing);
        final var pusherF = new ThingOpBindingForPusher(option, listener).bind(thing);
        return CompletableFuture.allOf(pullerF, pusherF)
                .thenApply(v -> new ThingConfigImpl(
                        listener,
                        pullerF.join(),
                        pusherF.join()
                ));
    }

}
