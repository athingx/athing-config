package io.github.athingx.athing.config.thing.builder;

import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.impl.ThingConfigImpl;
import io.github.athingx.athing.config.thing.impl.binder.PullOpBinder;
import io.github.athingx.athing.config.thing.impl.binder.PushOpBinder;
import io.github.athingx.athing.thing.api.Thing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.athingx.athing.thing.api.function.CompletableFutureFn.tryCatchComplete;

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

        final var group = thing.op().binding();
        final var listeners = ConcurrentHashMap.<ConfigListener>newKeySet();
        if (null != listener) {
            listeners.add(listener);
        }

        group.bindFor(new PushOpBinder(thing, option, listeners));
        final var pullCallFuture = group.bindFor(new PullOpBinder(thing, option));

        return group
                .commit()
                .thenCompose(binder -> tryCatchComplete(() -> new ThingConfigImpl(
                        thing,
                        listeners,
                        pullCallFuture.get()
                )));
    }

}
