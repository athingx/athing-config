package io.github.athingx.athing.config.thing.impl;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.Scope;
import io.github.athingx.athing.config.thing.builder.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.util.HttpUtils;
import io.github.athingx.athing.config.thing.impl.util.StringUtils;
import io.github.athingx.athing.thing.api.Thing;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.athingx.athing.thing.api.function.CompletableFutureFn.tryCatchExecute;

/**
 * 配置实现
 */
public class ConfigImpl implements Config {

    private final Thing thing;
    private final Meta meta;
    private final Scope scope;
    private final ThingConfigOption option;
    private final AtomicReference<CompletableFuture<String>> futureRef = new AtomicReference<>();

    public ConfigImpl(Thing thing, Meta meta, Scope scope, ThingConfigOption option) {
        this.thing = thing;
        this.meta = meta;
        this.scope = scope;
        this.option = option;
    }

    @Override
    public String getConfigId() {
        return meta.configId();
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    private CompletableFuture<String> initFuture() {
        return tryCatchExecute(future -> thing.executor().execute(() -> {

            try {

                // 获取配置文件内容
                final String content = HttpUtils.getAsString(
                        new URL(meta.configURL()),
                        option.getConnectTimeoutMs(),
                        option.getTimeoutMs()
                );

                // 校验获取的配置文件内容
                final String expect = meta.configCHS().toUpperCase();
                final String actual = StringUtils.signBySHA256(content).toUpperCase();
                if (!Objects.equals(expect, actual)) {
                    throw new Exception(
                            "get config: %s occur checksum failure, expect: %s but actual: %s".formatted(
                                    getConfigId(),
                                    expect,
                                    actual
                            )
                    );
                }

                // 设置结果
                future.complete(content);

            } catch (Exception cause) {
                future.completeExceptionally(cause);
            }

        }));
    }

    @Override
    public CompletableFuture<String> getContent() {
        var future = futureRef.get();
        if (null != future) {
            return future;
        }
        synchronized (this) {
            if (null != (future = futureRef.get())) {
                return future;
            }
            futureRef.set(future = initFuture());
            return future;
        }

    }

}
