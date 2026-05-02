package com.marcoaga02.modularhub.shared.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private String identityId;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private Set<String> roles;

    private AccountPreferencesResponseDTO preferences;

}
