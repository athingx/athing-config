package io.github.athingx.athing.config.thing.test;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.builder.ThingConfigBuilder;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.ThingPath;
import io.github.athingx.athing.thing.builder.ThingBuilder;
import io.github.athingx.athing.thing.builder.mqtt.AliyunMqttClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ThingConfigSupport implements LoadingProperties {

    protected static volatile Thing thing;
    protected static volatile ThingConfig thingConfig;

    @BeforeClass
    public static void _before() throws Exception {

        thing = new ThingBuilder(new ThingPath(PRODUCT_ID, THING_ID))
                .clientFactory(new AliyunMqttClientFactory()
                        .remote(THING_REMOTE)
                        .secret(THING_SECRET))
                .build();

        thingConfig = new ThingConfigBuilder()
                .build(thing)
                .get();

    }

    @AfterClass
    public static void _after() {
        thing.destroy();
    }

}
