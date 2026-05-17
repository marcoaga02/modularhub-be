package com.marcoaga02.modularhub.shared.exception;

import com.marcoaga02.modularhub.shared.constant.ExceptionCodes;

public class AccountPreferencesNotFoundException extends NotFoundException {

    public AccountPreferencesNotFoundException(String identityId) {
        super(ExceptionCodes.ACCOUNT_PREFERENCES_NOT_FOUND,
                String.format("AccountPreferences for identityId '%s' not found", identityId));
    }

}
