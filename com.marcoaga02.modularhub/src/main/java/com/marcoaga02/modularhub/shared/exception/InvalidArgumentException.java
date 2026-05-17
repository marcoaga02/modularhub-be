package com.marcoaga02.modularhub.shared.exception;

import com.marcoaga02.modularhub.shared.constant.ExceptionCodes;
import org.springframework.http.HttpStatus;

public class InvalidArgumentException extends ApplicationException {

    public InvalidArgumentException(String logMessage) {
        super(ExceptionCodes.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, logMessage);
    }
}
