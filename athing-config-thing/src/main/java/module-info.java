module athing.config.thing {

    exports io.github.athingx.athing.config.thing;
    exports io.github.athingx.athing.config.thing.builder;

    opens io.github.athingx.athing.config.thing.impl.domain;

    requires athing.thing.api;
    requires org.slf4j;
}