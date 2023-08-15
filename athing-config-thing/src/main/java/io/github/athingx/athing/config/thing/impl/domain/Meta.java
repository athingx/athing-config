package io.github.athingx.athing.config.thing.impl.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 配置元数据
 */
public record Meta(
        @SerializedName("configId") String id,
        @SerializedName("sign") String sign,
        @SerializedName("url") String url
) {

}
