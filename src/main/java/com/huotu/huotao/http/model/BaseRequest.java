package com.huotu.huotao.http.model;

import com.huotu.huotao.WeixinInstanceV2;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by CJ on 2017/2/27.
 */
@Data
public class BaseRequest {
    @JsonProperty("Uin")
    private final long unitId;
    @JsonProperty("Sid")
    private final String sessionId;
    @JsonProperty("Skey")
    private final String sessionKey;
    @JsonProperty("DeviceID")
    private final String deviceId;

    public BaseRequest(WeixinInstanceV2 instance) {
        sessionId = instance.getCookieValue("wxsid");
        sessionKey = instance.getKey();
        unitId = Long.parseLong(instance.getCookieValue("wxuin"));
        deviceId = instance.getDeviceId();
    }
}
