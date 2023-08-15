package io.github.athingx.athing.config.thing.impl;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigureOption;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.util.HttpUtils;
import io.github.athingx.athing.config.thing.impl.util.StringUtils;
import io.github.athingx.athing.thing.api.Thing;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Optional.ofNullable;

/**
 * 配置实现
 */
public class ThingConfigImpl implements ThingConfig {

    private final Thing thing;
    private final Meta meta;
    private final Scope scope;
    private final ThingConfigureOption option;
    private final AtomicReference<CompletableFuture<String>> futureRef = new AtomicReference<>();

    public ThingConfigImpl(Thing thing, ThingConfigureOption option, Scope scope, Meta meta) {
        this.thing = thing;
        this.meta = meta;
        this.scope = scope;
        this.option = option;
    }

    @Override
    public String getId() {
        return meta.id();
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    private static class FetchContentException extends RuntimeException {

        public FetchContentException(String message, Throwable cause) {
            super(message, cause);
        }

        public FetchContentException(String message) {
            super(message);
        }

    }

    private CompletableFuture<String> asyncFetchContent() {
        return CompletableFuture.supplyAsync(() -> {

            try {

                // 获取配置文件内容
                final var content = HttpUtils.getAsString(
                        new URL(meta.url()),
                        option.getConnectTimeoutMs(),
                        option.getTimeoutMs()
                );

                // 校验获取的配置文件内容
                final var expect = meta.sign().toUpperCase();
                final var actual = StringUtils.signBySHA256(content).toUpperCase();
                if (!Objects.equals(expect, actual)) {
                    throw new FetchContentException("config: %s checksum failure, expect: %s but actual: %s".formatted(
                            getId(),
                            expect,
                            actual
                    ));
                }

                return content;

            } catch (FetchContentException cause) {
                throw cause;
            } catch (Exception cause) {
                throw new FetchContentException("config: %s download occur error!".formatted(getId()), cause);
            }

        }, thing.executor());
    }

    @Override
    public CompletableFuture<String> getContent() {
        return ofNullable(futureRef.get())
                .orElseGet(() -> {
                    synchronized (this) {
                        return ofNullable(futureRef.get())
                                .orElseGet(() -> {
                                    final var future = asyncFetchContent();
                                    futureRef.set(future);
                                    return future;
                                });
                    }
                });

    }

}
