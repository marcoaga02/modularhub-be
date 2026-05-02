package com.marcoaga02.modularhub.shared.exception;

import lombok.Getter;

@Getter
public class IdentityProviderException extends RuntimeException {
    private final int statusCode;

    public IdentityProviderException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

}
