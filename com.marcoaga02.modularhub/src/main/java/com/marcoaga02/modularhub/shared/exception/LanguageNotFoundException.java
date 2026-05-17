package com.marcoaga02.modularhub.shared.exception;

import com.marcoaga02.modularhub.shared.constant.ExceptionCodes;

public class LanguageNotFoundException extends NotFoundException {

    public LanguageNotFoundException(String uuid) {
        super(ExceptionCodes.LANGUAGE_NOT_FOUND, String.format("Language with uuid '%s' not found", uuid));
    }

}
