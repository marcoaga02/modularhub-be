package com.marcoaga02.modularhub.modules.usermanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private String id;

    private String firstname;

    private String lastname;

    private String gender;

    private LanguageResponseDTO language;

    private String mobileNumber;

    private String taxIdNumber;

    // TODO abilitare dopo implementazione keycloak
//    private String email;

//    private String username;

//    private GroupResponseDTO groups;

//    private Boolean enabled;

}
