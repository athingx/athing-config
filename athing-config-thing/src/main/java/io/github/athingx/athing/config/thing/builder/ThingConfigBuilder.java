package io.github.athingx.athing.config.thing.builder;

import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.impl.ThingConfigImpl;
import io.github.athingx.athing.config.thing.impl.binding.BindingForPull;
import io.github.athingx.athing.config.thing.impl.binding.BindingForPush;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpGroupBind;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.athingx.athing.thing.api.util.CompletableFutureUtils.tryCatchCompleted;

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

        final OpGroupBind group = thing.op().group();
        final Set<ConfigListener> listeners = ConcurrentHashMap.newKeySet();
        if (null != listener) {
            listeners.add(listener);
        }

        new BindingForPush(thing, option, listeners).binding(group);
        final var pullCallerFuture = new BindingForPull(thing, option).binding(group);

        return group
                .commit()
                .thenCompose(binder -> tryCatchCompleted(() -> new ThingConfigImpl(
                        thing,
                        listeners,
                        pullCallerFuture.get()
                )));
    }

}
