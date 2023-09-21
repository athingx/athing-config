package io.github.athingx.athing.config.thing;

import io.github.athingx.athing.thing.api.plugin.ThingPlugin;

import java.util.concurrent.CompletableFuture;

/**
 * 设备配置
 */
public interface ThingConfig extends ThingPlugin {

    String THING_CONFIG_ID = "athingx.thing.config";

    /**
     * 更新最新配置
     *
     * @param scope 配置范围
     */
    CompletableFuture<Void> update(Config.Scope scope);

    /**
     * 拉取最新配置
     *
     * @param scope 配置范围
     * @return 拉取操作
     */
    CompletableFuture<Config> fetch(Config.Scope scope);

    /**
     * 应用配置
     *
     * @param config 配置
     */
    void apply(Config config);

}
