package com.pz.auth.functionalInterfaces;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author pz
 * @version 2.3
 * @E-mail 2919274153@qq.com
 * @date 2018-5-17 14:59:56
 */
@FunctionalInterface
public interface MygetRoles<V extends String> {
        Map<V, V> roleOrAuth();
}

