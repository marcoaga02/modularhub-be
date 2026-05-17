package com.marcoaga02.modularhub.modules.usermanagement.exception;

import com.marcoaga02.modularhub.modules.usermanagement.constant.UserManagementExceptionCodes;
import com.marcoaga02.modularhub.shared.exception.BadRequestException;

public class UserAlreadyExistsException extends BadRequestException {

    public UserAlreadyExistsException(String taxIdNumber) {
        super(UserManagementExceptionCodes.USER_ALREADY_EXISTS,
                String.format("User with taxIdNumber '%s' already exists", taxIdNumber));
    }
}
