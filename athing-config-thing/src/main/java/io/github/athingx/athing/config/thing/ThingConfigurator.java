package io.github.athingx.athing.config.thing;

import java.util.concurrent.CompletableFuture;

/**
 * 设备配置
 */
public interface ThingConfigurator {

    /**
     * 更新最新配置
     *
     * @param scope 配置范围
     */
    CompletableFuture<Void> update(ThingConfig.Scope scope);

    /**
     * 拉取最新配置
     *
     * @param scope 配置范围
     * @return 拉取操作
     */
    CompletableFuture<ThingConfig> fetch(ThingConfig.Scope scope);

}
