package com.huotu.huotao.http;

import java.io.IOException;

/**
 * Created by CJ on 2017/2/25.
 */
@FunctionalInterface
public interface IOFunction<P,T> {

    T apply(P input) throws IOException;

}
