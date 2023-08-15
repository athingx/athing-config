package io.github.athingx.athing.config.thing.impl.domain;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.config.thing.ThingConfig;

/**
 * 拉取配置
 */
public record Pull(
        @SerializedName("configScope") String scope,
        @SerializedName("getType") String type) {


    public Pull(ThingConfig.Scope scope) {
        this(scope.name().toLowerCase(), "file");
    }

}
