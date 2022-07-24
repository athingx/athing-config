package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.builder.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.Codes;
import io.github.athingx.athing.config.thing.impl.ConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Push;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBinder;
import io.github.athingx.athing.thing.api.op.OpBinding;
import io.github.athingx.athing.thing.api.op.OpGroupBind;
import io.github.athingx.athing.thing.api.op.OpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.config.thing.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.function.ThingFnMapJson.mappingJsonFromBytes;
import static io.github.athingx.athing.thing.api.function.ThingFnMapJson.mappingJsonToType;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BindingForPush implements OpBinding<OpBinder>, Codes {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final ThingConfigOption option;
    private final Set<ConfigListener> listeners;

    public BindingForPush(Thing thing, ThingConfigOption option, Set<ConfigListener> listeners) {
        this.thing = thing;
        this.option = option;
        this.listeners = listeners;
    }

    @Override
    public CompletableFuture<OpBinder> binding(OpGroupBind group) {
        return group.bind("/sys/%s/thing/config/push".formatted(thing.path().toURN()))
                .map(mappingJsonFromBytes(UTF_8))
                .map(mappingJsonToType(Push.class))
                .bind((topic, push) -> {

                    final var config = new ConfigImpl(thing, push.meta(), PRODUCT, option);
                    final var rTopic = topic + "_reply";
                    final var path = thing.path();
                    final var token = push.token();
                    final var configId = push.meta().configId();

                    // 如果没配置监听器，则返回配置失败
                    if (listeners.isEmpty()) {
                        logger.warn("{}/config/push give up: none-listener, token={};config-id={};", path, token, configId);
                        thing.op().data(rTopic, OpReply.reply(
                                token,
                                ALINK_REPLY_PROCESS_ERROR,
                                "none-listener"
                        ));
                    }

                    // 如果有配置监听器，则进行监听
                    try {

                        listeners.forEach(listener -> listener.apply(config));
                        logger.warn("{}/config/push apply success, token={};config-id={};", path, token, configId);
                        thing.op().data(rTopic, OpReply.success(token));

                    } catch (Throwable cause) {
                        logger.warn("{}/config/push apply failure, token={};config-id={};", path, token, configId, cause);
                        thing.op().data(rTopic, OpReply.reply(
                                token,
                                ALINK_REPLY_PROCESS_ERROR,
                                cause.getLocalizedMessage()
                        ));
                    }

                });
    }

}
