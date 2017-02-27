package com.huotu.huotao.http.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by CJ on 2017/2/27.
 */
@Data
public class TextMessage extends Message {
    @JsonProperty("Content")
    private final String content;

    public TextMessage(String from, String to, String content) {
        super(from, to);
        this.content = content;
    }

    @Override
    public int getType() {
        return 1;
    }
}
