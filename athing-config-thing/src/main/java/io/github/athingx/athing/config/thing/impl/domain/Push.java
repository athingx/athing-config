package io.github.athingx.athing.config.thing.impl.domain;

import com.google.gson.annotations.SerializedName;

public record Push(
        @SerializedName("id") String token,
        @SerializedName("params") Meta meta
) {
}
