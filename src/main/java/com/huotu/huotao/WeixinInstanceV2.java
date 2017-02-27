package com.huotu.huotao;

import com.huotu.huotao.exception.LoginRequiredException;
import com.huotu.huotao.http.IOFunction;
import com.huotu.huotao.http.model.BaseRequest;
import com.huotu.huotao.http.model.MediaMessage;
import com.huotu.huotao.http.model.RequestModel;
import com.huotu.huotao.http.model.UploadMediaRequestModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.function.Consumer;

/**
 * Created by CJ on 2017/2/25.
 */
public class WeixinInstanceV2 {

    private final String routeHost;
    @Getter
    private final String sessionId;
    @Getter
    private final String ticket;
    @Getter
    private final String key;
    @Getter
    private final String deviceId;
    private final CloseableHttpClient client;
    private final CookieStore cookieStore;
    private long uploadMediaSerialId;

    private JsonNode contactsList;
    private JsonNode initData;

    public WeixinInstanceV2(String routeHost, String sessionId, String ticket, String key, CookieStore cookieStore) {
        this.routeHost = routeHost;
        this.sessionId = sessionId;
        this.ticket = ticket;
        this.key = key;
        this.cookieStore = cookieStore;
        this.deviceId = "e" + RandomStringUtils.randomNumeric(15);
        this.client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
//                .setDefaultHeaders(Arrays.asList(new BasicHeader("Accept", "*/*")))
                .setUserAgent("Mozilla/5.0")
//                .disableContentCompression()
//                .setDefaultConnectionConfig(ConnectionConfig.custom().tim)
        .build();
    }

    private String toUserName(String nickName) throws IOException {
        if (contactsList == null)
            getContacts();
        JsonNode members = contactsList.get("MemberList");
        for (JsonNode contact:members){
            if (contact.get("NickName").asText().equals(nickName))
                return contact.get("UserName").asText();
        }
        return null;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonNode toJson(HttpResponse response) throws IOException {
        JsonNode result = mapper.readTree(response.getEntity().getContent());
        JsonNode base = result.get("BaseResponse");
        if (base.get("Ret").asInt()>0)
            throw new LoginRequiredException();
        return result;
    }
    private JsonNode getContacts() throws IOException {
        JsonNode all = httpGetBin("webwxgetcontact", this::toJson);
        contactsList = all;
        return all;
    }

    private <T> T httpGetBin(String binUri, IOFunction<HttpResponse, T> function) throws IOException {
        HttpGet get = new HttpGet("https://"+routeHost+"/cgi-bin/mmwebwx-bin/"+binUri);
        return http(get,function);
    }

    private <T> T httpPostBin(String binUri, Consumer<HttpPost> consumer, IOFunction<HttpResponse, T> function) throws IOException {
        HttpPost get = new HttpPost("https://"+routeHost+"/cgi-bin/mmwebwx-bin/"+binUri);
        consumer.accept(get);
        return http(get,function);
    }


    private <T> T http(HttpUriRequest request, IOFunction<HttpResponse, T> function) throws IOException {
        try(CloseableHttpResponse response=client.execute(request)){
            return function.apply(response);
        }
    }

    public void sendImageMessage(String nickName, String imageName, ClassPathResource resource) throws IOException {
        String to = toUserName(nickName);
        String from = myUserName();
        // from
        // UploadMediaRequestModel
        UploadMediaRequestModel model = new UploadMediaRequestModel(this,resource,from,to);

        String id = "WU_FILE_" + (uploadMediaSerialId++);

        String url = "https://file." + routeHost + "/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";
        HttpPost post = new HttpPost(url);
        String mineType = URLConnection.getFileNameMap().getContentTypeFor(imageName);
        DateFormat dateInstance = DateFormat.getDateTimeInstance();
        dateInstance.setTimeZone(TimeZone.getTimeZone("GTM"));
        post.setEntity(MultipartEntityBuilder.create()
                .addTextBody("id",id)
                .addTextBody("name",imageName)
                .addTextBody("type",mineType)
                .addTextBody("lastModifiedDate",dateInstance.format(new Date()) )
                .addTextBody("size",String.valueOf(model.getLength()))
                .addTextBody("mediatype","pic")
                .addTextBody("webwx_data_ticket",getCookieValue("webwx_data_ticket"))
                .addTextBody("pass_ticket",ticket)
                .addTextBody("uploadmediarequest",mapper.writeValueAsString(model))
                .addBinaryBody("filename", StreamUtils.copyToByteArray(resource.getInputStream()), ContentType.create(mineType),imageName)
                .build()
        );
        JsonNode result = http(post,this::toJson);
        String mediaId  =  result.get("MediaId").asText();

        // 发送消息
        httpPostBin("webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket=" + ticket, new Consumer<HttpPost>() {
            @Override
            @SneakyThrows(IOException.class)
            public void accept(HttpPost httpPost) {
                HashMap<String,Object> toPost = new HashMap<>();
                toPost.put("BaseRequest", new BaseRequest(WeixinInstanceV2.this));
                toPost.put("Scene",0);
                toPost.put("Msg", new MediaMessage(from,to,mediaId));

                httpPost.setEntity(
                        EntityBuilder.create()
                                .setBinary(mapper.writeValueAsBytes(toPost))
                                .build()
                );
            }
        },this::toJson);

    }

    private String myUserName() throws IOException {
        if (initData == null)
            init();
        return initData.get("User").get("UserName").asText();
    }

    private void init() throws IOException {
        initData = httpPostBin("webwxinit?r="+System.currentTimeMillis()+"&pass_ticket="+ticket, new java.util.function.Consumer<HttpPost>() {
            @Override
            @SneakyThrows(IOException.class)
            public void accept(HttpPost httpPost) {
                RequestModel request = new RequestModel(WeixinInstanceV2.this);
                httpPost.setEntity(
                        EntityBuilder.create()
                        .setBinary(mapper.writeValueAsBytes(request))
                        .build()
                );
            }
        }, this::toJson);
    }


    public String getCookieValue(String name) {
        for (Cookie cookie:cookieStore.getCookies()){
            if (cookie.getName().equals(name))
                return cookie.getValue();
        }
        return null;
    }
}
