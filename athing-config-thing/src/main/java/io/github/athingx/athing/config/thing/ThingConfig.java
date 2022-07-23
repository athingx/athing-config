package io.github.athingx.athing.config.thing;

import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.concurrent.CompletableFuture;

/**
 * 设备配置
 */
public interface ThingConfig {

    /**
     * 添加监听器
     *
     * @param listener 配置监听器
     */
    void appendListener(ConfigListener listener);

    /**
     * 移除监听器
     *
     * @param listener 配置监听器
     */
    void removeListener(ConfigListener listener);

    /**
     * 更新最新配置
     *
     * @param scope 配置范围
     */
    CompletableFuture<Void> update(Scope scope);

    /**
     * 拉取最新配置
     *
     * @param scope 配置范围
     * @return 配置Future
     */
    CompletableFuture<OpReply<Config>> fetch(Scope scope);

}
