package com.huotu.huotao.http.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by CJ on 2017/2/27.
 */
@Data
public class MediaMessage extends Message {
    @JsonProperty("MediaId")
    private final String mediaId;

    public MediaMessage(String from, String to, String mediaId) {
        super(from, to);
        this.mediaId = mediaId;
    }

    @Override
    public int getType() {
        return 3;
    }
}
