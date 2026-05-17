package com.marcoaga02.modularhub.shared.exception;

import com.marcoaga02.modularhub.shared.constant.ExceptionCodes;
import org.springframework.http.HttpStatus;

public class IdentityProviderException extends ApplicationException {

    public IdentityProviderException(int statusCode, String logMessage) {
        super(ExceptionCodes.IDENTITY_PROVIDER_ERROR, HttpStatus.valueOf(statusCode), logMessage);
    }

    public IdentityProviderException(int statusCode, String logMessage, Throwable cause) {
        super(ExceptionCodes.IDENTITY_PROVIDER_ERROR, HttpStatus.valueOf(statusCode), logMessage, cause);
    }
}
