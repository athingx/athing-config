package io.github.athingx.athing.config.thing.impl.binding;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.ThingConfigOption;
import io.github.athingx.athing.config.thing.impl.ConfigImpl;
import io.github.athingx.athing.config.thing.impl.domain.Meta;
import io.github.athingx.athing.config.thing.impl.domain.Pull;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.Codec;
import io.github.athingx.athing.thing.api.op.OpBinding;
import io.github.athingx.athing.thing.api.op.OpCaller;
import io.github.athingx.athing.thing.api.op.OpRequest;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.config.thing.Config.Scope.PRODUCT;
import static io.github.athingx.athing.thing.api.op.Codec.codecBytesToJson;
import static io.github.athingx.athing.thing.api.op.Codec.codecJsonToOpCaller;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingForPuller implements OpBinding<OpCaller<Pull, Config>> {

    private final ThingConfigOption option;

    public OpBindingForPuller(ThingConfigOption option) {
        this.option = option;
    }

    @Override
    public CompletableFuture<OpCaller<Pull, Config>> bind(Thing thing) {
        return thing.op()
                .codec(codecBytesToJson(UTF_8))
                .codec(codecJsonToOpCaller(Pull.class, Meta.class))
                .caller("/sys/%s/thing/config/get_reply".formatted(thing.path().toURN()), Codec.none())
                .thenApply(caller -> caller
                        .topics("/sys/%s/thing/config/get".formatted(thing.path().toURN()))
                        .<Pull>compose(pull -> new OpRequest<>(
                                thing.op().genToken(),
                                "thing.config.get",
                                pull
                        ))
                        .then(reply -> reply.handle(data -> new ConfigImpl(thing, option, PRODUCT, data)))
                );
    }

}
