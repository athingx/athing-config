package io.github.athingx.athing.config.thing.test;

import io.github.athingx.athing.config.thing.Config;
import io.github.athingx.athing.config.thing.ConfigListener;
import io.github.athingx.athing.config.thing.Scope;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class ThingConfigTestCase extends ThingConfigSupport {

    @Test
    public void test$thing$config$fetch() throws Exception {
        final var reply = thingConfig.fetch(Scope.PRODUCT).get();
        Assert.assertNotNull(reply);
        Assert.assertTrue(reply.isOk());
        Assert.assertNotNull(reply.token());
        Assert.assertNotNull(reply.data());
        final Config config = reply.data();
        Assert.assertNotNull(config.getConfigId());
        final String content = config.getContent().get();
        Assert.assertNotNull(content);
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
            thingConfig.appendListener(listener);
            thingConfig.update(Scope.PRODUCT).get();
            final Config config = queue.take();
            Assert.assertNotNull(config);
            Assert.assertNotNull(config.getConfigId());
            final String content = config.getContent().get();
            Assert.assertNotNull(content);
        } finally {
            thingConfig.removeListener(listener);
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
            thingConfig.appendListener(listener);
            final Config config = queue.take();
            Assert.assertNotNull(config);
            Assert.assertNotNull(config.getConfigId());
            final String content = config.getContent().get();
            Assert.assertNotNull(content);
        } finally {
            thingConfig.removeListener(listener);
        }
    }

}
