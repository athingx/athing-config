package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.ConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBinding;
import io.github.athingx.athing.thing.api.op.OpConsumer;
import io.github.athingx.athing.thing.api.op.OpReply;
import io.github.athingx.athing.thing.api.op.OpReplyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.common.ThingCodes.REQUEST_ERROR;
import static io.github.athingx.athing.common.util.ExceptionUtils.optionalCauseBy;
import static io.github.athingx.athing.config.thing.Config.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.op.Codec.codecBytesToJson;
import static io.github.athingx.athing.thing.api.op.Codec.codecJsonToOpServices;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingForPusher implements OpBinding<OpConsumer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingConfigOption option;
    private final ConfigListener listener;

    public OpBindingForPusher(ThingConfigOption option, ConfigListener listener) {
        this.option = option;
        this.listener = listener;
    }

    @Override
    public CompletableFuture<OpConsumer> bind(Thing thing) {

        return thing.op()
                .codec(codecBytesToJson(UTF_8))
                .codec(codecJsonToOpServices(Meta.class, Object.class))
                .self(op -> op.consumer("/sys/%s/thing/config/push".formatted(thing.path().toURN()), (topic, request) -> {

                    final var path = thing.path();
                    final var meta = request.params();
                    final var token = request.token();
                    final var rTopic = topic + "_reply";
                    final var config = new ConfigImpl(thing, option, PRODUCT, meta);

                    try {

                        // 对配置变更监听器进行通知，如果任何一个监听器失败，则配置变更失败
                        listener.apply(config);
                        logger.debug("{}/config/push apply success, token={};config-id={};", path, token, meta.id());

                        // 回复配置变更成功
                        op.post(rTopic, OpReply.succeed(token))
                                .whenComplete((v, ex) -> logger.debug("{}/config/push reply completed, token={};config-id={};", path, token, meta.id(), ex));
                    }

                    // 处理变更失败
                    catch (Throwable cause) {

                        logger.warn("{}/config/push apply failure, token={};config-id={};", path, token, meta.id(), cause);

                        final var reply = optionalCauseBy(cause, OpReplyException.class)
                                .map(OpReply::fail)
                                .orElseGet(() -> OpReply.fail(token, REQUEST_ERROR, cause.getLocalizedMessage()));

                        // 回复配置变更失败
                        op.post(rTopic, reply)
                                .whenComplete((v, ex) -> logger.debug("{}/config/push reply completed, token={};config-id={};", path, token, meta.id(), ex));

                    }
                }));
    }

}
