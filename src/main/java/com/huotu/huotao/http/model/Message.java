package com.huotu.huotao.http.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by CJ on 2017/2/27.
 */
@Data
public abstract class Message {
    @JsonProperty("ClientMediaId")
    private final String clientMediaId;
    @JsonProperty("FromUserName")
    private final String from;
    @JsonProperty("ToUserName")
    private final String to;
    @JsonProperty("LocalID")
    private final long LocalID = System.currentTimeMillis();
    @JsonProperty("Type")
    public abstract int getType();

    public Message(String from, String to) {
        this.clientMediaId = String.valueOf(System.currentTimeMillis());
        this.from = from;
        this.to = to;
    }
}
