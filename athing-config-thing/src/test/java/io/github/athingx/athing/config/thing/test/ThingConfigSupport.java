package io.github.athingx.athing.config.thing.test;

import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigInstaller;
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
    protected static volatile ThingConfig thingConfig;

    protected static final Set<ConfigListener> listeners = new LinkedHashSet<>();

    @BeforeClass
    public static void _before() throws Exception {

        thing = new ThingBuilder(new ThingPath(PRODUCT_ID, THING_ID))
                .client(new DefaultMqttClientFactory()
                        .remote(THING_REMOTE)
                        .secret(THING_SECRET))
                .build();

        thing.plugins().install(
                new ThingConfigInstaller()
                        .listener(config -> listeners.forEach(listener -> listener.apply(config)))
        );

        thingConfig = thing.plugins().depends(ThingConfig.THING_CONFIG_ID, ThingConfig.class)
                .get();

    }

    @AfterClass
    public static void _after() {
        thing.destroy();
    }

}
