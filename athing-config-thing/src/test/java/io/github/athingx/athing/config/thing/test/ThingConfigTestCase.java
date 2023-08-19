package io.github.athingx.athing.config.thing.test;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.ConfigListener;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class ThingConfigTestCase extends ThingConfigSupport {

    @Test
    public void test$thing$config$fetch() throws Exception {
        final var config = thingConfig.fetch(Config.Scope.PRODUCT).get();
        Assert.assertNotNull(config);
        Assert.assertNotNull(config.getId());
        final String content = config.getContent().get();
        Assert.assertNotNull(content);
        Assert.assertFalse(content.isBlank());
        Assert.assertEquals(Config.Scope.PRODUCT, config.getScope());
    }

    @Test
    public void test$thing$config$update() throws Exception {
        final var queue = new LinkedBlockingQueue<Config>();
        final var listener = new ConfigListener() {

            @Override
            public void apply(Config config) {
                while (true) {
                    if (queue.offer(config)) {
                        break;
                    }
                }
            }
        };
        try {
            listeners.add(listener);
            thingConfig.update(Config.Scope.PRODUCT).get();
            final Config config = queue.take();
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
        final var queue = new LinkedBlockingQueue<Config>();
        final var listener = new ConfigListener() {

            @Override
            public void apply(Config config) {
                while (true) {
                    if (queue.offer(config)) {
                        break;
                    }
                }
            }
        };
        try {
            listeners.add(listener);
            final Config config = queue.take();
            Assert.assertNotNull(config);
            Assert.assertNotNull(config.getId());
            final String content = config.getContent().get();
            Assert.assertNotNull(content);
        } finally {
            listeners.remove(listener);
        }
    }

}
