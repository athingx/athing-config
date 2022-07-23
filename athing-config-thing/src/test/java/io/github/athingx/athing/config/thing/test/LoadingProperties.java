package io.github.athingx.athing.config.thing.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface LoadingProperties {

    Properties prop = new Properties() {{

        try (final InputStream is = new FileInputStream(System.getProperties().getProperty("athing-qatest.properties.file"))) {
            load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }};

    static String $(String name) {
        return prop.getProperty(name);
    }

    String PRODUCT_ID = $("athing.product.id");
    String THING_ID = $("athing.thing.id");
    String THING_SECRET = $("athing.thing.secret");
    String THING_REMOTE = $("athing.thing.server-url");

}
