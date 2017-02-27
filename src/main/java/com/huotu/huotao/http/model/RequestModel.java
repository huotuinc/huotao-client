package com.huotu.huotao.http.model;

import com.huotu.huotao.WeixinInstanceV2;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by CJ on 2017/2/27.
 */
@Data
public class RequestModel {
    @JsonProperty("BaseRequest")
    protected final BaseRequest baseRequest;

    public RequestModel(WeixinInstanceV2 instance) {
        baseRequest = new BaseRequest(instance);
    }
}
