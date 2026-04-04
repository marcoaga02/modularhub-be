package com.marcoaga02.modularhub.modules.usermanagement.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserResponseDTO {

    private String id;

    private String firstname;

    private String lastname;

    private String gender;

    private LanguageResponseDTO language;

    private String mobileNumber;

    private String taxIdNumber;

    private String email;

    private Boolean enabled;

    // TODO aggiungere altri campi dopo keycloak e anche Audit

}
