package io.github.athingx.athing.config.thing;

import io.github.athingx.athing.config.thing.impl.ThingConfiguratorImpl;
import io.github.athingx.athing.config.thing.impl.binding.OpBindingThingConfigPuller;
import io.github.athingx.athing.config.thing.impl.binding.OpBindingThingConfigPusher;
import io.github.athingx.athing.thing.api.Thing;

import java.util.concurrent.CompletableFuture;

/**
 * 设备配置构造
 */
public class ThingConfigureBuilder {

    private ThingConfigureOption option = new ThingConfigureOption();
    private ThingConfigListener listener;

    /**
     * 设备配置参数
     *
     * @param option 设备配置参数
     * @return this
     */
    public ThingConfigureBuilder option(ThingConfigureOption option) {
        this.option = option;
        return this;
    }

    /**
     * 设备配置监听器
     *
     * @param listener 设备配置监听器
     * @return this
     */
    public ThingConfigureBuilder listener(ThingConfigListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 构造设备配置
     *
     * @param thing 设备
     * @return 设备配置
     */
    public CompletableFuture<ThingConfigurator> build(Thing thing) {
        final var puller = new OpBindingThingConfigPuller(option).bind(thing);
        final var pusher = new OpBindingThingConfigPusher(option, listener).bind(thing);
        return CompletableFuture.allOf(puller, pusher)
                .thenApply(v -> new ThingConfiguratorImpl(
                        listener,
                        puller.join()
                ));
    }

}
