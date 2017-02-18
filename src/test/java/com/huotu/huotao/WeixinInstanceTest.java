package com.huotu.huotao;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author CJ
 */
public class WeixinInstanceTest {

    @Test
    public void login() throws IOException, InvocationTargetException, InterruptedException {
        WeixinInstance instance = new WeixinInstance();
//        instance.login();
        instance.sendTextMessage("Guo Childe", "hello world, at " + new Date());
    }

}