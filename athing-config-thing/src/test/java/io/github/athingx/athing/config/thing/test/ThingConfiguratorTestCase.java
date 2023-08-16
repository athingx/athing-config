package io.github.athingx.athing.config.thing.test;

import io.github.athingx.athing.config.thing.ThingConfig;
import io.github.athingx.athing.config.thing.ThingConfigListener;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class ThingConfiguratorTestCase extends ThingConfigSupport {

    @Test
    public void test$thing$config$fetch() throws Exception {
        final var config = thingConfigurator.fetch(ThingConfig.Scope.PRODUCT).get();
        Assert.assertNotNull(config);
        Assert.assertNotNull(config.getId());
        final String content = config.getContent().get();
        Assert.assertNotNull(content);
        Assert.assertFalse(content.isBlank());
        Assert.assertEquals(ThingConfig.Scope.PRODUCT, config.getScope());
    }

    @Test
    public void test$thing$config$update() throws Exception {
        final var queue = new LinkedBlockingQueue<ThingConfig>();
        final var listener = new ThingConfigListener() {

            @Override
            public void apply(ThingConfig config) {
                while (true) {
                    if (queue.offer(config)) {
                        break;
                    }
                }
            }
        };
        try {
            listeners.add(listener);
            thingConfigurator.update(ThingConfig.Scope.PRODUCT).get();
            final ThingConfig config = queue.take();
            Assert.assertNotNull(config);
            Assert.assertNotNull(config.getId());
            final String content = config.getContent().get();
            Assert.assertNotNull(content);
        } finally {
            listeners.remove(listener);
        }
    }


    /**
     * 用于手工执行
     */
    @Ignore
    @Test
    public void test$thing$config$push() throws Exception {
        final var queue = new LinkedBlockingQueue<ThingConfig>();
        final var listener = new ThingConfigListener() {

            @Override
            public void apply(ThingConfig config) {
                while (true) {
                    if (queue.offer(config)) {
                        break;
                    }
                }
            }
        };
        try {
            listeners.add(listener);
            final ThingConfig config = queue.take();
            Assert.assertNotNull(config);
            Assert.assertNotNull(config.getId());
            final String content = config.getContent().get();
            Assert.assertNotNull(content);
        } finally {
            listeners.remove(listener);
        }
    }

}
