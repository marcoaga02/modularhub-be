package com.marcoaga02.modularhub.shared.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class IdentityUserCreateRequestDTO {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Boolean enabled;

    private Boolean emailVerified;

    private String password;

    private List<String> groupIds;

}
