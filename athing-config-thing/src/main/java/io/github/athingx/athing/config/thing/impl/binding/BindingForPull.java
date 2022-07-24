package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.builder.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.ConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBinding;
import io.github.athingx.athing.thing.api.op.OpCaller;
import io.github.athingx.athing.thing.api.op.OpGroupBind;
import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.config.thing.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.function.ThingFnMap.identity;
import static io.github.athingx.athing.thing.api.function.ThingFnMapJson.mappingJsonFromBytes;
import static io.github.athingx.athing.thing.api.function.ThingFnMapOpReply.mappingOpReplyFromJson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BindingForPull implements OpBinding<OpCaller<Pull, OpReply<Config>>> {

    private final Thing thing;
    private final ThingConfigOption option;

    public BindingForPull(Thing thing, ThingConfigOption option) {
        this.thing = thing;
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCaller<Pull, OpReply<Config>>> binding(OpGroupBind group) {
        return group.bind("/sys/%s/thing/config/get_reply".formatted(thing.path().toURN()))
                .map(mappingJsonFromBytes(UTF_8))
                .map(mappingOpReplyFromJson(Meta.class))
                .map((topic, reply) -> OpReply.reply(
                        reply.token(),
                        reply.code(),
                        reply.desc(),
                        (Config) new ConfigImpl(thing, reply.data(), PRODUCT, option)
                ))
                .call(identity());
    }

}
