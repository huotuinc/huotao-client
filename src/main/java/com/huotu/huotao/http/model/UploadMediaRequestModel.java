package com.huotu.huotao.http.model;

import com.huotu.huotao.WeixinInstanceV2;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CJ on 2017/2/27.
 */
@Data
public class UploadMediaRequestModel extends RequestModel {
    @JsonProperty("ClientMediaId")
    private final String clientMediaId;
    @JsonProperty("TotalLen")
    private final long total;
    @JsonProperty("DataLen")
    private final long length;
    @JsonProperty("StartPos")
    private final long start = 0;
    @JsonProperty("MediaType")
    private final int type  = 4;
    @JsonProperty("UploadType")
    private final int uploadType = 2;
    @JsonProperty("FromUserName")
    private final String from;
    @JsonProperty("ToUserName")
    private final String to;
    @JsonProperty("FileMd5")
    private final String md5;

    public UploadMediaRequestModel(WeixinInstanceV2 instance, Resource resource, String from, String to) throws IOException {
        super(instance);
        clientMediaId = String.valueOf(System.currentTimeMillis());
        total = resource.contentLength();
length  = total;
this.from = from;
this.to = to;
        md5 = DigestUtils.md5Hex(resource.getInputStream());
    }
}
