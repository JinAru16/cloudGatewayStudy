package com.mi.gateway.common;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Getter
public class WhiteListPath {

    public final String[] whiteList = {
            "/users/api/auth/login",
    };

    // StripPrefix 이후 기준 (GlobalFilter용)
    private final String[] strippedWhiteList = Arrays.stream(whiteList)
            .map(path -> path.replaceFirst("^/users", ""))
            .toArray(String[]::new);



}
