package com.huotu.huotao;

import com.huotu.huotao.exception.LoginRequiredException;
import com.huotu.huotao.http.IOFunction;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by CJ on 2017/2/25.
 */
public class WeixinInstanceV2 {

    private final String routeHost;
    private final String sessionId;
    private final String ticket;
    private final String key;
    private final CloseableHttpClient client;

    private JsonNode contactsList;

    public WeixinInstanceV2(String routeHost, String sessionId, String ticket, String key, CookieStore cookieStore) {
        this.routeHost = routeHost;
        this.sessionId = sessionId;
        this.ticket = ticket;
        this.key = key;
        this.client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .setDefaultHeaders(Collections.singleton(new BasicHeader("UserAgent","Mozilla/5.0")))
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
    private JsonNode getContacts() throws IOException {
        JsonNode all = httpGetBin("webwxgetcontact", input
                -> {
            JsonNode result = mapper.readTree(input.getEntity().getContent());
            JsonNode base = result.get("BaseResponse");
            if (base.get("Ret").asInt()>0)
                throw new LoginRequiredException();
            return result;
        });
        contactsList = all;
        return all;
    }

    private <T> T httpGetBin(String binUri, IOFunction<HttpResponse, T> function) throws IOException {
        HttpGet get = new HttpGet("https://"+routeHost+"/cgi-bin/mmwebwx-bin/"+binUri);
        return http(get,function);
    }

    private <T> T http(HttpUriRequest request, IOFunction<HttpResponse, T> function) throws IOException {
        try(CloseableHttpResponse response=client.execute(request)){
            return function.apply(response);
        }
    }

    public void sendImageMessage(String nickName, ClassPathResource resource) throws IOException {
        String to = toUserName(nickName);
        System.out.println(to);
    }
}
