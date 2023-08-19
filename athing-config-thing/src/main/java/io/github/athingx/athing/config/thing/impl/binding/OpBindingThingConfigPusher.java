package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.ConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpReply;
import io.github.athingx.athing.thing.api.op.OpReplyException;
import io.github.athingx.athing.thing.api.op.ThingOpBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.common.ThingCodes.REQUEST_ERROR;
import static io.github.athingx.athing.config.thing.Config.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingBytesToJson;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingJsonToOpRequest;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingThingConfigPusher implements OpBinding<ThingOpBinder> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingConfigOption option;
    private final ConfigListener listener;

    public OpBindingThingConfigPusher(ThingConfigOption option, ConfigListener listener) {
        this.option = option;
        this.listener = listener;
    }

    @Override
    public CompletableFuture<ThingOpBinder> bind(Thing thing) {
        return thing.op().bind("/sys/%s/thing/config/push".formatted(thing.path().toURN()))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpRequest(Meta.class))
                .consumer((topic, request) -> {

                    final var path = thing.path();
                    final var meta = request.params();
                    final var token = request.token();
                    final var rTopic = topic + "_reply";
                    logger.debug("{}/config/push received! token={};config-id={};", path, token, meta.id());

                    try {

                        // 对配置变更监听器进行通知，如果任何一个监听器失败，则配置变更失败
                        final var config = new ConfigImpl(thing, option, PRODUCT, meta);
                        listener.apply(config);
                        logger.debug("{}/config/push apply success, token={};config-id={};", path, token, meta.id());

                        // 回复配置变更成功
                        thing.op().post(rTopic, OpReply.succeed(token))
                                .whenComplete((v, ex) ->
                                        logger.debug("{}/config/push reply completed, token={};config-id={};", path, token, meta.id(), ex));
                    }

                    // 处理变更失败
                    catch (Throwable cause) {

                        logger.warn("{}/config/push apply failure, token={};config-id={};", path, token, meta.id(), cause);

                        final OpReply<?> reply;
                        if (cause instanceof OpReplyException orCause) {
                            reply = OpReply.fail(orCause);
                        } else {
                            reply = OpReply.fail(token, REQUEST_ERROR, cause.getLocalizedMessage());
                        }

                        // 回复配置变更失败
                        thing.op().post(rTopic, reply)
                                .whenComplete((v, ex) ->
                                        logger.debug("{}/config/push reply completed, token={};config-id={};", path, token, meta.id(), ex));
                    }

                });
    }

}
