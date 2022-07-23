package io.github.athingx.athing.config.thing;

import java.util.concurrent.CompletableFuture;

/**
 * 配置
 */
public interface Config {

    /**
     * 获取配置ID
     *
     * @return 配置ID
     */
    String getConfigId();

    /**
     * 获取配置范围
     *
     * @return 配置范围
     */
    Scope getScope();

    /**
     * 获取配置内容
     *
     * @return 获取凭证
     */
    CompletableFuture<String> getContent();

}
