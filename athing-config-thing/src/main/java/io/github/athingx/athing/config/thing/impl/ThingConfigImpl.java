package io.github.athingx.athing.config.thing.impl;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.Scope;
import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpCall;
import io.github.athingx.athing.thing.api.op.OpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.CompletableFutureFn.whenCompleted;

/**
 * 设备配置实现
 */
public class ThingConfigImpl implements ThingConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final Set<ConfigListener> listeners;
    private final OpCall<Pull, OpReply<Config>> pullCall;

    public ThingConfigImpl(final Thing thing,
                           final Set<ConfigListener> listeners,
                           final OpCall<Pull, OpReply<Config>> pullCall) {
        this.thing = thing;
        this.listeners = listeners;
        this.pullCall = pullCall;
    }

    @Override
    public void appendListener(ConfigListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ConfigListener listener) {
        listeners.remove(listener);
    }

    @Override
    public CompletableFuture<Void> update(Scope scope) {
        return fetch(scope)
                .thenAccept(reply -> listeners.forEach(listener -> listener.apply(reply.data())));
    }

    @Override
    public CompletableFuture<OpReply<Config>> fetch(Scope scope) {
        final var token = thing.op().genToken();
        return pullCall
                .calling("/sys/%s/thing/config/get".formatted(thing.path().toURN()), new Pull(token))
                .whenComplete(whenCompleted(
                        (v) -> logger.debug("{}/config/fetch success, scope={};config-id={};", thing.path(), scope, v.data().getConfigId()),
                        (ex) -> logger.warn("{}/config/fetch failure, scope={}", thing.path(), scope)
                ));
    }

}
