package io.github.athingx.athing.config.thing.impl.binder;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.builder.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.ConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpCall;
import io.github.athingx.athing.thing.api.op.OpGroupBinder;
import io.github.athingx.athing.thing.api.op.OpGroupBinding;
import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.config.thing.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.function.ThingFn.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class PullOpBinder implements OpGroupBinder<OpCall<Pull, OpReply<Config>>> {

    private final Thing thing;
    private final ThingConfigOption option;

    public PullOpBinder(Thing thing, ThingConfigOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCall<Pull, OpReply<Config>>> bindFor(OpGroupBinding group) {
        return group.binding("/sys/%s/thing/config/get_reply".formatted(thing.path().toURN()))
                .map(mappingByteToJson(UTF_8))
                .map(mappingJsonToOpReply(Meta.class))
                .map((topic, reply) -> OpReply.reply(
                        reply.token(),
                        reply.code(),
                        reply.desc(),
                        (Config) new ConfigImpl(thing, reply.data(), PRODUCT, option)
                ))
                .call(identity());
    }

}
