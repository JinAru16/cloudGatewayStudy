package com.mi.gateway.common.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomTokenException extends AuthenticationException {
    public CustomTokenException(String msg) {
        super(msg);
    }
}
