package com.mi.gateway.common;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WhiteListPath {

    public final String[] whiteList = {
            "/users/api/auth/login",
    };



}
