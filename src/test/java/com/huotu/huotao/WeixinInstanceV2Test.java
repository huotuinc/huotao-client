package com.huotu.huotao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by CJ on 2017/2/25.
 */
public class WeixinInstanceV2Test {

    private static final Log log = LogFactory.getLog(WeixinInstanceV2Test.class);

    @Test
    public void go() throws IOException, BackingStoreException {
        log.info("test");
        Ini ini = new Ini(new File(System.getProperty("user.home")+"/.WXTest.ini"));
        IniPreferences preferences = new IniPreferences(ini);

        BasicCookieStore basicCookieStore = new BasicCookieStore();
        for(String name:preferences.childrenNames()){
            if (name.startsWith("Cookie_")){
                Preferences cookiePre = preferences.node(name);
                BasicClientCookie cookie =  new BasicClientCookie(cookiePre.get("Name",null),cookiePre.get("Value",null));
                cookie.setPath(cookiePre.get("Path",null));
                cookie.setDomain(cookiePre.get("Domain",null));
                basicCookieStore.addCookie(cookie);
            }
        }

        WeixinInstanceV2 instace = new WeixinInstanceV2(ini.get("Common","WeixinRouteHost")
                ,ini.get("Common","_session_id")
                ,ini.get("Common","Pass_Ticket")
                ,ini.get("Common","SKey")
                ,basicCookieStore
        );

        instace.sendImageMessage("Guo Childe",new ClassPathResource("/test.png"));
    }

}