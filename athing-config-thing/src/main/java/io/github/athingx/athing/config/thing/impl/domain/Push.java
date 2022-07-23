package io.github.athingx.athing.config.thing.impl.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 推送配置
 *
 * @param token 令牌
 * @param meta  配置元数据
 */
public record Push(
        @SerializedName("id") String token,
        @SerializedName("params") Meta meta
) {
}
