package com.marcoaga02.modularhub.shared.exception;

import com.marcoaga02.modularhub.shared.constant.ExceptionCodes;
import org.springframework.http.HttpStatus;

public class InternalStateException extends ApplicationException {

    public InternalStateException(String logMessage) {
        super(ExceptionCodes.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, logMessage);
    }
}
