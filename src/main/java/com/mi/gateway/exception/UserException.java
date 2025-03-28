package com.mi.gateway.exception;


public class UserException extends RuntimeException {

    protected Object[] args;

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Object[] args) {
        super(message);
        if ( args != null ) {
            this.args = args.clone();
        }
    }


}
