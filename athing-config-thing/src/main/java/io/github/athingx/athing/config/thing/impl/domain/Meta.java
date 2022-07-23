package io.github.athingx.athing.config.thing.impl.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 配置元数据
 */
public record Meta(
        @SerializedName("configId") String configId,
        @SerializedName("sign") String configCHS,
        @SerializedName("url") String configURL
) {

}
