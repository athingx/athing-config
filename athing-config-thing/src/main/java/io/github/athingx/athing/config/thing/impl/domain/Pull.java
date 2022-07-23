package io.github.athingx.athing.config.thing.impl.domain;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.config.thing.Scope;
import io.github.athingx.athing.thing.api.op.OpData;

public class Pull implements OpData {

    @SerializedName("id")
    private final String token;
    @SerializedName("version")
    private final String version;
    @SerializedName("method")
    private final String method;
    @SerializedName("params")
    private final Param param;

    public Pull(String token) {
        this.token = token;
        this.version = "1.0";
        this.method = "thing.config.get";
        this.param = new Param(Scope.PRODUCT.name(), "file");
    }

    @Override
    public String token() {
        return token;
    }

    public String version() {
        return version;
    }

    public String method() {
        return method;
    }

    public Param param() {
        return param;
    }

    private record Param(
            @SerializedName("configScope") String scope,
            @SerializedName("getType") String type
    ) {

    }

}
