package io.github.athingx.athing.config.thing.impl;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigListener;
import io.github.athingx.athing.config.thing.ThingConfigurator;
import io.github.athingx.athing.config.thing.ThingConfigureOption;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.*;
import io.github.athingx.athing.thing.api.op.function.OpFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.common.ThingCodes.REQUEST_ERROR;
import static io.github.athingx.athing.config.thing.ThingConfig.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 设备配置实现
 */
public class ThingConfiguratorImpl implements ThingConfigurator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingConfigListener listener;

    private final ThingOpCaller<Pull, OpReply<ThingConfig>> puller;

    public ThingConfiguratorImpl(final ThingConfigListener listener,
                                 final ThingOpCaller<Pull, OpReply<ThingConfig>> puller) {
        this.listener = listener;
        this.puller = puller;
    }

    @Override
    public CompletableFuture<Void> update(ThingConfig.Scope scope) {
        return fetch(scope).thenAccept(reply -> listener.apply(reply.data()));
    }

    @Override
    public CompletableFuture<OpReply<ThingConfig>> fetch(ThingConfig.Scope scope) {
        return puller.call(new Pull(scope));
    }

}
