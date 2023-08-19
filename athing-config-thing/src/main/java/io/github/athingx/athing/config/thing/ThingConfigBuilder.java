package io.github.athingx.athing.config.thing;

import io.github.athingx.athing.config.thing.impl.ThingConfigImpl;
import io.github.athingx.athing.config.thing.impl.binding.OpBindingThingConfigPuller;
import io.github.athingx.athing.config.thing.impl.binding.OpBindingThingConfigPusher;
import io.github.athingx.athing.thing.api.Thing;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 设备配置构造
 */
public class ThingConfigBuilder {

    private ThingConfigOption option = new ThingConfigOption();
    private ConfigListener listener;

    /**
     * 设备配置参数
     *
     * @param option 设备配置参数
     * @return this
     */
    public ThingConfigBuilder option(ThingConfigOption option) {
        this.option = option;
        return this;
    }

    /**
     * 设备配置监听器
     *
     * @param listener 设备配置监听器
     * @return this
     */
    public ThingConfigBuilder listener(ConfigListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 构造设备配置
     *
     * @param thing 设备
     * @return 设备配置
     */
    public CompletableFuture<ThingConfig> build(Thing thing) {
        Objects.requireNonNull(option, "option is required!");
        Objects.requireNonNull(listener, "listener is required!");
        final var puller = new OpBindingThingConfigPuller(option).bind(thing);
        final var pusher = new OpBindingThingConfigPusher(option, listener).bind(thing);
        return CompletableFuture.allOf(puller, pusher)
                .thenApply(v -> new ThingConfigImpl(
                        listener,
                        puller.join()
                ));
    }

}
