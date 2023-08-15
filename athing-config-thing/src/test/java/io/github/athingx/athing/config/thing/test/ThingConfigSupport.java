package io.github.athingx.athing.config.thing.test;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigListener;
import io.github.athingx.athing.config.thing.ThingConfigurator;
import io.github.athingx.athing.config.thing.ThingConfigureBuilder;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.ThingPath;
import io.github.athingx.athing.thing.builder.ThingBuilder;
import io.github.athingx.athing.thing.builder.client.DefaultMqttClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.LinkedHashSet;
import java.util.Set;

public class ThingConfigSupport implements LoadingProperties {

    protected static volatile Thing thing;
    protected static volatile ThingConfigurator thingConfigurator;

    protected static final Set<ThingConfigListener> listeners = new LinkedHashSet<>();

    @BeforeClass
    public static void _before() throws Exception {

        thing = new ThingBuilder(new ThingPath(PRODUCT_ID, THING_ID))
                .client(new DefaultMqttClientFactory()
                        .remote(THING_REMOTE)
                        .secret(THING_SECRET))
                .build();

        thingConfigurator = new ThingConfigureBuilder()
                .listener(new ThingConfigListener() {
                    @Override
                    public void apply(ThingConfig config) {
                        listeners.forEach(listener -> listener.apply(config));
                    }
                })
                .build(thing)
                .get();

    }

    @AfterClass
    public static void _after() {
        thing.destroy();
    }

}
