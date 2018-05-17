package com.pz.auth.functionalInterfaces;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@FunctionalInterface
public interface MygetRoles<V extends String> {
        Map<V, V> roleOrAuth();
}

