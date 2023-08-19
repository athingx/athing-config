package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigureOption;
import io.github.athingx.athing.config.thing.impl.ThingConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpReplyException;
import io.github.athingx.athing.thing.api.op.OpRequest;
import io.github.athingx.athing.thing.api.op.ThingOpBind;
import io.github.athingx.athing.thing.api.op.ThingOpCaller;
import io.github.athingx.athing.thing.api.op.function.OpFunction;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.config.thing.ThingConfig.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingBytesToJson;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingJsonToOpReply;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingThingConfigPuller implements OpBinding<ThingOpCaller<Pull, ThingConfig>> {

    private final ThingConfigureOption option;

    public OpBindingThingConfigPuller(ThingConfigureOption option) {
        this.option = option;
    }

    @Override
    public CompletableFuture<ThingOpCaller<Pull, ThingConfig>> bind(Thing thing) {
        return thing.op().bind("/sys/%s/thing/config/get_reply".formatted(thing.path().toURN()))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpReply(Meta.class))
                .caller(new ThingOpBind.Option(), OpFunction.identity())
                .thenApply(caller -> caller
                        .route(pull -> "/sys/%s/thing/config/get".formatted(thing.path().toURN()))
                        .<Pull>compose(pull -> new OpRequest<>(thing.op().genToken(), "thing.config.get", pull))
                        .then(reply -> reply.handle(data -> new ThingConfigImpl(thing, option, PRODUCT, data)))

                );
    }

}
