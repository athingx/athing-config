package io.github.athingx.athing.config.thing;

import java.util.concurrent.CompletableFuture;

/**
 * 配置
 */
public interface ThingConfig {

    /**
     * 获取配置ID
     *
     * @return 配置ID
     */
    String getId();

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

    /**
     * 配置范围
     */
    enum Scope {

        /**
         * 产品级
         */
        PRODUCT

    }

}
