package io.github.athingx.athing.config.thing;

/**
 * 配置监听器
 */
@FunctionalInterface
public interface ConfigListener {

    /**
     * 应用配置变更
     *
     * @param config 配置
     */
    void apply(Config config);

}
